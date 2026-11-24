package py.edu.uc.lp3.avera_tplp3_2026.service.impl;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ITEM_ESCUDO;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.avera_tplp3_2026.domain.Jugada;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.RechazoExternoException;
import py.edu.uc.lp3.avera_tplp3_2026.integration.Companero;
import py.edu.uc.lp3.avera_tplp3_2026.integration.ReglaExternaClient;
import py.edu.uc.lp3.avera_tplp3_2026.repository.JugadaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.SimulacionResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ValidacionExternaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ValidacionExternaResponse;
import py.edu.uc.lp3.avera_tplp3_2026.service.CondicionPreviaService;
import py.edu.uc.lp3.avera_tplp3_2026.service.EscenarioService;
import py.edu.uc.lp3.avera_tplp3_2026.service.SimulacionService;

@Service
public class EscenarioServiceImpl implements EscenarioService {

    private final JugadaRepository jugadaRepository;
    private final SimulacionService simulacionService;
    private final ReglaExternaClient reglaExternaClient;
    private final CondicionPreviaService condicionPreviaService;

    public EscenarioServiceImpl(JugadaRepository jugadaRepository,
                                SimulacionService simulacionService,
                                ReglaExternaClient reglaExternaClient,
                                CondicionPreviaService condicionPreviaService) {
        this.jugadaRepository = jugadaRepository;
        this.simulacionService = simulacionService;
        this.reglaExternaClient = reglaExternaClient;
        this.condicionPreviaService = condicionPreviaService;
    }

    @Override
    @Transactional
    public EscenarioResponse ejecutar(EscenarioRequest request) {
        if (request == null) {
            throw new EntidadInvalidaException("El cuerpo del escenario es obligatorio");
        }

        CondicionPreviaService.Participantes participantes =
                condicionPreviaService.validar(request.jugadorId(), request.monstruoId());
        PersonajeJugable jugador = participantes.jugador();
        Monstruo monstruo = participantes.monstruo();

        if (!monstruo.isHostil()) {
            throw new EntidadInvalidaException("El monstruo " + monstruo.getNombre() + " no es hostil");
        }
        if (request.potencia() <= 0 || request.potencia() > monstruo.getPotenciaExplosiva()) {
            throw new EntidadInvalidaException("La potencia debe estar entre 1 y "
                    + monstruo.getPotenciaExplosiva() + " para " + monstruo.getNombre());
        }

        double radio = request.radio() != null ? request.radio() : request.potencia();
        double distancia = request.distancia() != null
                ? request.distancia()
                : Math.abs(jugador.getAltura() - monstruo.getAltura());
        int escudos = (int) jugador.getInventario().stream()
                .filter(item -> item.equalsIgnoreCase(ITEM_ESCUDO))
                .count();
        ValidacionExternaRequest payload = new ValidacionExternaRequest(
                jugador.getNombre(), jugador.getVida(), escudos, monstruo.getNombre(),
                request.potencia(), radio, distancia, "explosion-validada");

        ValidacionExternaResponse decisionA2 = reglaExternaClient.validar(Companero.A2, payload);
        if (!decisionA2.aceptada()) {
            String motivoA2 = limpiarPrefijo(decisionA2.motivo(), "Rechazo de A2:");
            throw new RechazoExternoException("A2", 400,
                    decisionA2.codigo() != null ? decisionA2.codigo() : "REGLA_A2", motivoA2);
        }

        ValidacionExternaResponse decisionA3 = reglaExternaClient.validar(Companero.A3, payload);
        if (!decisionA3.aceptada()) {
            String motivoA3 = limpiarPrefijo(decisionA3.motivo(), "Rechazo de A3:");
            throw new RechazoExternoException("A3", 400,
                    decisionA3.codigo() != null ? decisionA3.codigo() : "REGLA_A3", motivoA3);
        }

        SimulacionResponse simulacion = simulacionService.simularExplosion(monstruo.getId(), request.potencia());
        SimulacionResponse.ResultadoEntidad resultadoJugador = simulacion.afectadas().stream()
                .filter(resultado -> resultado.id().equals(jugador.getId()))
                .findFirst()
                .orElse(null);
        int vidaDespues = resultadoJugador != null ? resultadoJugador.vidaDespues() : jugador.getVida();
        int escudosDelJugador = resultadoJugador != null ? resultadoJugador.escudosConsumidos() : 0;

        Jugada jugada = jugadaRepository.save(new Jugada(
                jugador.getId(), jugador.getNombre(), monstruo.getId(), monstruo.getNombre(),
                request.potencia(), escudosDelJugador, vidaDespues,
                true, true, request.motivo()));

        return new EscenarioResponse(
                jugada.getId(), jugador.getId(), monstruo.getId(), request.potencia(),
                true, true, escudosDelJugador, vidaDespues,
                "A2 y A3 aceptaron; la jugada quedó persistida");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jugada> listar() {
        return jugadaRepository.findAll();
    }

    private String limpiarPrefijo(String texto, String prefijo) {
        if (texto == null || texto.isBlank()) {
            return "Regla rechazada por el servicio externo";
        }
        String limpio = texto.trim();
        if (limpio.startsWith(prefijo)) {
            return limpio.substring(prefijo.length()).trim();
        }
        return limpio;
    }
}

package py.edu.uc.lp3.avera_tplp3_2026.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Explosion;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Jugada;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.repository.JugadaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.EscenarioRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.EscenarioResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.ValidacionExternaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.ValidacionExternaResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.integration.Companero;
import py.edu.uc.lp3.avera_tplp3_2026.rest.integration.RechazoExternoException;
import py.edu.uc.lp3.avera_tplp3_2026.rest.integration.ReglaExternaClient;

@Service
public class EscenarioService {

    private static final Logger LOG = LogManager.getLogger(EscenarioService.class);

    private final EntidadVivaRepository entidadRepository;
    private final JugadaRepository jugadaRepository;
    private final ReglaExternaClient reglaExternaClient;

    public EscenarioService(EntidadVivaRepository entidadRepository,
                            JugadaRepository jugadaRepository,
                            ReglaExternaClient reglaExternaClient) {
        this.entidadRepository = entidadRepository;
        this.jugadaRepository = jugadaRepository;
        this.reglaExternaClient = reglaExternaClient;
    }

    @Transactional
    public EscenarioResponse ejecutar(EscenarioRequest request) {
        if (request == null || request.jugadorId() == null || request.monstruoId() == null) {
            throw new EntidadInvalidaException("jugadorId y monstruoId son obligatorios");
        }
        LOG.debug("Escenario iniciado: jugadorId={} monstruoId={} potencia={} jugadasAntes={}",
                request.jugadorId(), request.monstruoId(), request.potencia(), jugadaRepository.count());

        EntidadViva jugadorEntidad = entidadRepository.findById(request.jugadorId())
                .orElseThrow(() -> new EntidadInvalidaException(
                        "No existe el jugador " + request.jugadorId()));
        EntidadViva monstruoEntidad = entidadRepository.findById(request.monstruoId())
                .orElseThrow(() -> new EntidadInvalidaException(
                        "No existe el monstruo " + request.monstruoId()));
        if (!(jugadorEntidad instanceof PersonajeJugable jugador)) {
            throw new EntidadInvalidaException("La entidad " + request.jugadorId() + " no es un jugador");
        }
        if (!(monstruoEntidad instanceof Monstruo monstruo)) {
            throw new EntidadInvalidaException("La entidad " + request.monstruoId() + " no es un monstruo");
        }

        int escudos = (int) jugador.getInventario().stream()
                .filter(item -> item.equalsIgnoreCase("escudo")).count();
        ValidacionExternaRequest payload = new ValidacionExternaRequest(
                jugador.getNombre(), jugador.getVida(), escudos, monstruo.getNombre(),
                request.potencia(), "explosion-validada");
        LOG.debug("Paso 1: payload común armado para A2 y A3: {}", payload);

        ValidacionExternaResponse decisionA2 = reglaExternaClient.validar(Companero.A2, payload);
        if (!decisionA2.aceptada()) {
            throw new RechazoExternoException("A2", 400,
                    decisionA2.codigo() == null ? "REGLA_A2" : decisionA2.codigo(),
                    decisionA2.motivo());
        }
        LOG.debug("Paso 2: A2 aceptó; se consulta A3");

        ValidacionExternaResponse decisionA3 = reglaExternaClient.validar(Companero.A3, payload);
        if (!decisionA3.aceptada()) {
            throw new RechazoExternoException("A3", 400,
                    decisionA3.codigo() == null ? "REGLA_A3" : decisionA3.codigo(),
                    decisionA3.motivo());
        }
        LOG.debug("Paso 3: A3 aceptó; ambas reglas permiten persistir");

        List<EntidadViva> mundo = new ArrayList<>(entidadRepository.findAll());
        Map<Long, Integer> vidaAntes = new HashMap<>();
        for (EntidadViva entidad : mundo) {
            vidaAntes.put(entidad.getId(), entidad.getVida());
        }
        LOG.debug("Paso 3b: vidas antes de aplicar la onda {}", vidaAntes);
        Explosion explosion = monstruo.explotar(request.potencia());
        String origen = "explosión validada por A2 y A3 de " + monstruo.getNombre();
        int escudosConsumidos = 0;
        for (EntidadViva entidad : mundo) {
            if (entidad.isVivo()) {
                escudosConsumidos += entidad.recibirExplosion(explosion.potencia(), origen);
            }
        }
        entidadRepository.saveAll(mundo);

        Jugada jugada = jugadaRepository.save(new Jugada(
                jugador.getId(), jugador.getNombre(), monstruo.getId(), monstruo.getNombre(),
                request.potencia(), escudosConsumidos, jugador.getVida(), true, true, request.motivo()));
        LOG.debug("Paso 4: jugada {} persistida; escudos={} vidaJugador={} jugadasDespués={}",
                jugada.getId(), escudosConsumidos, jugador.getVida(), jugadaRepository.count());

        return new EscenarioResponse(
                jugada.getId(), jugador.getId(), monstruo.getId(), request.potencia(),
                true, true, escudosConsumidos, jugador.getVida(),
                "A2 y A3 aceptaron; la jugada quedó persistida");
    }
}

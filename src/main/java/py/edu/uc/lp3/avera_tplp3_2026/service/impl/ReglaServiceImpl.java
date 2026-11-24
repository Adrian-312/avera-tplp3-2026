package py.edu.uc.lp3.avera_tplp3_2026.service.impl;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ESCUDO_ABSORCION;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.ReglaRechazadaException;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.AbsorcionEscudoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.CondicionPreviaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.VeredictoRegla;
import py.edu.uc.lp3.avera_tplp3_2026.service.CondicionPreviaService;
import py.edu.uc.lp3.avera_tplp3_2026.service.ReglaService;

@Service
public class ReglaServiceImpl implements ReglaService {

    private final CondicionPreviaService condicionPreviaService;

    public ReglaServiceImpl(CondicionPreviaService condicionPreviaService) {
        this.condicionPreviaService = condicionPreviaService;
    }

    @Override
    @Transactional(readOnly = true)
    public VeredictoRegla validarCondicionPrevia(CondicionPreviaRequest request) {
        if (request == null) {
            throw new EntidadInvalidaException("jugadorId y monstruoId son obligatorios");
        }
        CondicionPreviaService.Participantes participantes =
                condicionPreviaService.validar(request.jugadorId(), request.monstruoId());
        return new VeredictoRegla(
                "condicion-previa", true,
                participantes.jugador().getId(), participantes.monstruo().getId(),
                true, participantes.jugador().isVivo(), participantes.monstruo().isVivo(),
                participantes.jugador().getVida(), participantes.monstruo().getVida(),
                null, null, null, null, null,
                "El jugador " + participantes.jugador().getNombre() + " y el monstruo "
                        + participantes.monstruo().getNombre()
                        + " existen, tienen el tipo correcto y están vivos antes de la explosión");
    }

    @Override
    public VeredictoRegla absorcionEscudo(AbsorcionEscudoRequest request) {
        if (request == null || request.potencia() == null
                || request.escudosDisponibles() == null || request.vidaJugador() == null) {
            throw new EntidadInvalidaException(
                    "potencia, escudosDisponibles y vidaJugador son obligatorios");
        }
        if (request.potencia() <= 0) {
            throw new EntidadInvalidaException("La potencia debe ser mayor que cero");
        }
        if (request.escudosDisponibles() < 0) {
            throw new EntidadInvalidaException("Los escudos disponibles no pueden ser negativos");
        }
        if (request.vidaJugador() < 0) {
            throw new EntidadInvalidaException("La vida del jugador no puede ser negativa");
        }

        int necesarios = (request.potencia() + ESCUDO_ABSORCION - 1) / ESCUDO_ABSORCION;
        int consumidos = Math.min(request.escudosDisponibles(), necesarios);
        int danio = Math.max(0, request.potencia() - consumidos * ESCUDO_ABSORCION);
        int vidaDespues = Math.max(0, request.vidaJugador() - danio);
        if (request.danioDeclarado() != null && request.danioDeclarado() != danio) {
            throw new ReglaRechazadaException("Con " + consumidos + " escudo(s) el daño es " + danio
                    + ", no " + request.danioDeclarado());
        }
        return new VeredictoRegla(
                "absorcion-escudo", true,
                null, null, null, null, null, request.vidaJugador(), null,
                request.potencia(), request.escudosDisponibles(), consumidos, danio, vidaDespues,
                "Cada escudo absorbe " + ESCUDO_ABSORCION + " puntos: se consumen " + consumidos
                        + ", llegan " + danio + " de daño y la vida queda en " + vidaDespues);
    }
}

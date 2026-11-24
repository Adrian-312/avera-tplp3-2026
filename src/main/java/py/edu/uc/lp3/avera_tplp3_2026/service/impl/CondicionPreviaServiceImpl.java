package py.edu.uc.lp3.avera_tplp3_2026.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.ReglaRechazadaException;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.service.CondicionPreviaService;

@Service
public class CondicionPreviaServiceImpl implements CondicionPreviaService {

    private final EntidadVivaRepository repository;

    public CondicionPreviaServiceImpl(EntidadVivaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public Participantes validar(Long jugadorId, Long monstruoId) {
        if (jugadorId == null || monstruoId == null) {
            throw new EntidadInvalidaException("jugadorId y monstruoId son obligatorios");
        }

        EntidadViva jugadorEntidad = repository.findById(jugadorId)
                .orElseThrow(() -> new ReglaRechazadaException(
                        "No existe el jugador " + jugadorId + " en la base de A1"));
        EntidadViva monstruoEntidad = repository.findById(monstruoId)
                .orElseThrow(() -> new ReglaRechazadaException(
                        "No existe el monstruo " + monstruoId + " en la base de A1"));

        if (!(jugadorEntidad instanceof PersonajeJugable jugador)) {
            throw new ReglaRechazadaException(
                    "La entidad " + jugadorId + " no es un PersonajeJugable");
        }
        if (!(monstruoEntidad instanceof Monstruo monstruo)) {
            throw new ReglaRechazadaException(
                    "La entidad " + monstruoId + " no es un Monstruo");
        }
        if (!jugador.isVivo() || !monstruo.isVivo()) {
            throw new ReglaRechazadaException(
                    "El jugador y el monstruo deben estar vivos antes de la explosión");
        }

        return new Participantes(jugador, monstruo);
    }
}

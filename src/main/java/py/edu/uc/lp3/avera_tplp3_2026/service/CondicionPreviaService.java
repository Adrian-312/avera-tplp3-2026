package py.edu.uc.lp3.avera_tplp3_2026.service;

import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;

public interface CondicionPreviaService {

    Participantes validar(Long jugadorId, Long monstruoId);

    record Participantes(PersonajeJugable jugador, Monstruo monstruo) {
    }
}

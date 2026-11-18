package py.edu.uc.lp3.avera_tplp3_2026.service;

import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.SimulacionResponse;

public interface SimulacionService {

    SimulacionResponse simularExplosion(Long monstruoId, Integer potencia);
}

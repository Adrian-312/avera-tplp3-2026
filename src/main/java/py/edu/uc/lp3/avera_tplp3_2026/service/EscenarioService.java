package py.edu.uc.lp3.avera_tplp3_2026.service;

import java.util.List;

import py.edu.uc.lp3.avera_tplp3_2026.domain.Jugada;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioResponse;

public interface EscenarioService {

    EscenarioResponse ejecutar(EscenarioRequest request);

    List<Jugada> listar();
}

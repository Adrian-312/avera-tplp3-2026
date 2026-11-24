package py.edu.uc.lp3.avera_tplp3_2026.service;

import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.AbsorcionEscudoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.CaidaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.VeredictoRegla;

public interface ReglaService {

    VeredictoRegla danioCaida(CaidaRequest request);

    VeredictoRegla absorcionEscudo(AbsorcionEscudoRequest request);
}

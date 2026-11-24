package py.edu.uc.lp3.avera_tplp3_2026.service;

import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.AbsorcionEscudoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.CondicionPreviaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.VeredictoRegla;

public interface ReglaService {

    VeredictoRegla validarCondicionPrevia(CondicionPreviaRequest request);

    VeredictoRegla absorcionEscudo(AbsorcionEscudoRequest request);
}

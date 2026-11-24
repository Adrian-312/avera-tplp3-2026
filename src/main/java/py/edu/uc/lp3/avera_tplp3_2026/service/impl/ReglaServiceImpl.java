package py.edu.uc.lp3.avera_tplp3_2026.service.impl;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ALTURA_SEGURA_CAIDA;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ESCUDO_ABSORCION;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.ReglaRechazadaException;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.AbsorcionEscudoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.CaidaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.VeredictoRegla;
import py.edu.uc.lp3.avera_tplp3_2026.service.ReglaService;

@Service
public class ReglaServiceImpl implements ReglaService {

    @Override
    public VeredictoRegla danioCaida(CaidaRequest request) {
        if (request == null || request.alturaInicial() == null || request.alturaFinal() == null) {
            throw new EntidadInvalidaException("alturaInicial y alturaFinal son obligatorias");
        }
        double bloques = Math.max(0.0, request.alturaInicial() - request.alturaFinal());
        int danio = (int) Math.max(0, Math.floor(bloques - ALTURA_SEGURA_CAIDA));
        if (request.danioDeclarado() != null && request.danioDeclarado() != danio) {
            throw new ReglaRechazadaException("Con " + bloques + " bloques caídos el daño es "
                    + danio + ", no " + request.danioDeclarado());
        }
        return new VeredictoRegla(
                "danio-caida", true, null, null, null, null, danio, (int) bloques,
                "Se caen " + bloques + " bloques; los primeros " + ALTURA_SEGURA_CAIDA
                        + " no hacen daño y quedan " + danio + " de daño");
    }

    @Override
    public VeredictoRegla absorcionEscudo(AbsorcionEscudoRequest request) {
        if (request == null || request.potencia() == null || request.escudosDisponibles() == null) {
            throw new EntidadInvalidaException("potencia y escudosDisponibles son obligatorios");
        }
        if (request.potencia() <= 0) {
            throw new EntidadInvalidaException("La potencia debe ser mayor que cero");
        }
        if (request.escudosDisponibles() < 0) {
            throw new EntidadInvalidaException("Los escudos disponibles no pueden ser negativos");
        }
        int necesarios = (request.potencia() + ESCUDO_ABSORCION - 1) / ESCUDO_ABSORCION;
        int consumidos = Math.min(request.escudosDisponibles(), necesarios);
        int danio = Math.max(0, request.potencia() - consumidos * ESCUDO_ABSORCION);
        if (request.danioDeclarado() != null && request.danioDeclarado() != danio) {
            throw new ReglaRechazadaException("Con " + consumidos + " escudo(s) el daño es " + danio
                    + ", no " + request.danioDeclarado());
        }
        return new VeredictoRegla(
                "absorcion-escudo", true, request.potencia(), null,
                request.escudosDisponibles(), consumidos, danio, null,
                "Cada escudo absorbe " + ESCUDO_ABSORCION + " puntos: se consumen " + consumidos
                        + " y quedan " + danio + " de daño");
    }
}

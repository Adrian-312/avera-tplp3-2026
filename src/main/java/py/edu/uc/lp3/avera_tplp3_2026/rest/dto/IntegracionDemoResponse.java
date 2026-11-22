package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import java.util.List;
import java.util.Map;

public record IntegracionDemoResponse(
        String objetoId,
        String objetoNombre,
        Map<String, Object> datosExternos,
        String monstruo,
        int potencia,
        int escudosConsumidos,
        int vidaJugador,
        String sonido,
        List<String> pasos) {
}

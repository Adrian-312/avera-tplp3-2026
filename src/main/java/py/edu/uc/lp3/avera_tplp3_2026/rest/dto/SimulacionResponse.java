package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import java.util.List;

public record SimulacionResponse(
        Long monstruoId,
        String monstruo,
        int potencia,
        int escudosConsumidos,
        String sonido,
        List<ResultadoEntidad> afectadas) {

    public record ResultadoEntidad(
            Long id,
            String nombre,
            String tipo,
            int vidaAntes,
            int vidaDespues,
            int escudosConsumidos,
            boolean vivo) {
    }
}

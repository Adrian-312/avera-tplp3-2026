package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado del escenario: jugada guardada y decisión de cada compañero.")
public record EscenarioResponse(
        Long jugadaId,
        Long jugadorId,
        Long monstruoId,
        int potencia,
        boolean aceptadaPorA2,
        boolean aceptadaPorA3,
        int escudosConsumidos,
        int vidaJugadorDespues,
        String detalle) {
}

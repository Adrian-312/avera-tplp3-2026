package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Escenario propio de A1: explosión validada por A2 y A3 antes de persistirse.")
public record EscenarioRequest(
        @Schema(description = "Id del jugador persistido en A1", example = "1") Long jugadorId,
        @Schema(description = "Id del monstruo persistido en A1", example = "2") Long monstruoId,
        @Schema(description = "Potencia de la explosión", example = "6") int potencia,
        @Schema(description = "Motivo narrativo de la jugada", example = "explosión validada") String motivo) {
}

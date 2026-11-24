package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Condición previa publicada por A1: el jugador y el monstruo deben existir, tener el tipo correcto y estar vivos antes de una explosión. No modifica la base.")
public record CondicionPreviaRequest(
        @Schema(description = "Id del PersonajeJugable persistido en A1", example = "1") Long jugadorId,
        @Schema(description = "Id del Monstruo persistido en A1", example = "2") Long monstruoId) {
}

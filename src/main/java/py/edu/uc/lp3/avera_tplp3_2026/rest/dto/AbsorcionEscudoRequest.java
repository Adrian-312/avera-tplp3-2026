package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Jugada sometida a la regla 2 publicada por A1: cada escudo absorbe 5 puntos.")
public record AbsorcionEscudoRequest(
        @Schema(description = "Potencia de la onda", example = "10") Integer potencia,
        @Schema(description = "Escudos disponibles en el inventario", example = "2") Integer escudosDisponibles,
        @Schema(description = "Daño declarado; si no coincide con la fórmula, la regla rechaza", example = "0") Integer danioDeclarado) {
}

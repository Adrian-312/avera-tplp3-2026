package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Regla 2 publicada por A1: cada escudo absorbe 5 puntos de la onda y se consume. Con la vida recibida se calcula vidaDespues.")
public record AbsorcionEscudoRequest(
        @Schema(description = "Potencia de la onda que llega al jugador", example = "10") Integer potencia,
        @Schema(description = "Escudos disponibles en el inventario", example = "2") Integer escudosDisponibles,
        @Schema(description = "Vida del jugador antes de recibir la onda", example = "20") Integer vidaJugador,
        @Schema(description = "Daño declarado; si no coincide con el cálculo, la regla rechaza", example = "0") Integer danioDeclarado) {
}

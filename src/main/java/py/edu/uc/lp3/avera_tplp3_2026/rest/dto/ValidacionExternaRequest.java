package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Hecho del juego que A1 traduce al cuerpo de cada OpenAPI ajeno.")
public record ValidacionExternaRequest(
        @Schema(description = "Nombre del jugador de A1") String jugador,
        @Schema(description = "Vida del jugador antes de la jugada") int vidaJugador,
        @Schema(description = "Escudos disponibles en el inventario") int escudosDisponibles,
        @Schema(description = "Nombre del monstruo que explode") String monstruo,
        @Schema(description = "Potencia de la explosión") int potencia,
        @Schema(description = "Radio enviado a A2 y a A3") double radio,
        @Schema(description = "Distancia enviada a la regla de A3") double distancia,
        @Schema(description = "Identificador del escenario") String escenario) {
}

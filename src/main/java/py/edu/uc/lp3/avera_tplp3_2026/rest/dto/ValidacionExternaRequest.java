package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Cuerpo que A1 envía a A2 y a A3 según el contrato leído de sus OpenAPI.")
public record ValidacionExternaRequest(
        @Schema(description = "Nombre del jugador de A1") String jugador,
        @Schema(description = "Vida del jugador antes de la jugada") int vidaJugador,
        @Schema(description = "Escudos disponibles en el inventario") int escudosDisponibles,
        @Schema(description = "Nombre del monstruo que explode") String monstruo,
        @Schema(description = "Potencia de la explosión") int potencia,
        @Schema(description = "Identificador del escenario") String escenario) {
}

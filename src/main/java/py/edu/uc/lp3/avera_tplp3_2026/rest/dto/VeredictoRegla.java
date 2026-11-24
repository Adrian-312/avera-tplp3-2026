package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Veredicto de una regla publicada por A1. El rechazo viaja como 400 REGLA_RECHAZADA. Los campos que no aplican a la regla consultada vuelven null.")
public record VeredictoRegla(
        @Schema(description = "Nombre de la regla", example = "condicion-previa") String regla,
        @Schema(description = "Siempre true: el rechazo se comunica con otro código HTTP") boolean aceptada,
        @Schema(description = "Id del jugador evaluado", example = "1") Long jugadorId,
        @Schema(description = "Id del monstruo evaluado", example = "2") Long monstruoId,
        @Schema(description = "Tipos correctos: PersonajeJugable y Monstruo", example = "true") Boolean tiposCorrectos,
        @Schema(description = "El jugador estaba vivo antes de la explosión", example = "true") Boolean jugadorVivo,
        @Schema(description = "El monstruo estaba vivo antes de la explosión", example = "true") Boolean monstruoVivo,
        @Schema(description = "Vida del jugador antes de la explosión", example = "20") Integer vidaJugador,
        @Schema(description = "Vida del monstruo antes de la explosión", example = "20") Integer vidaMonstruo,
        @Schema(description = "Potencia de la onda", example = "10") Integer potencia,
        @Schema(description = "Escudos disponibles", example = "2") Integer escudosDisponibles,
        @Schema(description = "Escudos que se consumirían", example = "2") Integer escudosConsumidos,
        @Schema(description = "Daño que llega al jugador después de los escudos", example = "0") Integer danioCalculado,
        @Schema(description = "Vida resultante del jugador", example = "20") Integer vidaDespues,
        @Schema(description = "Explicación de la decisión") String motivo) {
}

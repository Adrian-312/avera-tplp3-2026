package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Veredicto de una regla publicada por A1. El rechazo viaja como 400 REGLA_RECHAZADA.")
public record VeredictoRegla(
        @Schema(description = "Nombre de la regla", example = "danio-caida") String regla,
        @Schema(description = "Siempre true: el rechazo se comunica con otro código HTTP") boolean aceptada,
        @Schema(description = "Potencia evaluada", example = "6") Integer potencia,
        @Schema(description = "Potencia máxima del monstruo", example = "10") Integer potenciaMaxima,
        @Schema(description = "Escudos disponibles", example = "2") Integer escudosDisponibles,
        @Schema(description = "Escudos que se consumirían", example = "2") Integer escudosConsumidos,
        @Schema(description = "Daño que quedaría después de los escudos o del cálculo de caída", example = "0") Integer danioCalculado,
        @Schema(description = "Bloques caídos, solo aplica a la regla de caída", example = "10") Integer bloquesCaidos,
        @Schema(description = "Explicación de la decisión") String motivo) {
}

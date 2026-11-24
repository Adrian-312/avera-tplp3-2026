package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Caída entre dos alturas. Según Minecraft, los primeros 3 bloques no hacen daño y después cada bloque quita 1 punto.")
public record CaidaRequest(
        @Schema(description = "Altura inicial en bloques", example = "12.0") Double alturaInicial,
        @Schema(description = "Altura final en bloques", example = "2.0") Double alturaFinal,
        @Schema(description = "Daño que el compañero esperaba; si no coincide, la regla rechaza", example = "7") Integer danioDeclarado) {
}

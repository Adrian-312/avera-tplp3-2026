package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de una regla ajena: aceptada=true permite persistir la jugada.")
public record ValidacionExternaResponse(
        boolean aceptada,
        String motivo,
        String codigo) {
}

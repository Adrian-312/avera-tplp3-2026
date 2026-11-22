package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Pedido de prueba del WebClient contra la API pública de objetos.")
public record IntegracionDemoRequest(
        @Schema(description = "Id del objeto en la API externa") String objetoId,
        @Schema(description = "Nombre del monstruo local que aplica la regla") String nombreMonstruo,
        @Schema(description = "Potencia usada por la regla local") int potencia,
        @Schema(description = "Escudos del jugador local usados por la regla local") int escudos) {
}

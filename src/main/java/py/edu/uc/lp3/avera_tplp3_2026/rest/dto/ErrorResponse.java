package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

// Cuerpo JSON que devuelve el GlobalExceptionHandler en todos los errores.
// El orden de los campos coincide con cómo lo construye el handler.
public record ErrorResponse(String timestamp, int status, String codigo, String mensaje, String path) {
}

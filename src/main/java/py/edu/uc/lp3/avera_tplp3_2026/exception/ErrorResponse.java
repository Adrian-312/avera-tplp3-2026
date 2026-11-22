package py.edu.uc.lp3.avera_tplp3_2026.exception;

// Cuerpo JSON que devuelve el GlobalExceptionHandler en todos los errores.
public record ErrorResponse(String timestamp, int status, String codigo, String mensaje, String path) {
}

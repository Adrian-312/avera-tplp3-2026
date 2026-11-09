package py.edu.uc.lp3.avera_tplp3_2026.rest.dto;

public record ErrorResponse(String timestamp, int status, String codigo, String mensaje, String path) {
}

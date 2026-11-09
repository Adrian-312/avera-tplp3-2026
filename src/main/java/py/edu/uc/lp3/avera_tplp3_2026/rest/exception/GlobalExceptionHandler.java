package py.edu.uc.lp3.avera_tplp3_2026.rest.exception;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.MinecraftException;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntidadNoEncontradaException.class)
    public ResponseEntity<ErrorResponse> handleNoEncontrada(EntidadNoEncontradaException ex,
                                                            HttpServletRequest request) {
        return respuesta(HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(EntidadInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleInvalida(EntidadInvalidaException ex,
                                                        HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(MinecraftException.class)
    public ResponseEntity<ErrorResponse> handleBase(MinecraftException ex,
                                                    HttpServletRequest request) {
        return respuesta(HttpStatus.BAD_REQUEST, ex, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                               HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "ARGUMENTO_INVALIDO",
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTipoInvalido(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "PARAMETRO_INVALIDO",
                "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue(),
                request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    // JSON roto o ilegible en el cuerpo de la petición.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonIlegible(HttpMessageNotReadableException ex,
                                                            HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                HttpStatus.BAD_REQUEST.value(),
                "CUERPO_ILEGIBLE",
                "El cuerpo de la petición no es un JSON válido",
                request.getRequestURI());
        return ResponseEntity.badRequest().body(body);
    }

    private ResponseEntity<ErrorResponse> respuesta(HttpStatus status, MinecraftException ex,
                                                    HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                Instant.now().toString(),
                status.value(),
                ex.getCodigo(),
                ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}

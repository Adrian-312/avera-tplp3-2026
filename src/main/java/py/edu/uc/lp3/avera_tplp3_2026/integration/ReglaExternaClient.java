package py.edu.uc.lp3.avera_tplp3_2026.integration;

import java.time.Duration;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.avera_tplp3_2026.exception.IntegracionExternaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.RechazoExternoException;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ValidacionExternaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ValidacionExternaResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class ReglaExternaClient {

    private static final Logger LOG = LoggerFactory.getLogger(ReglaExternaClient.class);
    private static final String REGLA_A3 = "onda-expansiva";

    private final Map<Companero, WebClient> clientes;
    private final Map<Companero, String> baseUrls;
    private final Map<Companero, String> rutas;
    private final Duration timeout;
    private final ObjectMapper objectMapper;

    public ReglaExternaClient(
            WebClient.Builder builder,
            ObjectMapper objectMapper,
            @Value("${integracion.a2.base-url}") String a2BaseUrl,
            @Value("${integracion.a2.path}") String a2Path,
            @Value("${integracion.a3.base-url}") String a3BaseUrl,
            @Value("${integracion.a3.path}") String a3Path,
            @Value("${integracion.client.timeout-seconds}") long timeoutSeconds) {
        this.objectMapper = objectMapper;
        this.clientes = Map.of(
                Companero.A2, builder.clone().baseUrl(a2BaseUrl).build(),
                Companero.A3, builder.clone().baseUrl(a3BaseUrl).build());
        this.baseUrls = Map.of(Companero.A2, a2BaseUrl, Companero.A3, a3BaseUrl);
        this.rutas = Map.of(Companero.A2, a2Path, Companero.A3, a3Path);
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        LOG.info("Clientes configurados: A2={}{}, A3={}{}, timeout={}s",
                a2BaseUrl, a2Path, a3BaseUrl, a3Path, timeoutSeconds);
    }

    public ValidacionExternaResponse validar(Companero companero, ValidacionExternaRequest payload) {
        WebClient cliente = clientes.get(companero);
        String ruta = rutas.get(companero);
        LOG.debug("A1 -> {}: POST {}{} con {}", companero, baseUrls.get(companero), ruta, payload);
        try {
            RespuestaCruda cruda = cliente.post()
                    .uri(ruta)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(cuerpoPara(companero, payload))
                    .exchangeToMono(response -> response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .map(body -> new RespuestaCruda(response.statusCode().value(), body)))
                    .block(timeout);
            LOG.debug("{} -> A1: status={} body={}", companero, cruda.status(), cruda.cuerpo());

            JsonNode nodo = leerJson(companero, cruda.cuerpo());
            if (cruda.status() >= 400) {
                lanzarRechazo(companero, cruda.status(), nodo);
            }
            if (cruda.status() < 200 || cruda.status() >= 300) {
                throw new IntegracionExternaException(companero.name(),
                        companero + " devolvió el estado inesperado " + cruda.status());
            }
            ValidacionExternaResponse decision = decision(companero, cruda.status(), nodo);
            LOG.debug("Decisión de {}: aceptada={}, motivo={}", companero, decision.aceptada(), decision.motivo());
            return decision;
        } catch (RechazoExternoException ex) {
            throw ex;
        } catch (IntegracionExternaException ex) {
            throw ex;
        } catch (JacksonException ex) {
            throw new IntegracionExternaException(companero.name(),
                    "Respuesta no comprensible de " + companero + ": " + ex.getMessage());
        } catch (RuntimeException ex) {
            throw new IntegracionExternaException(companero.name(),
                    "No se pudo consultar " + companero + ": " + ex.getMessage());
        }
    }

    private Map<String, Object> cuerpoPara(Companero companero, ValidacionExternaRequest payload) {
        return switch (companero) {
            case A2 -> Map.of(
                    "x", 0.0,
                    "y", 0.0,
                    "z", 0.0,
                    "radio", payload.radio());
            case A3 -> Map.of(
                    "potencia", payload.potencia(),
                    "radio", (int) Math.round(payload.radio()),
                    "distancia", payload.distancia());
        };
    }

    private JsonNode leerJson(Companero companero, String cuerpo) {
        if (cuerpo == null || cuerpo.isBlank()) {
            throw new IntegracionExternaException(companero.name(),
                    companero + " devolvió un cuerpo vacío");
        }
        try {
            JsonNode nodo = objectMapper.readTree(cuerpo);
            if (!nodo.isObject()) {
                throw new IntegracionExternaException(companero.name(),
                        companero + " no devolvió un objeto JSON");
            }
            return nodo;
        } catch (JacksonException ex) {
            throw new IntegracionExternaException(companero.name(),
                    companero + " no devolvió un JSON válido: " + ex.getMessage());
        }
    }

    private void lanzarRechazo(Companero companero, int status, JsonNode nodo) {
        switch (companero) {
            case A2 -> {
                String detalle = textoRequerido(companero, nodo, "detail");
                int estado = enteroRequerido(companero, nodo, "status");
                if (estado != status) {
                    throw new IntegracionExternaException(companero.name(),
                            "El status del ProblemDetail de A2 no coincide con el HTTP");
                }
                throw new RechazoExternoException(companero.name(), status, "HTTP_" + status, detalle);
            }
            case A3 -> {
                String error = textoRequerido(companero, nodo, "error");
                String mensaje = textoRequerido(companero, nodo, "mensaje");
                int estado = enteroRequerido(companero, nodo, "estado");
                if (estado != status) {
                    throw new IntegracionExternaException(companero.name(),
                            "El estado del error de A3 no coincide con el HTTP");
                }
                throw new RechazoExternoException(companero.name(), status, error, mensaje);
            }
        }
    }

    private ValidacionExternaResponse decision(Companero companero, int status, JsonNode nodo) {
        return switch (companero) {
            case A2 -> {
                numeroRequerido(companero, nodo, "x");
                numeroRequerido(companero, nodo, "y");
                numeroRequerido(companero, nodo, "z");
                double radio = numeroRequerido(companero, nodo, "radio");
                numeroRequerido(companero, nodo, "danioMaximo");
                int afectados = enteroRequerido(companero, nodo, "totalAfectados");
                JsonNode lista = nodo.get("afectados");
                if (lista == null || !lista.isArray()) {
                    throw new IntegracionExternaException(companero.name(),
                            "Falta 'afectados' en la respuesta de A2");
                }
                yield new ValidacionExternaResponse(true,
                        "A2 aceptó la explosión con radio " + radio + " y " + afectados + " afectados",
                        "HTTP_" + status);
            }
            case A3 -> {
                String regla = textoRequerido(companero, nodo, "regla");
                if (!REGLA_A3.equals(regla)) {
                    throw new IntegracionExternaException(companero.name(),
                            "A3 devolvió la regla inesperada '" + regla + "'");
                }
                JsonNode aceptada = nodo.get("aceptada");
                if (aceptada == null || !aceptada.isBoolean()) {
                    throw new IntegracionExternaException(companero.name(),
                            "Falta 'aceptada' como booleano en la respuesta de A3");
                }
                if (!aceptada.booleanValue()) {
                    throw new IntegracionExternaException(companero.name(),
                            "A3 devolvió aceptada=false con un estado 200, fuera de su contrato");
                }
                numeroRequerido(companero, nodo, "danioCalculado");
                if (!nodo.has("bloquesNecesarios")) {
                    throw new IntegracionExternaException(companero.name(),
                            "Falta 'bloquesNecesarios' en la respuesta de A3");
                }
                String motivo = textoRequerido(companero, nodo, "motivo");
                yield new ValidacionExternaResponse(true, motivo, regla);
            }
        };
    }

    private String textoRequerido(Companero companero, JsonNode nodo, String campo) {
        JsonNode valor = nodo.get(campo);
        if (valor == null || !valor.isTextual() || valor.asText().isBlank()) {
            throw new IntegracionExternaException(companero.name(),
                    "Falta '" + campo + "' como texto en la respuesta de " + companero);
        }
        return valor.asText();
    }

    private double numeroRequerido(Companero companero, JsonNode nodo, String campo) {
        JsonNode valor = nodo.get(campo);
        if (valor == null || !valor.isNumber()) {
            throw new IntegracionExternaException(companero.name(),
                    "Falta '" + campo + "' como número en la respuesta de " + companero);
        }
        return valor.asDouble();
    }

    private int enteroRequerido(Companero companero, JsonNode nodo, String campo) {
        JsonNode valor = nodo.get(campo);
        if (valor == null || !valor.isIntegralNumber()) {
            throw new IntegracionExternaException(companero.name(),
                    "Falta '" + campo + "' como entero en la respuesta de " + companero);
        }
        return valor.asInt();
    }

    private record RespuestaCruda(int status, String cuerpo) {
    }
}

package py.edu.uc.lp3.avera_tplp3_2026.integration;

import java.time.Duration;
import java.util.List;
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
                    .bodyValue(payload)
                    .exchangeToMono(response -> response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .map(body -> new RespuestaCruda(response.statusCode().value(), body)))
                    .block(timeout);
            LOG.debug("{} -> A1: status={} body={}", companero, cruda.status(), cruda.cuerpo());

            if (cruda.status() >= 400) {
                String motivo = textoDe(cruda.cuerpo(), "motivo", "mensaje", "message", "detail");
                String codigo = textoDe(cruda.cuerpo(), "codigo", "code");
                throw new RechazoExternoException(companero.name(), cruda.status(),
                        codigo != null ? codigo : "HTTP_" + cruda.status(), motivo);
            }
            if (cruda.cuerpo() == null || cruda.cuerpo().isBlank()) {
                throw new IntegracionExternaException(companero.name(),
                        companero + " devolvió un cuerpo vacío");
            }

            JsonNode nodo = objectMapper.readTree(cruda.cuerpo());
            boolean aceptada = nodo.path("aceptada").asBoolean(false);
            for (String clave : List.of("acepta", "aceptado", "aprobado", "valido", "valida", "ok")) {
                if (nodo.has(clave)) {
                    aceptada = nodo.get(clave).asBoolean(aceptada);
                    break;
                }
            }
            ValidacionExternaResponse decision = new ValidacionExternaResponse(
                    aceptada,
                    valorTexto(nodo, "motivo", "mensaje", "razon", "message", "detail"),
                    valorTexto(nodo, "codigo", "code", "resultado"));
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

    private String textoDe(String cuerpo, String... claves) {
        if (cuerpo == null || cuerpo.isBlank()) {
            return null;
        }
        try {
            JsonNode nodo = objectMapper.readTree(cuerpo);
            for (String clave : List.of(claves)) {
                if (nodo.hasNonNull(clave)) {
                    return nodo.get(clave).asText();
                }
            }
        } catch (JacksonException ex) {
            LOG.debug("El cuerpo no era JSON, se usa como texto: {}", ex.getMessage());
        }
        return cuerpo.length() > 200 ? cuerpo.substring(0, 200) : cuerpo;
    }

    private String valorTexto(JsonNode nodo, String... claves) {
        for (String clave : claves) {
            if (nodo.hasNonNull(clave)) {
                return nodo.get(clave).asText();
            }
        }
        return null;
    }

    private record RespuestaCruda(int status, String cuerpo) {
    }
}

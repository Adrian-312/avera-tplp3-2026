package py.edu.uc.lp3.avera_tplp3_2026.integration;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ValidacionExternaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ValidacionExternaResponse;
import py.edu.uc.lp3exceptions.IntegracionExternaException;
import py.edu.uc.lp3exceptions.RechazoExternoException;

@Component
public class ReglaExternaClient {

    private static final Logger LOG = LogManager.getLogger(ReglaExternaClient.class);

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
                Companero.A2, builder.baseUrl(a2BaseUrl).build(),
                Companero.A3, builder.baseUrl(a3BaseUrl).build());
        this.baseUrls = Map.of(Companero.A2, a2BaseUrl, Companero.A3, a3BaseUrl);
        this.rutas = Map.of(Companero.A2, a2Path, Companero.A3, a3Path);
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        LOG.debug("Clientes configurados: A2={}{} timeout={}s; A3={}{} timeout={}s",
                a2BaseUrl, a2Path, timeoutSeconds, a3BaseUrl, a3Path, timeoutSeconds);
    }

    public ValidacionExternaResponse validar(Companero companero, ValidacionExternaRequest payload) {
        WebClient cliente = clientes.get(companero);
        String ruta = rutas.get(companero);
        LOG.debug("Paso integración {}: armando POST {}{} con payload {}",
                companero, baseUrls.get(companero), ruta, payload);
        try {
            RespuestaCruda cruda = cliente.post()
                    .uri(ruta)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(payload)
                    .exchangeToMono(response -> response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .map(body -> new RespuestaCruda(response.statusCode().value(), body)))
                    .block(timeout);
            LOG.debug("Paso integración {}: status={} body={}", companero, cruda.status(), cruda.cuerpo());
            if (cruda.status() >= 400) {
                String motivo = textoDe(cruda.cuerpo(), "motivo", "mensaje", "message", "detail");
                String codigo = textoDe(cruda.cuerpo(), "codigo", "code");
                throw new RechazoExternoException(companero.name(), cruda.status(),
                        codigo != null ? codigo : "HTTP_" + cruda.status(), motivo);
            }
            ValidacionExternaResponse decision = objectMapper.readValue(
                    cruda.cuerpo(), ValidacionExternaResponse.class);
            LOG.debug("Paso integración {}: decisión aceptada={} motivo={}",
                    companero, decision.aceptada(), decision.motivo());
            return decision;
        } catch (IntegracionExternaException ex) {
            LOG.error("Integración con {} rechazó o falló: {}", companero, ex.getMessage());
            throw ex;
        } catch (JacksonException ex) {
            LOG.error("Integración con {} devolvió un cuerpo no comprensible: {}", companero, ex.getMessage());
            throw new IntegracionExternaException(companero.name(),
                    "Respuesta no comprensible de " + companero + ": " + ex.getMessage());
        } catch (RuntimeException ex) {
            LOG.error("Integración con {} no disponible: {}", companero, ex.getMessage());
            throw new IntegracionExternaException(companero.name(),
                    "No se pudo consultar " + companero + ": " + ex.getMessage());
        } catch (Exception ex) {
            LOG.error("Integración con {} devolvió un cuerpo no comprensible: {}", companero, ex.getMessage());
            throw new IntegracionExternaException(companero.name(),
                    "Respuesta no comprensible de " + companero + ": " + ex.getMessage());
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
        } catch (Exception ex) {
            LOG.debug("El cuerpo no era JSON, se usa como texto: {}", ex.getMessage());
        }
        return cuerpo.length() > 200 ? cuerpo.substring(0, 200) : cuerpo;
    }

    private record RespuestaCruda(int status, String cuerpo) {
    }
}

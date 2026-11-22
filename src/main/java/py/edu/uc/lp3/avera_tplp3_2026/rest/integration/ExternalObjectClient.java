package py.edu.uc.lp3.avera_tplp3_2026.rest.integration;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.ExternalObjectResponse;

@Component
public class ExternalObjectClient {

    private static final Logger LOG = LogManager.getLogger(ExternalObjectClient.class);

    private final WebClient webClient;
    private final String objectsPath;
    private final Duration timeout;

    public ExternalObjectClient(
            WebClient.Builder builder,
            @Value("${integracion.demo.base-url}") String baseUrl,
            @Value("${integracion.demo.objects-path}") String objectsPath,
            @Value("${integracion.demo.timeout-seconds}") long timeoutSeconds) {
        this.webClient = builder.baseUrl(baseUrl).build();
        this.objectsPath = objectsPath;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
        LOG.debug("WebClient de la API de objetos configurado con baseUrl={} path={} timeout={}s",
                baseUrl, objectsPath, timeoutSeconds);
    }

    public ExternalObjectResponse buscar(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id del objeto externo es obligatorio");
        }
        LOG.debug("Paso 1: consultar API externa de objetos, id={}", id);
        try {
            ExternalObjectResponse respuesta = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path(objectsPath).path("/{id}").build(id))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .map(body -> new IntegracionExternaException("API de objetos",
                                    "GET " + objectsPath + "/" + id + " respondió "
                                            + response.statusCode().value() + ": " + body)))
                    .bodyToMono(ExternalObjectResponse.class)
                    .block(timeout);
            LOG.debug("Paso 2: respuesta recibida id={} nombre={} datos={}",
                    respuesta != null ? respuesta.id() : null,
                    respuesta != null ? respuesta.name() : null,
                    respuesta != null ? respuesta.data() : null);
            return respuesta;
        } catch (IntegracionExternaException ex) {
            LOG.error("Fallo la consulta a la API de objetos: {}", ex.getMessage());
            throw ex;
        } catch (RuntimeException ex) {
            LOG.error("Fallo inesperado del WebClient contra la API de objetos: {}", ex.getMessage());
            throw new IntegracionExternaException("API de objetos", "No se pudo consultar la API externa: "
                    + ex.getMessage());
        }
    }
}

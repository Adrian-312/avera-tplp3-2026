package py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalObjectResponse(String id, String name, Map<String, Object> data) {
}

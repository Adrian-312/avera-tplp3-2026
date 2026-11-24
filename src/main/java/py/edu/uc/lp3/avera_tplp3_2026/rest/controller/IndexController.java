package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.constants.ApiPaths;

@RestController
@Tag(name = "Estado del servicio", description = "Confirma que A1 está vivo y dónde está su documentación.")
public class IndexController {

    @GetMapping("/")
    @Operation(summary = "Comprobar que el servicio responde",
            description = "Devuelve el estado del servicio, el autor, el dominio y las rutas del OpenAPI y de Swagger UI.")
    @ApiResponse(responseCode = "200", description = "Servicio activo",
            content = @Content(schema = @Schema(example = """
                    {"estado":"Servicio activo","autor":"Adrian Vera","dominio":"Minecraft","openapi":"/v3/api-docs","swagger":"/swagger-ui.html"}
                    """)))
    public Map<String, Object> index() {
        return Map.of(
                "estado", "Servicio activo",
                "autor", "Adrian Vera",
                "dominio", "Minecraft",
                "openapi", ApiPaths.API_DOCS,
                "swagger", "/swagger-ui.html");
    }
}

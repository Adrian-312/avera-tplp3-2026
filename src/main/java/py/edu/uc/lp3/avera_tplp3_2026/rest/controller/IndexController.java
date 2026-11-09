package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, Object> index() {
        return Map.of(
                "estado", "Servicio activo",
                "autor", "Adrian",
                "dominio", "Minecraft",
                "h2Console", "/h2-console",
                "endpoints", List.of(
                        "GET /minecraft/entidades",
                        "POST /minecraft/entidades",
                        "GET /minecraft/entidades/{id}",
                        "PUT /minecraft/entidades/{id}",
                        "PATCH /minecraft/entidades/{id}",
                        "DELETE /minecraft/entidades/{id}",
                        "GET /minecraft/explosivos",
                        "POST /minecraft/simulacion/explosion"));
    }
}

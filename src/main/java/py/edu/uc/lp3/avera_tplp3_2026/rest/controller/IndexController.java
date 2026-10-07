package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, String> index() {
        return Map.of(
                "estado", "Servicio activo",
                "autor", "Adrian",
                "dominio", "Minecraft");
    }
}

package py.edu.uc.lp3.avera_tplp3_2026.minecraft.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class IndexController {

    @GetMapping("/")
    public Map<String, String> index() {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("estado", "Servicio activo");
        respuesta.put("autor", "Adrian");
        respuesta.put("dominio", "Minecraft");
        return respuesta;
    }
}
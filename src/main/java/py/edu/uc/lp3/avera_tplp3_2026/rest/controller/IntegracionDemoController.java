package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.constants.ApiPaths;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.IntegracionDemoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.IntegracionDemoResponse;
import py.edu.uc.lp3.avera_tplp3_2026.service.IntegracionDemoService;

@RestController
@RequestMapping(ApiPaths.INTEGRACION)
@Tag(name = "Integración de prueba", description = "Consumo de la API pública de objetos con WebClient y reglas locales de Minecraft.")
public class IntegracionDemoController {

    private final IntegracionDemoService integracionDemoService;

    public IntegracionDemoController(IntegracionDemoService integracionDemoService) {
        this.integracionDemoService = integracionDemoService;
    }

    @PostMapping("/objeto")
    @Operation(summary = "Consultar un objeto externo y aplicar sobre él las reglas del dominio local",
            description = "Llama por WebClient a la URL configurada en integracion.demo.base-url y usa el resultado como datos de una explosión local. No persiste.")
    public IntegracionDemoResponse objeto(@RequestBody IntegracionDemoRequest request) {
        return integracionDemoService.ejecutar(request);
    }
}

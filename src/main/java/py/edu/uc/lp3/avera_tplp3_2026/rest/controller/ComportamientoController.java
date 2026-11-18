package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.avera_tplp3_2026.service.ComportamientoService;

@RestController
@RequestMapping("/minecraft")
public class ComportamientoController {

    private final ComportamientoService service;

    public ComportamientoController(ComportamientoService service) {
        this.service = service;
    }

    @GetMapping("/acciones")
    public List<Map<String, String>> obtenerAcciones() {
        return service.obtenerAcciones();
    }

    @GetMapping("/explosivos")
    public List<Map<String, Object>> obtenerExplosivos() {
        return service.obtenerExplosivos();
    }
}

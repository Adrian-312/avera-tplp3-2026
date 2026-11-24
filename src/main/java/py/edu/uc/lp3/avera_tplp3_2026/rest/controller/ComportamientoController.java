package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.service.ComportamientoService;

@RestController
@RequestMapping("/minecraft")
@Tag(name = "Comportamiento del dominio", description = "Lecturas polimórficas: acción especial de cada entidad y objetos que implementan Explosivo.")
public class ComportamientoController {

    private final ComportamientoService service;

    public ComportamientoController(ComportamientoService service) {
        this.service = service;
    }

    @GetMapping("/acciones")
    @Operation(summary = "Acciones especiales", description = "Devuelve la acción de cada entidad viva; los muertos no figuran.")
    public List<Map<String, String>> obtenerAcciones() {
        return service.obtenerAcciones();
    }

    @GetMapping("/explosivos")
    @Operation(summary = "Objetos explosivos", description = "Mezcla jugadores y monstruos vivos que implementan la interface Explosivo.")
    public List<Map<String, Object>> obtenerExplosivos() {
        return service.obtenerExplosivos();
    }
}

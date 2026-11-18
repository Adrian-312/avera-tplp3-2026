package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EntidadRequest;
import py.edu.uc.lp3.avera_tplp3_2026.service.EntidadService;

// El controller solo recibe el pedido HTTP, llama al service y devuelve la respuesta.
@RestController
@RequestMapping("/minecraft/entidades")
public class EntidadController {

    private final EntidadService service;

    public EntidadController(EntidadService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EntidadViva> crear(@RequestBody EntidadRequest request) {
        EntidadViva guardada = service.crear(request);
        return ResponseEntity.created(URI.create("/minecraft/entidades/" + guardada.getId())).body(guardada);
    }

    @GetMapping
    public List<EntidadViva> listar(@RequestParam(required = false) String nombre,
                                    @RequestParam(required = false, name = "vidaMinima") Integer vidaMinima) {
        return service.listar(nombre, vidaMinima);
    }

    @GetMapping("/{id}")
    public EntidadViva buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public EntidadViva reemplazar(@PathVariable Long id, @RequestBody EntidadRequest request) {
        return service.reemplazar(id, request);
    }

    @PatchMapping("/{id}")
    public EntidadViva actualizar(@PathVariable Long id, @RequestBody EntidadRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

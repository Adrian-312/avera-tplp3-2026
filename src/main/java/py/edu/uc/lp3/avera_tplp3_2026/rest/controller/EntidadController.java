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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EntidadRequest;
import py.edu.uc.lp3.avera_tplp3_2026.service.EntidadService;

// El controller solo recibe el pedido HTTP, llama al service y devuelve la respuesta.
@RestController
@RequestMapping("/minecraft/entidades")
@Tag(name = "CRUD de entidades", description = "Alta, consulta, reemplazo, cambio parcial y borrado de jugadores, monstruos, animales y plantas.")
public class EntidadController {

    private final EntidadService service;

    public EntidadController(EntidadService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Crear entidad", description = "El campo tipo decide el subtipo: jugador, monstruo, animal o planta. La vida enviada debe estar entre 1 y 20: una entidad no nace muerta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Entidad creada, el cuerpo trae el id asignado"),
            @ApiResponse(responseCode = "400", description = "ENTIDAD_INVALIDA: dato o regla de creación violada")
    })
    public ResponseEntity<EntidadViva> crear(@RequestBody EntidadRequest request) {
        EntidadViva guardada = service.crear(request);
        return ResponseEntity.created(URI.create("/minecraft/entidades/" + guardada.getId())).body(guardada);
    }

    @GetMapping
    @Operation(summary = "Listar entidades", description = "Filtros opcionales por nombre (ignore case) y vida mínima.")
    public List<EntidadViva> listar(@RequestParam(required = false) String nombre,
                                    @RequestParam(required = false, name = "vidaMinima") Integer vidaMinima) {
        return service.listar(nombre, vidaMinima);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar entidad por id")
    @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA")
    public EntidadViva buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar entidad", description = "Exige nombre y vida. No permite revivir una entidad muerta (409 ENTIDAD_MUERTA) ni matarla enviando vida 0 (400).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entidad reemplazada"),
            @ApiResponse(responseCode = "400", description = "ENTIDAD_INVALIDA"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA"),
            @ApiResponse(responseCode = "409", description = "ENTIDAD_MUERTA: la muerte es un estado final")
    })
    public EntidadViva reemplazar(@PathVariable Long id, @RequestBody EntidadRequest request) {
        return service.reemplazar(id, request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Modificar campos de una entidad", description = "Solo cambia los campos enviados. Rechaza vida 0 (no se mata por CRUD) y cualquier cambio sobre una entidad muerta (409).")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Entidad actualizada"),
            @ApiResponse(responseCode = "400", description = "ENTIDAD_INVALIDA"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA"),
            @ApiResponse(responseCode = "409", description = "ENTIDAD_MUERTA")
    })
    public EntidadViva actualizar(@PathVariable Long id, @RequestBody EntidadRequest request) {
        return service.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar entidad", description = "Borrado físico. Sí está permitido borrar una entidad muerta.")
    @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

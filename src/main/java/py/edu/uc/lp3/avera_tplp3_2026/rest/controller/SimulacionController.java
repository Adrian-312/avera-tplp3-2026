package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ExplosionRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.SimulacionResponse;
import py.edu.uc.lp3.avera_tplp3_2026.service.SimulacionService;

// El controller solo recibe el pedido HTTP, llama al service y devuelve la respuesta.
@RestController
@RequestMapping("/minecraft/simulacion")
@Tag(name = "Simulación de explosiones", description = "Regla del juego que ya publica A1: un monstruo hostil detona y cada escudo del jugador absorbe 5 puntos de la onda.")
public class SimulacionController {

    private final SimulacionService service;

    public SimulacionController(SimulacionService service) {
        this.service = service;
    }

    @PostMapping("/explosion")
    @Operation(summary = "Simular explosión", description = "Aplica la onda a todas las entidades guardadas y persiste las nuevas vidas y los escudos consumidos. Tras morir, el Creeper queda en vida 0 y ya no puede explotar.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulación persistida; el cuerpo trae el estado de cada entidad"),
            @ApiResponse(responseCode = "400", description = "ENTIDAD_INVALIDA: monstruo no hostil o potencia fuera de rango"),
            @ApiResponse(responseCode = "409", description = "ENTIDAD_MUERTA: el monstruo ya explotó"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA")
    })
    public SimulacionResponse explosion(@RequestBody ExplosionRequest request) {
        return service.simularExplosion(request.monstruoId(), request.potencia());
    }
}

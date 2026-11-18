package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ExplosionRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.SimulacionResponse;
import py.edu.uc.lp3.avera_tplp3_2026.service.SimulacionService;

// El controller solo recibe el pedido HTTP, llama al service y devuelve la respuesta.
@RestController
@RequestMapping("/minecraft/simulacion")
public class SimulacionController {

    private final SimulacionService service;

    public SimulacionController(SimulacionService service) {
        this.service = service;
    }

    @PostMapping("/explosion")
    public SimulacionResponse explosion(@RequestBody ExplosionRequest request) {
        return service.simularExplosion(request.monstruoId(), request.potencia());
    }
}

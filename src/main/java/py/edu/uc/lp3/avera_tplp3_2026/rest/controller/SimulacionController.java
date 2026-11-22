package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Explosion;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ExplosionRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.SimulacionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/minecraft/simulacion")
@Tag(name = "Simulación de explosiones", description = "Reglas de POO-08: onda expansiva y escudos de bloque, con estado persistido en H2.")
public class SimulacionController {

    private final EntidadVivaRepository repository;

    public SimulacionController(EntidadVivaRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/explosion")
    @Operation(summary = "Simular una explosión de Creeper",
            description = "Valida que el monstruo esté vivo y hostil y que la potencia no supere la suya. Aplica la onda a todas las entidades vivas; cada jugador consume un escudo por cada 5 puntos de daño absorbidos y el resultado queda persistido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Simulación ejecutada y persistida"),
            @ApiResponse(responseCode = "400", description = "ENTIDAD_INVALIDA: potencia fuera de rango o entidad no explosiva"),
            @ApiResponse(responseCode = "404", description = "ENTIDAD_NO_ENCONTRADA: no existe la entidad indicada")
    })
    @Transactional
    public SimulacionResponse explosion(@RequestBody ExplosionRequest request) {
        if (request == null || request.monstruoId() == null) {
            throw new EntidadInvalidaException("El campo monstruoId es obligatorio");
        }
        if (request.potencia() == null) {
            throw new EntidadInvalidaException("El campo potencia es obligatorio");
        }

        EntidadViva entidad = repository.findById(request.monstruoId())
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No existe un monstruo con id " + request.monstruoId()));
        if (!(entidad instanceof Monstruo monstruo)) {
            throw new EntidadInvalidaException(
                    "La entidad " + request.monstruoId() + " no es un monstruo explosivo");
        }

        List<EntidadViva> mundo = repository.findAll();
        Map<Long, Integer> vidaAntes = new HashMap<>();
        for (EntidadViva e : mundo) {
            if (e.getId() != null) {
                vidaAntes.put(e.getId(), e.getVida());
            }
        }

        Explosion explosion = monstruo.explotar(request.potencia());
        String origen = "explosión de " + monstruo.getNombre();

        List<SimulacionResponse.ResultadoEntidad> resultados = new ArrayList<>();
        int escudosConsumidos = 0;
        for (EntidadViva e : mundo) {
            int antes = vidaAntes.getOrDefault(e.getId(), e.getVida());
            int escudos = 0;
            if (e.isVivo()) {
                escudos = e.recibirExplosion(explosion.potencia(), origen);
            }
            escudosConsumidos += escudos;
            resultados.add(new SimulacionResponse.ResultadoEntidad(
                    e.getId(),
                    e.getNombre(),
                    e.getClass().getSimpleName(),
                    antes,
                    e.getVida(),
                    escudos,
                    e.isVivo()));
        }
        repository.saveAll(mundo);

        return new SimulacionResponse(
                monstruo.getId(),
                monstruo.getNombre(),
                explosion.potencia(),
                escudosConsumidos,
                monstruo.sonidoDeExplosion(),
                resultados);
    }
}

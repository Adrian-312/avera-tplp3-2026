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

@RestController
@RequestMapping("/minecraft/simulacion")
public class SimulacionController {

    private final EntidadVivaRepository repository;

    public SimulacionController(EntidadVivaRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/explosion")
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

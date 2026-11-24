package py.edu.uc.lp3.avera_tplp3_2026.service.impl;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ESCUDO_ABSORCION;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ITEM_ESCUDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Explosion;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadMuertaException;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.SimulacionResponse;
import py.edu.uc.lp3.avera_tplp3_2026.service.SimulacionService;

// Simulación de explosión: las dos reglas del juego, las validaciones y la transacción.
@Service
public class SimulacionServiceImpl implements SimulacionService {

    private final EntidadVivaRepository repository;

    public SimulacionServiceImpl(EntidadVivaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public SimulacionResponse simularExplosion(Long monstruoId, Integer potencia) {
        if (monstruoId == null) {
            throw new EntidadInvalidaException("El campo monstruoId es obligatorio");
        }
        if (potencia == null) {
            throw new EntidadInvalidaException("El campo potencia es obligatorio");
        }

        EntidadViva entidad = repository.findById(monstruoId)
                .orElseThrow(() -> new EntidadNoEncontradaException(
                        "No existe un monstruo con id " + monstruoId));
        if (!(entidad instanceof Monstruo monstruo)) {
            throw new EntidadInvalidaException(
                    "La entidad " + monstruoId + " no es un monstruo explosivo");
        }

        List<EntidadViva> mundo = repository.findAll();
        Map<Long, Integer> vidaAntes = new HashMap<>();
        for (EntidadViva e : mundo) {
            if (e.getId() != null) {
                vidaAntes.put(e.getId(), e.getVida());
            }
        }

        Explosion explosion = explotar(monstruo, potencia);
        String origen = "explosión de " + monstruo.getNombre();

        List<SimulacionResponse.ResultadoEntidad> resultados = new ArrayList<>();
        int escudosConsumidos = 0;
        for (EntidadViva e : mundo) {
            int antes = vidaAntes.getOrDefault(e.getId(), e.getVida());
            int escudos = 0;
            if (e.isVivo()) {
                escudos = recibirExplosion(e, explosion.potencia(), origen);
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

    // Primera regla del juego: el monstruo hostil y vivo se autodestruye y produce
    // una explosión con la potencia indicada.
    private Explosion explotar(Monstruo monstruo, int potencia) {
        if (!monstruo.isVivo()) {
            throw new EntidadMuertaException(
                    "El monstruo " + monstruo.getNombre() + " ya no está vivo y no puede explotar");
        }
        if (!monstruo.isHostil()) {
            throw new EntidadInvalidaException(
                    "El monstruo " + monstruo.getNombre() + " no es hostil y no explota");
        }
        if (potencia <= 0 || potencia > monstruo.getPotenciaExplosiva()) {
            throw new EntidadInvalidaException("La potencia debe estar entre 1 y "
                    + monstruo.getPotenciaExplosiva() + " para " + monstruo.getNombre());
        }
        validarExplosion(monstruo.getNombre(), potencia);

        Explosion explosion = new Explosion(monstruo.getId(), monstruo.getNombre(), potencia);
        aplicarDanio(monstruo, monstruo.getVida(), "explosión de " + monstruo.getNombre());
        return explosion;
    }

    private void validarExplosion(String origen, int potencia) {
        if (origen == null || origen.isBlank()) {
            throw new EntidadInvalidaException("El origen de la explosión no puede estar vacío");
        }
        if (potencia <= 0) {
            throw new EntidadInvalidaException("La potencia de la explosión debe ser mayor que cero");
        }
    }

    // Segunda regla del juego: cada escudo del jugador absorbe 5 puntos de la onda
    // expansiva y se consume del inventario. Sin escudos, el daño llega completo.
    // Devuelve la cantidad de escudos consumidos.
    private int recibirExplosion(EntidadViva entidad, int potencia, String origen) {
        if (entidad instanceof PersonajeJugable jugador) {
            int danioRestante = potencia;
            int escudosConsumidos = 0;
            while (danioRestante > 0 && consumirItem(jugador, ITEM_ESCUDO)) {
                danioRestante = Math.max(0, danioRestante - ESCUDO_ABSORCION);
                escudosConsumidos++;
            }
            aplicarDanio(jugador, danioRestante, origen);
            return escudosConsumidos;
        }
        aplicarDanio(entidad, potencia, origen);
        return 0;
    }

    private boolean consumirItem(PersonajeJugable jugador, String item) {
        List<String> inventario = jugador.getInventario();
        for (int i = 0; i < inventario.size(); i++) {
            if (inventario.get(i).equalsIgnoreCase(item)) {
                inventario.remove(i);
                return true;
            }
        }
        return false;
    }

    private void aplicarDanio(EntidadViva entidad, int puntos, String origen) {
        if (origen == null || origen.isBlank()) {
            throw new EntidadInvalidaException("El origen del daño no puede estar vacío");
        }
        if (puntos < 0) {
            throw new EntidadInvalidaException("El daño no puede ser negativo");
        }
        entidad.setVida(Math.max(0, entidad.getVida() - puntos));
        entidad.setUltimoOrigenDanio(origen.trim());
    }
}

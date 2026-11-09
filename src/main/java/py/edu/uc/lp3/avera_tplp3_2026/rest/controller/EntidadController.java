package py.edu.uc.lp3.avera_tplp3_2026.rest.controller;

import java.net.URI;
import java.util.List;
import java.util.Locale;

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

import py.edu.uc.lp3.avera_tplp3_2026.domain.Animal;
import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Planta;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EntidadRequest;

@RestController
@RequestMapping("/minecraft/entidades")
public class EntidadController {

    private final EntidadVivaRepository repository;

    public EntidadController(EntidadVivaRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<EntidadViva> crear(@RequestBody EntidadRequest request) {
        EntidadViva entidad = construir(request);
        EntidadViva guardada = repository.save(entidad);
        return ResponseEntity.created(URI.create("/minecraft/entidades/" + guardada.getId())).body(guardada);
    }

    @GetMapping
    public List<EntidadViva> listar(@RequestParam(required = false) String nombre,
                                    @RequestParam(required = false, name = "vidaMinima") Integer vidaMinima) {
        boolean hayNombre = nombre != null && !nombre.isBlank();
        if (hayNombre && vidaMinima != null) {
            return repository.findByNombreContainingIgnoreCaseAndVidaGreaterThanEqual(nombre, vidaMinima);
        }
        if (hayNombre) {
            return repository.findByNombreContainingIgnoreCase(nombre);
        }
        if (vidaMinima != null) {
            return repository.findByVidaGreaterThanEqual(vidaMinima);
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public EntidadViva buscar(@PathVariable Long id) {
        return obtener(id);
    }

    @PutMapping("/{id}")
    public EntidadViva reemplazar(@PathVariable Long id, @RequestBody EntidadRequest request) {
        EntidadViva entidad = obtener(id);
        aplicarCambios(entidad, request, true);
        return repository.save(entidad);
    }

    @PatchMapping("/{id}")
    public EntidadViva actualizar(@PathVariable Long id, @RequestBody EntidadRequest request) {
        EntidadViva entidad = obtener(id);
        aplicarCambios(entidad, request, false);
        return repository.save(entidad);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        EntidadViva entidad = obtener(id);
        repository.delete(entidad);
        return ResponseEntity.noContent().build();
    }

    private EntidadViva obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe una entidad con id " + id));
    }

    private EntidadViva construir(EntidadRequest request) {
        if (request == null) {
            throw new EntidadInvalidaException("El cuerpo de la petición no puede estar vacío");
        }
        String tipo = normalizarTipo(request.getTipo());
        int vida = request.getVida() != null ? request.getVida() : EntidadViva.VIDA_MAXIMA;

        return switch (tipo) {
            case "jugador" -> {
                PersonajeJugable jugador = new PersonajeJugable(
                        vida,
                        request.getNombre(),
                        request.getAltura() != null ? request.getAltura() : 1.8,
                        request.getHambre() != null ? request.getHambre() : PersonajeJugable.HAMBRE_MAXIMA,
                        request.getControlable() != null ? request.getControlable() : true);
                jugador.reemplazarInventario(request.getInventario());
                yield jugador;
            }
            case "monstruo" -> new Monstruo(
                    vida,
                    request.getNombre(),
                    request.getAltura() != null ? request.getAltura() : 1.0,
                    request.getCrecen() != null ? request.getCrecen() : false,
                    request.getHostil() != null ? request.getHostil() : true,
                    request.getPuedeEstarArmado() != null ? request.getPuedeEstarArmado() : false,
                    request.getPotenciaExplosiva() != null ? request.getPotenciaExplosiva()
                            : Monstruo.POTENCIA_EXPLOSIVA_CREEPER);
            case "animal" -> new Animal(
                    vida,
                    request.getNombre(),
                    request.getAltura() != null ? request.getAltura() : 1.0,
                    request.getCrecen() != null ? request.getCrecen() : false,
                    request.getDomable() != null ? request.getDomable() : false,
                    request.getPacifico() != null ? request.getPacifico() : true);
            case "planta" -> new Planta(
                    vida,
                    request.getNombre(),
                    request.getAltura() != null ? request.getAltura() : 1.0,
                    request.getCrecen() != null ? request.getCrecen() : false);
            default -> throw new EntidadInvalidaException(
                    "Tipo de entidad desconocido: '" + request.getTipo() + "'. Use jugador, monstruo, animal o planta.");
        };
    }

    private void aplicarCambios(EntidadViva entidad, EntidadRequest request, boolean reemplazo) {
        if (request == null) {
            throw new EntidadInvalidaException("El cuerpo de la petición no puede estar vacío");
        }
        if (request.getTipo() != null && !normalizarTipo(request.getTipo()).equals(tipoDe(entidad))) {
            throw new EntidadInvalidaException(
                    "El tipo '" + request.getTipo() + "' no coincide con la entidad " + tipoDe(entidad));
        }

        if (reemplazo) {
            if (request.getNombre() == null) {
                throw new EntidadInvalidaException("PUT requiere el campo nombre");
            }
            double altura = request.getAltura() != null ? request.getAltura() : alturaPorDefecto(entidad);
            int vida = request.getVida() != null ? request.getVida() : EntidadViva.VIDA_MAXIMA;
            entidad.actualizarPerfil(request.getNombre(), altura);
            entidad.actualizarVida(vida);
        } else {
            String nombre = request.getNombre() != null ? request.getNombre() : entidad.getNombre();
            double altura = request.getAltura() != null ? request.getAltura() : entidad.getAltura();
            entidad.actualizarPerfil(nombre, altura);
            if (request.getVida() != null) {
                entidad.actualizarVida(request.getVida());
            }
        }

        if (entidad instanceof PersonajeJugable jugador) {
            if (reemplazo) {
                jugador.actualizarHambre(request.getHambre() != null ? request.getHambre()
                        : PersonajeJugable.HAMBRE_MAXIMA);
                jugador.actualizarControl(request.getControlable() != null ? request.getControlable() : true);
                jugador.reemplazarInventario(request.getInventario() != null ? request.getInventario() : List.of());
            } else {
                if (request.getHambre() != null) {
                    jugador.actualizarHambre(request.getHambre());
                }
                if (request.getControlable() != null) {
                    jugador.actualizarControl(request.getControlable());
                }
                if (request.getInventario() != null) {
                    jugador.reemplazarInventario(request.getInventario());
                }
            }
        } else if (entidad instanceof Monstruo monstruo) {
            if (reemplazo) {
                monstruo.actualizarCrecimiento(request.getCrecen() != null ? request.getCrecen() : false);
                monstruo.actualizarHostilidad(request.getHostil() != null ? request.getHostil() : true);
                monstruo.actualizarArmamento(request.getPuedeEstarArmado() != null
                        ? request.getPuedeEstarArmado() : false);
                monstruo.actualizarPotenciaExplosiva(request.getPotenciaExplosiva() != null
                        ? request.getPotenciaExplosiva() : Monstruo.POTENCIA_EXPLOSIVA_CREEPER);
            } else {
                if (request.getCrecen() != null) {
                    monstruo.actualizarCrecimiento(request.getCrecen());
                }
                if (request.getHostil() != null) {
                    monstruo.actualizarHostilidad(request.getHostil());
                }
                if (request.getPuedeEstarArmado() != null) {
                    monstruo.actualizarArmamento(request.getPuedeEstarArmado());
                }
                if (request.getPotenciaExplosiva() != null) {
                    monstruo.actualizarPotenciaExplosiva(request.getPotenciaExplosiva());
                }
            }
        } else if (entidad instanceof Animal animal) {
            if (reemplazo) {
                animal.actualizarCrecimiento(request.getCrecen() != null ? request.getCrecen() : false);
                animal.actualizarRasgos(request.getDomable() != null ? request.getDomable() : false,
                        request.getPacifico() != null ? request.getPacifico() : true);
            } else {
                if (request.getCrecen() != null) {
                    animal.actualizarCrecimiento(request.getCrecen());
                }
                animal.actualizarRasgos(
                        request.getDomable() != null ? request.getDomable() : animal.isDomable(),
                        request.getPacifico() != null ? request.getPacifico() : animal.isPacifico());
            }
        } else if (entidad instanceof Planta planta) {
            if (reemplazo) {
                planta.actualizarCrecimiento(request.getCrecen() != null ? request.getCrecen() : false);
            } else if (request.getCrecen() != null) {
                planta.actualizarCrecimiento(request.getCrecen());
            }
        }
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new EntidadInvalidaException("El campo tipo es obligatorio");
        }
        return tipo.trim().toLowerCase(Locale.ROOT);
    }

    private String tipoDe(EntidadViva entidad) {
        if (entidad instanceof PersonajeJugable) {
            return "jugador";
        }
        if (entidad instanceof Monstruo) {
            return "monstruo";
        }
        if (entidad instanceof Animal) {
            return "animal";
        }
        if (entidad instanceof Planta) {
            return "planta";
        }
        return "desconocido";
    }

    private double alturaPorDefecto(EntidadViva entidad) {
        return entidad instanceof PersonajeJugable ? 1.8 : 1.0;
    }
}

package py.edu.uc.lp3.avera_tplp3_2026.service.impl;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ALTURA_DEFECTO;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ALTURA_JUGADOR_DEFECTO;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ALTURA_MAXIMA;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.HAMBRE_MAXIMA;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.POTENCIA_EXPLOSIVA_CREEPER;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.POTENCIA_EXPLOSIVA_MAXIMA;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_ANIMAL;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_JUGADOR;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_MONSTRUO;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_PLANTA;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.VIDA_MAXIMA;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.avera_tplp3_2026.domain.Animal;
import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Planta;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadMuertaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EntidadRequest;
import py.edu.uc.lp3.avera_tplp3_2026.service.EntidadService;

// Lógica de negocio del CRUD: validaciones, excepciones y transacciones.
@Service
public class EntidadServiceImpl implements EntidadService {

    private final EntidadVivaRepository repository;

    public EntidadServiceImpl(EntidadVivaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public EntidadViva crear(EntidadRequest request) {
        return repository.save(construir(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntidadViva> listar(String nombre, Integer vidaMinima) {
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

    @Override
    @Transactional(readOnly = true)
    public EntidadViva buscar(Long id) {
        return obtener(id);
    }

    @Override
    @Transactional
    public EntidadViva reemplazar(Long id, EntidadRequest request) {
        EntidadViva entidad = obtener(id);
        rechazarSiEstaMuerta(entidad);
        aplicarCambios(entidad, request, true);
        return repository.save(entidad);
    }

    @Override
    @Transactional
    public EntidadViva actualizar(Long id, EntidadRequest request) {
        EntidadViva entidad = obtener(id);
        rechazarSiEstaMuerta(entidad);
        aplicarCambios(entidad, request, false);
        return repository.save(entidad);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.delete(obtener(id));
    }

    private EntidadViva obtener(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntidadNoEncontradaException("No existe una entidad con id " + id));
    }

    // ---------------------------------------------------------------- creación

    private EntidadViva construir(EntidadRequest request) {
        if (request == null) {
            throw new EntidadInvalidaException("El cuerpo de la petición no puede estar vacío");
        }
        String tipo = normalizarTipo(request.getTipo());
        int vida = request.getVida() != null ? request.getVida() : VIDA_MAXIMA;
        String nombre = request.getNombre();

        return switch (tipo) {
            case TIPO_JUGADOR -> {
                double altura = request.getAltura() != null ? request.getAltura() : ALTURA_JUGADOR_DEFECTO;
                int hambre = request.getHambre() != null ? request.getHambre() : HAMBRE_MAXIMA;
                boolean controlable = request.getControlable() != null ? request.getControlable() : true;
                validarBase(nombre, vida, altura);
                validarHambre(hambre);
                List<String> inventario = normalizarInventario(request.getInventario());
                PersonajeJugable jugador = new PersonajeJugable(vida, nombre.trim(), altura, hambre, controlable);
                jugador.setInventario(inventario);
                yield jugador;
            }
            case TIPO_MONSTRUO -> {
                double altura = request.getAltura() != null ? request.getAltura() : ALTURA_DEFECTO;
                int potencia = request.getPotenciaExplosiva() != null
                        ? request.getPotenciaExplosiva() : POTENCIA_EXPLOSIVA_CREEPER;
                validarBase(nombre, vida, altura);
                validarPotencia(potencia);
                yield new Monstruo(
                        vida,
                        nombre.trim(),
                        altura,
                        request.getCrecen() != null ? request.getCrecen() : false,
                        request.getHostil() != null ? request.getHostil() : true,
                        request.getPuedeEstarArmado() != null ? request.getPuedeEstarArmado() : false,
                        potencia);
            }
            case TIPO_ANIMAL -> {
                double altura = request.getAltura() != null ? request.getAltura() : ALTURA_DEFECTO;
                validarBase(nombre, vida, altura);
                yield new Animal(
                        vida,
                        nombre.trim(),
                        altura,
                        request.getCrecen() != null ? request.getCrecen() : false,
                        request.getDomable() != null ? request.getDomable() : false,
                        request.getPacifico() != null ? request.getPacifico() : true);
            }
            case TIPO_PLANTA -> {
                double altura = request.getAltura() != null ? request.getAltura() : ALTURA_DEFECTO;
                validarBase(nombre, vida, altura);
                yield new Planta(
                        vida,
                        nombre.trim(),
                        altura,
                        request.getCrecen() != null ? request.getCrecen() : false);
            }
            default -> throw new EntidadInvalidaException(
                    "Tipo de entidad desconocido: '" + request.getTipo() + "'. Use jugador, monstruo, animal o planta.");
        };
    }

    // ------------------------------------------------------------ modificación

    // Regla del juego: morir es un estado final. Una entidad muerta no puede modificarse
    // por PUT ni por PATCH, así que su vida nunca pasa de 0 a un valor positivo.
    // DELETE sí se permite: quita el cadáver de la base.
    private void rechazarSiEstaMuerta(EntidadViva entidad) {
        if (!entidad.isVivo()) {
            throw new EntidadMuertaException("La entidad " + entidad.getNombre() + " (id " + entidad.getId()
                    + ") está muerta: es un estado final y no se puede modificar");
        }
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
            // PUT reemplaza todo el recurso: la vida hay que enviarla. Si faltara, no se
            // puede completar con VIDA_MAXIMA porque eso curaría a la entidad sin motivo.
            if (request.getVida() == null) {
                throw new EntidadInvalidaException("PUT requiere el campo vida");
            }
            double altura = request.getAltura() != null ? request.getAltura() : alturaPorDefecto(entidad);
            int vida = request.getVida();
            actualizarPerfil(entidad, request.getNombre(), altura);
            actualizarVida(entidad, vida);
        } else {
            String nombre = request.getNombre() != null ? request.getNombre() : entidad.getNombre();
            double altura = request.getAltura() != null ? request.getAltura() : entidad.getAltura();
            actualizarPerfil(entidad, nombre, altura);
            if (request.getVida() != null) {
                actualizarVida(entidad, request.getVida());
            }
        }

        if (entidad instanceof PersonajeJugable jugador) {
            cambiarJugador(jugador, request, reemplazo);
        } else if (entidad instanceof Monstruo monstruo) {
            cambiarMonstruo(monstruo, request, reemplazo);
        } else if (entidad instanceof Animal animal) {
            cambiarAnimal(animal, request, reemplazo);
        } else if (entidad instanceof Planta planta) {
            cambiarPlanta(planta, request, reemplazo);
        }
    }

    private void cambiarJugador(PersonajeJugable jugador, EntidadRequest request, boolean reemplazo) {
        if (reemplazo) {
            int hambre = request.getHambre() != null ? request.getHambre() : HAMBRE_MAXIMA;
            validarHambre(hambre);
            jugador.setHambre(hambre);
            jugador.setControlable(request.getControlable() != null ? request.getControlable() : true);
            jugador.setInventario(normalizarInventario(
                    request.getInventario() != null ? request.getInventario() : List.of()));
        } else {
            if (request.getHambre() != null) {
                validarHambre(request.getHambre());
                jugador.setHambre(request.getHambre());
            }
            if (request.getControlable() != null) {
                jugador.setControlable(request.getControlable());
            }
            if (request.getInventario() != null) {
                jugador.setInventario(normalizarInventario(request.getInventario()));
            }
        }
    }

    private void cambiarMonstruo(Monstruo monstruo, EntidadRequest request, boolean reemplazo) {
        if (reemplazo) {
            int potencia = request.getPotenciaExplosiva() != null
                    ? request.getPotenciaExplosiva() : POTENCIA_EXPLOSIVA_CREEPER;
            validarPotencia(potencia);
            monstruo.setCrecen(request.getCrecen() != null ? request.getCrecen() : false);
            monstruo.setHostil(request.getHostil() != null ? request.getHostil() : true);
            monstruo.setPuedeEstarArmado(request.getPuedeEstarArmado() != null
                    ? request.getPuedeEstarArmado() : false);
            monstruo.setPotenciaExplosiva(potencia);
        } else {
            if (request.getCrecen() != null) {
                monstruo.setCrecen(request.getCrecen());
            }
            if (request.getHostil() != null) {
                monstruo.setHostil(request.getHostil());
            }
            if (request.getPuedeEstarArmado() != null) {
                monstruo.setPuedeEstarArmado(request.getPuedeEstarArmado());
            }
            if (request.getPotenciaExplosiva() != null) {
                validarPotencia(request.getPotenciaExplosiva());
                monstruo.setPotenciaExplosiva(request.getPotenciaExplosiva());
            }
        }
    }

    private void cambiarAnimal(Animal animal, EntidadRequest request, boolean reemplazo) {
        if (reemplazo) {
            animal.setCrecen(request.getCrecen() != null ? request.getCrecen() : false);
            animal.setDomable(request.getDomable() != null ? request.getDomable() : false);
            animal.setPacifico(request.getPacifico() != null ? request.getPacifico() : true);
        } else {
            if (request.getCrecen() != null) {
                animal.setCrecen(request.getCrecen());
            }
            if (request.getDomable() != null) {
                animal.setDomable(request.getDomable());
            }
            if (request.getPacifico() != null) {
                animal.setPacifico(request.getPacifico());
            }
        }
    }

    private void cambiarPlanta(Planta planta, EntidadRequest request, boolean reemplazo) {
        if (reemplazo) {
            planta.setCrecen(request.getCrecen() != null ? request.getCrecen() : false);
        } else if (request.getCrecen() != null) {
            planta.setCrecen(request.getCrecen());
        }
    }

    private void actualizarPerfil(EntidadViva entidad, String nombre, double altura) {
        validarNombre(nombre);
        validarAltura(altura);
        entidad.setNombre(nombre.trim());
        entidad.setAltura(altura);
    }

    private void actualizarVida(EntidadViva entidad, int vida) {
        validarVida(vida);
        entidad.setVida(vida);
    }

    // ------------------------------------------------------------ validaciones

    private void validarBase(String nombre, int vida, double altura) {
        validarNombre(nombre);
        validarVida(vida);
        validarAltura(altura);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new EntidadInvalidaException("El nombre no puede estar vacío");
        }
    }

    private void validarVida(int vida) {
        if (vida < 0 || vida > VIDA_MAXIMA) {
            throw new EntidadInvalidaException("La vida debe estar entre 0 y " + VIDA_MAXIMA);
        }
    }

    private void validarAltura(double altura) {
        if (!(altura > 0) || altura > ALTURA_MAXIMA) {
            throw new EntidadInvalidaException(
                    "La altura debe ser mayor que 0 y no superar " + (int) ALTURA_MAXIMA);
        }
    }

    private void validarHambre(int hambre) {
        if (hambre < 0 || hambre > HAMBRE_MAXIMA) {
            throw new EntidadInvalidaException("El hambre debe estar entre 0 y " + HAMBRE_MAXIMA);
        }
    }

    private void validarPotencia(int potencia) {
        if (potencia <= 0 || potencia > POTENCIA_EXPLOSIVA_MAXIMA) {
            throw new EntidadInvalidaException(
                    "La potencia explosiva debe estar entre 1 y " + POTENCIA_EXPLOSIVA_MAXIMA);
        }
    }

    private List<String> normalizarInventario(List<String> items) {
        List<String> resultado = new ArrayList<>();
        if (items == null) {
            return resultado;
        }
        for (String item : items) {
            if (item == null || item.isBlank()) {
                throw new EntidadInvalidaException("El ítem no puede estar vacío");
            }
            resultado.add(item.trim());
        }
        return resultado;
    }

    private String normalizarTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new EntidadInvalidaException("El campo tipo es obligatorio");
        }
        return tipo.trim().toLowerCase(Locale.ROOT);
    }

    private String tipoDe(EntidadViva entidad) {
        if (entidad instanceof PersonajeJugable) {
            return TIPO_JUGADOR;
        }
        if (entidad instanceof Monstruo) {
            return TIPO_MONSTRUO;
        }
        if (entidad instanceof Animal) {
            return TIPO_ANIMAL;
        }
        if (entidad instanceof Planta) {
            return TIPO_PLANTA;
        }
        return "desconocido";
    }

    private double alturaPorDefecto(EntidadViva entidad) {
        return entidad instanceof PersonajeJugable ? ALTURA_JUGADOR_DEFECTO : ALTURA_DEFECTO;
    }
}

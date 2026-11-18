package py.edu.uc.lp3.avera_tplp3_2026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import py.edu.uc.lp3.avera_tplp3_2026.domain.Animal;
import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadMuertaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EntidadRequest;
import py.edu.uc.lp3.avera_tplp3_2026.service.ComportamientoService;
import py.edu.uc.lp3.avera_tplp3_2026.service.EntidadService;
import py.edu.uc.lp3.avera_tplp3_2026.service.SimulacionService;

// Única clase de prueba del proyecto. Levanta la aplicación completa con una base H2
// en memoria (no toca la carpeta data/) y comprueba las capas service y repository.
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:minecraft_test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ApplicationTest {

    @Autowired
    private EntidadVivaRepository repository;

    @Autowired
    private EntidadService entidadService;

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private ComportamientoService comportamientoService;

    @BeforeEach
    void limpiarBase() {
        repository.deleteAll();
    }

    private PersonajeJugable guardarSteve(String... items) {
        PersonajeJugable steve = new PersonajeJugable(20, "Steve", 1.8, 20, true);
        steve.setInventario(List.of(items));
        return repository.save(steve);
    }

    private Monstruo guardarCreeper() {
        return repository.save(new Monstruo(20, "Creeper", 1.7, false, true, false, 10));
    }

    private Animal guardarVaca() {
        return repository.save(new Animal(20, "Vaca", 1.4, true, true, true));
    }

    @Test
    void laAplicacionArranca() {
        assertNotNull(repository);
        assertNotNull(entidadService);
        assertNotNull(simulacionService);
        assertNotNull(comportamientoService);
    }

    @Test
    void laSimulacionPersisteElEstadoDeTodasLasEntidades() {
        PersonajeJugable steve = guardarSteve("escudo", "TNT");
        Monstruo creeper = guardarCreeper();
        Animal vaca = guardarVaca();

        simulacionService.simularExplosion(creeper.getId(), 10);

        // Se vuelve a leer desde la base: lo que importa es lo que quedó guardado.
        EntidadViva creeperGuardado = repository.findById(creeper.getId()).orElseThrow();
        PersonajeJugable steveGuardado = (PersonajeJugable) repository.findById(steve.getId()).orElseThrow();
        EntidadViva vacaGuardada = repository.findById(vaca.getId()).orElseThrow();

        assertEquals(0, creeperGuardado.getVida());
        assertEquals(15, steveGuardado.getVida());
        assertEquals(List.of("TNT"), new ArrayList<>(steveGuardado.getInventario()));
        assertEquals(10, vacaGuardada.getVida());
    }

    @Test
    void unMonstruoQueYaExplotoNoPuedeExplotarOtraVez() {
        guardarSteve();
        Monstruo creeper = guardarCreeper();
        simulacionService.simularExplosion(creeper.getId(), 10);

        assertThrows(EntidadInvalidaException.class,
                () -> simulacionService.simularExplosion(creeper.getId(), 5));
    }

    @Test
    void unAnimalNoPuedeExplotar() {
        Animal vaca = guardarVaca();

        assertThrows(EntidadInvalidaException.class,
                () -> simulacionService.simularExplosion(vaca.getId(), 5));
    }

    @Test
    void unMonstruoInexistenteLanzaNoEncontrada() {
        assertThrows(EntidadNoEncontradaException.class,
                () -> simulacionService.simularExplosion(9999L, 5));
    }

    @Test
    void crearConVidaIlegalLanzaInvalidaYNoGuardaNada() {
        EntidadRequest request = mock(EntidadRequest.class);
        when(request.getTipo()).thenReturn("monstruo");
        when(request.getNombre()).thenReturn("Zombi");
        when(request.getVida()).thenReturn(99);

        assertThrows(EntidadInvalidaException.class, () -> entidadService.crear(request));
        assertEquals(0, repository.count());
    }

    @Test
    void eliminarBorraLaEntidadYUnaBusquedaPosteriorFalla() {
        Animal vaca = guardarVaca();

        entidadService.eliminar(vaca.getId());

        assertThrows(EntidadNoEncontradaException.class, () -> entidadService.buscar(vaca.getId()));
    }

    @Test
    void laInterfaceExplosivaMezclaJugadoresYMonstruos() {
        guardarSteve("TNT");
        guardarCreeper();
        guardarVaca();

        List<Map<String, Object>> explosivos = comportamientoService.obtenerExplosivos();
        List<Object> poderes = explosivos.stream().map(e -> e.get("poderExplosivo")).toList();

        assertEquals(2, explosivos.size());
        assertTrue(poderes.contains(4));
        assertTrue(poderes.contains(10));
    }

    @Test
    void unCreeperMuertoNoPuedeRevivirPorPatch() {
        guardarSteve();
        Monstruo creeper = guardarCreeper();
        simulacionService.simularExplosion(creeper.getId(), 10);
        EntidadRequest request = mock(EntidadRequest.class);
        when(request.getVida()).thenReturn(20);

        assertThrows(EntidadMuertaException.class, () -> entidadService.actualizar(creeper.getId(), request));
        assertEquals(0, repository.findById(creeper.getId()).orElseThrow().getVida());
    }

    @Test
    void unCreeperMuertoNoPuedeRevivirPorPutNiSiquieraSinMandarVida() {
        guardarSteve();
        Monstruo creeper = guardarCreeper();
        simulacionService.simularExplosion(creeper.getId(), 10);
        EntidadRequest request = mock(EntidadRequest.class);
        when(request.getNombre()).thenReturn("Creeper");

        assertThrows(EntidadMuertaException.class, () -> entidadService.reemplazar(creeper.getId(), request));
        assertEquals(0, repository.findById(creeper.getId()).orElseThrow().getVida());
    }

    @Test
    void unaEntidadMuertaSiSePuedeEliminar() {
        guardarSteve();
        Monstruo creeper = guardarCreeper();
        simulacionService.simularExplosion(creeper.getId(), 10);

        entidadService.eliminar(creeper.getId());

        assertThrows(EntidadNoEncontradaException.class, () -> entidadService.buscar(creeper.getId()));
    }

    @Test
    void unPutSinVidaSobreUnaEntidadVivaEsInvalidoYNoLaCura() {
        Animal vaca = repository.save(new Animal(3, "Vaca", 1.4, true, true, true));
        EntidadRequest request = mock(EntidadRequest.class);
        when(request.getNombre()).thenReturn("Vaca");

        assertThrows(EntidadInvalidaException.class, () -> entidadService.reemplazar(vaca.getId(), request));
        assertEquals(3, repository.findById(vaca.getId()).orElseThrow().getVida());
    }

    @Test
    void losMuertosNoAparecenEnAccionesNiEnExplosivos() {
        guardarSteve("TNT");
        Monstruo creeper = guardarCreeper();
        guardarVaca();
        simulacionService.simularExplosion(creeper.getId(), 10);

        List<String> nombres = comportamientoService.obtenerAcciones().stream()
                .map(accion -> accion.get("nombre")).toList();
        List<Object> poderes = comportamientoService.obtenerExplosivos().stream()
                .map(explosivo -> explosivo.get("poderExplosivo")).toList();

        assertTrue(nombres.contains("Steve"));
        assertTrue(nombres.contains("Vaca"));
        assertFalse(nombres.contains("Creeper"));
        assertEquals(1, poderes.size());
        assertTrue(poderes.contains(4));
    }
}

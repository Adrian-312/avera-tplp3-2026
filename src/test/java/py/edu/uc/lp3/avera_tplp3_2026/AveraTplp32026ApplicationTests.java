package py.edu.uc.lp3.avera_tplp3_2026;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.avera_tplp3_2026.domain.Animal;
import py.edu.uc.lp3.avera_tplp3_2026.domain.EntidadViva;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Jugada;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadInvalidaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadMuertaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.EntidadNoEncontradaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.IntegracionExternaException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.RechazoExternoException;
import py.edu.uc.lp3.avera_tplp3_2026.exception.ReglaRechazadaException;
import py.edu.uc.lp3.avera_tplp3_2026.integration.Companero;
import py.edu.uc.lp3.avera_tplp3_2026.integration.ReglaExternaClient;
import py.edu.uc.lp3.avera_tplp3_2026.repository.EntidadVivaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.repository.JugadaRepository;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.AbsorcionEscudoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.CaidaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EscenarioResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.EntidadRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ValidacionExternaRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.ValidacionExternaResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.VeredictoRegla;
import py.edu.uc.lp3.avera_tplp3_2026.service.ComportamientoService;
import py.edu.uc.lp3.avera_tplp3_2026.service.ReglaService;
import py.edu.uc.lp3.avera_tplp3_2026.service.EntidadService;
import py.edu.uc.lp3.avera_tplp3_2026.service.SimulacionService;
import py.edu.uc.lp3.avera_tplp3_2026.service.impl.EscenarioServiceImpl;
import tools.jackson.databind.ObjectMapper;

import com.sun.net.httpserver.HttpServer;

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
    private JugadaRepository jugadaRepository;

    @Autowired
    private EntidadService entidadService;

    @Autowired
    private SimulacionService simulacionService;

    @Autowired
    private ComportamientoService comportamientoService;

    @Autowired
    private ReglaService reglaService;

    private final List<HttpServer> servidores = new ArrayList<>();

    @BeforeEach
    void limpiarBase() {
        jugadaRepository.deleteAll();
        repository.deleteAll();
    }

    @AfterEach
    void cerrarServidores() {
        servidores.forEach(servidor -> servidor.stop(0));
        servidores.clear();
    }

    private ReglaExternaClient clienteCon(int status, String cuerpo, AtomicReference<String> recibido)
            throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(0), 0);
        servidor.createContext("/", intercambio -> {
            recibido.set(new String(intercambio.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] bytes = cuerpo.getBytes(StandardCharsets.UTF_8);
            intercambio.getResponseHeaders().add("Content-Type", "application/json");
            intercambio.sendResponseHeaders(status, bytes.length);
            intercambio.getResponseBody().write(bytes);
            intercambio.close();
        });
        servidor.start();
        servidores.add(servidor);
        String base = "http://localhost:" + servidor.getAddress().getPort();
        return new ReglaExternaClient(WebClient.builder(), new ObjectMapper(),
                base, "/", base, "/", 10);
    }

    private ValidacionExternaRequest payloadDeValidacion() {
        return new ValidacionExternaRequest("Steve", 20, 2, "Creeper", 6, 6.0, 2.0, "explosion-validada");
    }

    private EscenarioServiceImpl escenarioCon(ReglaExternaClient cliente) {
        return new EscenarioServiceImpl(repository, jugadaRepository, simulacionService, cliente);
    }

    private ReglaExternaClient clienteQueAcepta() {
        ReglaExternaClient cliente = mock(ReglaExternaClient.class);
        when(cliente.validar(eq(Companero.A2), any()))
                .thenReturn(new ValidacionExternaResponse(true, "A2 aceptó", "OK_A2"));
        when(cliente.validar(eq(Companero.A3), any()))
                .thenReturn(new ValidacionExternaResponse(true, "A3 aceptó", "OK_A3"));
        return cliente;
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
        assertNotNull(reglaService);
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

        assertThrows(EntidadMuertaException.class,
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

    @Test
    void crearConVidaCeroEsInvalidoYNoGuardaNada() {
        EntidadRequest request = mock(EntidadRequest.class);
        when(request.getTipo()).thenReturn("animal");
        when(request.getNombre()).thenReturn("VacaNacidaMuerta");
        when(request.getVida()).thenReturn(0);

        assertThrows(EntidadInvalidaException.class, () -> entidadService.crear(request));
        assertEquals(0, repository.count());
    }

    @Test
    void unPatchConVidaCeroNoMataALaEntidad() {
        Animal vaca = repository.save(new Animal(5, "Vaca", 1.4, true, true, true));
        EntidadRequest request = mock(EntidadRequest.class);
        when(request.getVida()).thenReturn(0);

        assertThrows(EntidadInvalidaException.class, () -> entidadService.actualizar(vaca.getId(), request));
        assertEquals(5, repository.findById(vaca.getId()).orElseThrow().getVida());
    }

    @Test
    void elEscenarioFelizPersisteLaJugadaSoloDespuesDeAmbasAceptaciones() {
        guardarSteve("escudo");
        PersonajeJugable jugador = guardarSteve("escudo", "escudo");
        Monstruo creeper = guardarCreeper();
        ReglaExternaClient cliente = clienteQueAcepta();

        EscenarioResponse respuesta = escenarioCon(cliente).ejecutar(
                new EscenarioRequest(jugador.getId(), creeper.getId(), 6, "explosión válida"));

        assertTrue(respuesta.aceptadaPorA2());
        assertTrue(respuesta.aceptadaPorA3());
        assertEquals(2, respuesta.escudosConsumidos());
        assertEquals(20, respuesta.vidaJugadorDespues());
        assertEquals(1, jugadaRepository.count());
        Jugada guardada = jugadaRepository.findAll().get(0);
        assertEquals(jugador.getId(), guardada.getJugadorId());
        assertEquals(creeper.getId(), guardada.getMonstruoId());
        verify(cliente).validar(eq(Companero.A2), any());
        verify(cliente).validar(eq(Companero.A3), any());
    }

    @Test
    void rechazoDeA2NoGuardaYNombraAlCompanero() {
        PersonajeJugable jugador = guardarSteve();
        Monstruo creeper = guardarCreeper();
        ReglaExternaClient cliente = mock(ReglaExternaClient.class);
        when(cliente.validar(eq(Companero.A2), any()))
                .thenReturn(new ValidacionExternaResponse(false, "sin escudos", "SIN_ESCUDO"));

        RechazoExternoException ex = assertThrows(RechazoExternoException.class,
                () -> escenarioCon(cliente).ejecutar(
                        new EscenarioRequest(jugador.getId(), creeper.getId(), 6, "rechazo A2")));

        assertEquals("A2", ex.getCompanero());
        assertEquals("SIN_ESCUDO", ex.getCodigoExterno());
        assertEquals(0, jugadaRepository.count());
        assertEquals(20, repository.findById(creeper.getId()).orElseThrow().getVida());
    }

    @Test
    void rechazoDeA3NoGuardaYNombraAlCompanero() {
        PersonajeJugable jugador = guardarSteve("escudo");
        Monstruo creeper = guardarCreeper();
        ReglaExternaClient cliente = mock(ReglaExternaClient.class);
        when(cliente.validar(eq(Companero.A2), any()))
                .thenReturn(new ValidacionExternaResponse(true, "A2 aceptó", "OK_A2"));
        when(cliente.validar(eq(Companero.A3), any()))
                .thenReturn(new ValidacionExternaResponse(false, "potencia alta", "POTENCIA_ALTA"));

        RechazoExternoException ex = assertThrows(RechazoExternoException.class,
                () -> escenarioCon(cliente).ejecutar(
                        new EscenarioRequest(jugador.getId(), creeper.getId(), 10, "rechazo A3")));

        assertEquals("A3", ex.getCompanero());
        assertEquals(0, jugadaRepository.count());
        assertEquals(20, repository.findById(creeper.getId()).orElseThrow().getVida());
    }

    @Test
    void miReglaDeCaidaCalculaElDanioDespuesDeTresBloques() {
        VeredictoRegla verdict = reglaService.danioCaida(new CaidaRequest(12.0, 2.0, 7));

        assertTrue(verdict.aceptada());
        assertEquals(10, verdict.bloquesCaidos());
        assertEquals(7, verdict.danioCalculado());
        assertEquals("danio-caida", verdict.regla());
    }

    @Test
    void miReglaDeCaidaRechazaDanioDeclaradoIncorrecto() {
        ReglaRechazadaException ex = assertThrows(ReglaRechazadaException.class,
                () -> reglaService.danioCaida(new CaidaRequest(12.0, 2.0, 5)));

        assertEquals("REGLA_RECHAZADA", ex.getCodigo());
    }

    @Test
    void miRegla2CalculaElDanioDespuesDeLosEscudos() {
        VeredictoRegla verdict = reglaService.absorcionEscudo(
                new AbsorcionEscudoRequest(10, 2, 0));

        assertTrue(verdict.aceptada());
        assertEquals(2, verdict.escudosConsumidos());
        assertEquals(0, verdict.danioCalculado());
    }

    @Test
    void miRegla2RechazaDanioDeclaradoIncorrecto() {
        ReglaRechazadaException ex = assertThrows(ReglaRechazadaException.class,
                () -> reglaService.absorcionEscudo(new AbsorcionEscudoRequest(10, 2, 3)));

        assertEquals("REGLA_RECHAZADA", ex.getCodigo());
    }

    @Test
    void elClienteA2LeeSoloElFormatoDeSuOpenAPI() throws IOException {
        AtomicReference<String> recibido = new AtomicReference<>();
        ReglaExternaClient cliente = clienteCon(200, "{\"aceptada\":true}", recibido);

        assertThrows(IntegracionExternaException.class,
                () -> cliente.validar(Companero.A2, payloadDeValidacion()));
        assertTrue(recibido.get().contains("\"radio\""));
        assertTrue(recibido.get().contains("\"x\""));
    }

    @Test
    void elClienteA2AceptaElResultadoDeExplosionYTraduceSuProblema() throws IOException {
        AtomicReference<String> recibido = new AtomicReference<>();
        String resultado = "{\"x\":0.0,\"y\":0.0,\"z\":0.0,\"radio\":6.0,\"danioMaximo\":20.0,"
                + "\"totalAfectados\":0,\"afectados\":[]}";
        ValidacionExternaResponse decision = clienteCon(200, resultado, recibido)
                .validar(Companero.A2, payloadDeValidacion());
        assertTrue(decision.aceptada());

        String problema = "{\"detail\":\"El radio debe ser mayor que 0 y como máximo 100\","
                + "\"instance\":\"/api/explosiones\",\"status\":422,\"title\":\"Unprocessable Content\"}";
        RechazoExternoException ex = assertThrows(RechazoExternoException.class,
                () -> clienteCon(422, problema, new AtomicReference<>())
                        .validar(Companero.A2, payloadDeValidacion()));
        assertEquals("HTTP_422", ex.getCodigoExterno());
        assertEquals(422, ex.getStatusExterno());
    }

    @Test
    void elClienteA3ExigeElVeredictoExactoYTraduceSuRechazo() throws IOException {
        AtomicReference<String> recibido = new AtomicReference<>();
        String veredicto = "{\"regla\":\"onda-expansiva\",\"aceptada\":true,\"danioCalculado\":5,"
                + "\"bloquesNecesarios\":null,\"motivo\":\"daño calculado\"}";
        ValidacionExternaResponse decision = clienteCon(200, veredicto, recibido)
                .validar(Companero.A3, payloadDeValidacion());
        assertTrue(decision.aceptada());
        assertEquals("daño calculado", decision.motivo());
        assertTrue(recibido.get().contains("\"potencia\""));
        assertTrue(recibido.get().contains("\"distancia\""));

        String error = "{\"error\":\"REGLA_RECHAZADA\",\"excepcion\":\"ReglaRechazadaException\","
                + "\"estado\":400,\"mensaje\":\"fuera del radio\",\"ruta\":\"/api/reglas/onda-expansiva\","
                + "\"momento\":\"2026-11-23T21:22:11-03:00\"}";
        RechazoExternoException ex = assertThrows(RechazoExternoException.class,
                () -> clienteCon(400, error, new AtomicReference<>())
                        .validar(Companero.A3, payloadDeValidacion()));
        assertEquals("REGLA_RECHAZADA", ex.getCodigoExterno());
    }

    @Test
    void unFormatoDeErrorDistintoSeConsideraFalloDeIntegracion() throws IOException {
        ReglaExternaClient cliente = clienteCon(400, "{\"title\":\"Bad Request\"}", new AtomicReference<>());

        IntegracionExternaException ex = assertThrows(IntegracionExternaException.class,
                () -> cliente.validar(Companero.A3, payloadDeValidacion()));
        assertEquals("A3", ex.getCompanero());
    }
}

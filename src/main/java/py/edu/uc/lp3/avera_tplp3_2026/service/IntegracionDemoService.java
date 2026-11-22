package py.edu.uc.lp3.avera_tplp3_2026.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import py.edu.uc.lp3.avera_tplp3_2026.domain.Explosion;
import py.edu.uc.lp3.avera_tplp3_2026.domain.Monstruo;
import py.edu.uc.lp3.avera_tplp3_2026.domain.PersonajeJugable;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.ExternalObjectResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.IntegracionDemoRequest;
import py.edu.uc.lp3.avera_tplp3_2026.rest.dto.integracion.IntegracionDemoResponse;
import py.edu.uc.lp3.avera_tplp3_2026.rest.integration.ExternalObjectClient;

@Service
public class IntegracionDemoService {

    private static final Logger LOG = LogManager.getLogger(IntegracionDemoService.class);

    private final ExternalObjectClient client;

    public IntegracionDemoService(ExternalObjectClient client) {
        this.client = client;
    }

    public IntegracionDemoResponse ejecutar(IntegracionDemoRequest request) {
        if (request == null || request.objetoId() == null || request.objetoId().isBlank()) {
            throw new IllegalArgumentException("El campo objetoId es obligatorio");
        }
        List<String> pasos = new ArrayList<>();

        ExternalObjectResponse objeto = client.buscar(request.objetoId().trim());
        if (objeto == null) {
            throw new IllegalStateException("La API externa no devolvió el objeto " + request.objetoId());
        }
        Map<String, Object> datos = objeto.data() == null ? Map.of() : objeto.data();
        pasos.add("Objeto externo leído: " + objeto.id() + " - " + objeto.name());
        LOG.debug("Paso 3: objeto externo {} tiene datos {}", objeto.id(), datos);

        int potencia = request.potencia() > 0 ? request.potencia() : potenciaSugerida(datos);
        String nombreExterno = objeto.name() == null || objeto.name().isBlank()
                ? "Creeper importado" : objeto.name();
        String nombreMonstruo = request.nombreMonstruo() == null || request.nombreMonstruo().isBlank()
                ? nombreExterno : request.nombreMonstruo();

        Monstruo monstruo = new Monstruo(20, nombreMonstruo, 1.0, false, true, false,
                Math.min(Math.max(potencia, 1), 50));
        PersonajeJugable jugador = new PersonajeJugable("Jugador de prueba");
        for (int i = 0; i < Math.max(0, request.escudos()); i++) {
            jugador.agregarAlInventario("escudo");
        }
        pasos.add("Regla local preparada: " + nombreMonstruo + " con potencia " + potencia
                + " y " + request.escudos() + " escudos");
        LOG.debug("Paso 4: dominio local preparado monstruo={} potencia={} escudos={}",
                nombreMonstruo, potencia, request.escudos());

        Explosion explosion = monstruo.explotar(potencia);
        int escudosConsumidos = jugador.recibirExplosion(explosion.potencia(),
                "explosión importada de " + nombreExterno);
        pasos.add("Regla local aplicada: " + monstruo.sonidoDeExplosion());
        pasos.add("Escudos consumidos: " + escudosConsumidos + "; vida restante: " + jugador.getVida());
        LOG.debug("Paso 5: regla local aplicada, escudos={} vida={}", escudosConsumidos, jugador.getVida());

        return new IntegracionDemoResponse(
                objeto.id(), objeto.name(), datos, nombreMonstruo, potencia, escudosConsumidos,
                jugador.getVida(), monstruo.sonidoDeExplosion(), List.copyOf(pasos));
    }

    private int potenciaSugerida(Map<String, Object> datos) {
        Object valor = datos.get("potencia");
        if (valor instanceof Number numero) {
            return Math.min(Math.max(numero.intValue(), 1), 50);
        }
        return 8;
    }
}

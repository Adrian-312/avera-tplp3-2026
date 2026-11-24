package py.edu.uc.lp3.avera_tplp3_2026.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import py.edu.uc.lp3.avera_tplp3_2026.constants.Contacto;

@Configuration
public class OpenApiConfig {

    private static final String DESCRIPCION = """
            Servicio HTTP de **Minecraft** de Adrián Vera (LP3 - UC 2026). Guarda su \
            estado en H2, expone el CRUD de `EntidadViva` (jugador, monstruo, animal \
            y planta), la simulación de explosiones y el escenario cruzado de POO-09.

            ## Mis dos reglas del juego

            Las dos están publicadas en `/minecraft/reglas` para que otro servicio \
            las consulte sin leer mi código. Ninguna modifica la base.

            **Regla 1 - Condición previa.** Antes de una explosión, el jugador y el \
            monstruo deben existir en mi base, ser `PersonajeJugable` y `Monstruo` \
            respectivamente y estar vivos. `POST /minecraft/reglas/condicion-previa` \
            devuelve los ids, los tipos y las vidas; si algo falta, responde \
            `400 REGLA_RECHAZADA`. El escenario cruzado usa esta misma validación.

            **Regla 2 - Absorción de escudo.** Cada escudo del inventario absorbe \
            5 puntos de la onda y se consume:

                escudosConsumidos = min(escudosDisponibles, ceil(potencia / 5))
                danioCalculado   = max(0, potencia - escudosConsumidos * 5)
                vidaDespues      = max(0, vidaJugador - danioCalculado)

            `POST /minecraft/reglas/absorcion-escudo` calcula esos tres valores. Si \
            se manda un `danioDeclarado` distinto, responde `400 REGLA_RECHAZADA`.

            ## Escenario cruzado

            `POST /minecraft/escenarios/explosion-validada` comprueba la condición \
            previa, consulta a **A2 (Yamil Padilla)** en `/api/explosiones` y a \
            **A3 (Javier Atlasovich)** en `/api/reglas/onda-expansiva`, ejecuta la \
            simulación local y guarda la `Jugada` solo si los dos aceptan. \
            `GET /minecraft/escenarios` muestra el historial; un rechazo no agrega filas.

            ## Errores

            Todos los errores llevan `timestamp`, `status`, `codigo`, `mensaje` y `path`.

            | Código | HTTP | Cuándo |
            |---|---|---|
            | `ENTIDAD_INVALIDA` | 400 | un dato rompe un invariante o el tipo no es el esperado |
            | `ENTIDAD_NO_ENCONTRADA` | 404 | el id no existe |
            | `ENTIDAD_MUERTA` | 409 | se intenta revivir una entidad muerta por `PUT`/`PATCH` |
            | `REGLA_RECHAZADA` | 400 | una de mis dos reglas rechaza la jugada propuesta |
            | `CUERPO_ILEGIBLE` | 400 | el JSON de la petición no es válido |
            | `RECHAZO_A2` / `RECHAZO_A3` | 400 | el compañero contestó que no; el mensaje dice cuál |
            | `FALLO_A2` / `FALLO_A3` | 502 | el compañero no respondió o cambió su contrato |

            ## Consola de la base

            `/h2-console`, con JDBC URL `jdbc:h2:file:./data/minecraft;AUTO_SERVER=TRUE`, \
            usuario `sa` y contraseña vacía. Para empezar de cero, detener la app y borrar `data/`.
            """;

    @Value("${app.puerto}")
    private String puerto;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Minecraft - Adrián Vera (LP3 UC 2026)")
                        .version("POO-09")
                        .description(DESCRIPCION)
                        .contact(new Contact().name(Contacto.A1).url(Contacto.GITHUB))
                        .license(new License().name("Apache License 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(new Server()
                        .url("http://localhost:" + puerto)
                        .description("Servicio A1 local")));
    }
}

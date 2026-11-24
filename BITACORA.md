# Bitácora de uso de IA

- **Asistente:** Claude (Anthropic), usado desde la interfaz web claude.ai.
- **Modelo de LLM:** Claude Sonnet 5.5
- **Fecha:** 9 de noviembre de 2026

## Resumen de prompts

1. Pasé el enunciado de POO-08, los 17 archivos Java del proyecto (dominio, excepciones y controllers) y pedí los pasos para completar la tarea y la corrección de lo que estuviera mal.
2. Subí `pom.xml`, `application.properties` y los DTO compilados, y pedí que me explicara cómo hacer cada pieza que faltaba (porque no sabía hacerlas) y si estaban bien las clases `Explosivo` y `Explosion`.
3. Pegué el error de compilación de Maven y el código de mi `GlobalExceptionHandler`; pedí corregir el conflicto.
4. Pedí ayuda con el `.gitignore`, con las URL para probar, con la consola H2 y con importar la colección en Insomnia (capturas de pantalla de los errores que me salían).
5. Pregunté si era correcto que el Creeper siga apareciendo en el listado después de explotar, pedí completar el README con lo que pedía la tarea y actualizar el diagrama de clases, cómo hacer el merge de mi rama `desarrollo` a `main` y si la tarea pedía un `RUN.md`.
6. Pedí separar la documentación: un `RUN.md` con las instrucciones de ejecución y un `README.md` sin la parte de entregas, y que corrigiera ambos.
7. Pegué la devolución de mi docente (faltaba la capa service, las transacciones estaban en el controller y el dominio lanzaba excepciones) junto con la estructura de carpetas del template (`constants`, `domain`, `exception`, `rest/controller`, `repository`, `service/impl`) y pedí el refactor.
8. Pegué el error de compilación de un test viejo (`SimulacionDomainTest`) y pedí cómo dejar un único test llamado `ApplicationTest.java`, como pide el template.

## Qué aportó la IA

- Revisión del código existente contra las consignas del enunciado y lista de correcciones:
  - `listar` usaba una consulta "mayor que" para un parámetro llamado `vidaMinima`, y ignoraba `vidaMinima` si también venía `nombre`.
  - `quitarDelInventario` distinguía mayúsculas y `consumirItem` no.
  - Se quitaron los endpoints con verbo en la URL (`/crear-jugador`, `/crear-monstruo`, `/danio`), que no seguían la convención REST.
- Código nuevo: `EntidadVivaRepository`. El `GlobalExceptionHandler` y el `ErrorResponse` ya existían en mi proyecto; la IA asumió que faltaban y los pisó, lo que causó un error de compilación. Se corrigió dejando un `ErrorResponse` (con `timestamp`, `status`, `codigo`, `mensaje` y `path`) y agregando a mi handler el manejo del JSON ilegible.
- Refactor a tres capas según el template: `service` e `impl` con la lógica, las validaciones y las transacciones; controllers delgados; dominio solo con estado; carpetas `constants` y `exception`. Se verificó con un compilador y con una prueba de la lógica de los services en memoria.
- Un único `ApplicationTest.java` (prueba de integración con H2 en memoria) que reemplaza a los tests viejos.
- Documentación y pruebas: la colección de Insomnia, el README (con diagrama Mermaid, invariantes y recorrido de prueba), esta bitácora y el documento de especificaciones.

## Qué hice yo

Revisé y probé lo generado: arranqué el servicio, recorrí la colección de Insomnia y comprobé en el listado que el estado se persiste después de la simulación. Resolví el conflicto del handler y subí los cambios al repositorio.

---

## Entrada del 17 de noviembre de 2026

- **Asistente:** Claude (Anthropic), usado desde la interfaz web claude.ai.
- **Modelo de LLM:** Claude Sonnet 5.5 (identificador `claude-sonnet-5-5`)
- **Fecha:** 17 de noviembre de 2026

### Resumen de prompts

1. Pregunté cómo probar desde Insomnia después del refactor y si había que volver a importar la colección (respuesta: no; hay que reiniciar el servicio y borrar la carpeta `data/`).
2. Pegué la devolución de mi docente: un `PATCH` con `vida: 20` revive a un Creeper que ya explotó (el criterio quedó en 0; el service debe rechazar `PUT` y `PATCH` sobre una entidad muerta con un 409 y una excepción propia). Pedí cómo comprobar el problema y que lo corrigiera.
3. Pregunté cómo cambiar la URL del pedido que dupliqué en Insomnia (no me dejaba editar la variable `jugador_id`) y qué otros errores traía el problema del Creeper que revive.
4. Pregunté si la corrección cubría a todos los tipos de entidad, si arreglaba `GET /acciones` con las entidades muertas y en qué parte del código un `PUT` sin vida dejaba la vida en 20. Pedí un único paquete con todo: la regla de entidad muerta, el filtro de `/acciones` y `/explosivos`, y el `PUT` que exige `vida`.

### Qué aportó la IA

- Reprodujo el problema en mi código antes de corregirlo, con una prueba en memoria: un `PATCH` con `vida: 20` y un `PUT` sin `vida` dejaban al Creeper muerto con vida 20 y `vivo: true`.
- Auditó el código y listó otros fallos de la misma familia (estados imposibles con entidades muertas):
  - `GET /minecraft/acciones` y `GET /minecraft/explosivos` seguían listando a las entidades muertas (un Creeper muerto "rugía y perseguía").
  - Un `POST` con `vida: 0` crea una entidad que nace muerta.
  - Un `PUT` sin `vida` sobre una entidad viva la curaba a 20.
  - Un `PATCH` con `vida: 0` mata a una entidad sin pasar por las reglas del juego.
  - No hay control de concurrencia (`@Version`); este punto es teórico y no se probó.
- Código nuevo y cambios, siguiendo el template:
  - `EntidadMuertaException` (en `exception/`, código `ENTIDAD_MUERTA`) y un handler que la responde con 409 en `GlobalExceptionHandler`.
  - `EntidadServiceImpl`: `rechazarSiEstaMuerta` en `PUT` y `PATCH` (la regla vive en el service, no en la Entity) y `PUT` que exige `vida`.
  - `ComportamientoServiceImpl`: solo las entidades vivas figuran en `/acciones` y `/explosivos`.
  - `ApplicationTest.java`: cinco pruebas nuevas, 13 en total (sigue siendo un único archivo de test).
- Verificación: compilación del código y pruebas de la lógica en memoria con los cuatro tipos (jugador, monstruo, animal y planta). No pudo ejecutar Spring ni H2 en su entorno, así que la prueba real es `./mvnw test`.
- Error de la IA que debe quedar escrito: ni en su revisión inicial ni al hacer el refactor detectó que el CRUD permitía revivir una entidad muerta. Lo señaló mi docente, y la IA lo reconoció y lo corrigió recién después.

### Qué hice yo

- Reproduje el problema en Insomnia duplicando el pedido `PATCH` y comprobé que no solo el Creeper revivía: también se podía revivir a un animal y a un monstruo cualquiera.
- Decidí qué correcciones incluir en el paquete: la regla de entidad muerta con su 409, el filtro de `/acciones` y `/explosivos`, y el `PUT` que exige `vida`.

---

## Entrada del 22 de noviembre de 2026

- **Asistente:** OpenCode, usado desde la CLI en el repositorio local.
- **Modelo de LLM:** `opencode/big-pickle`
- **Fecha:** 22 de noviembre de 2026

### Resumen de prompts

1. Pedí completar POO-09 en el proyecto sin borrar archivos, sin hacer commits, manteniendo el template y dejando para después solo lo que depende del grupo (A2/A3).
2. Pedí continuar con los pasos pendientes cuando se detuvo la sesión anterior.

### Qué aportó la IA

- Leyó los enunciados de POO-09 y el estado del repositorio; identificó POO-08 y sus observaciones pendientes.
- Cerró en código las observaciones conocidas: `validarVida(1..20)`, `@Version`, `EntidadMuertaException` con 409, filtro de entidades muertas en `/acciones` y `/explosivos`, y `PUT` que exige `vida`.
- Agregó springdoc, el OpenAPI propio, anotaciones en los controllers y la sección de errores del escenario.
- Implementó el consumo de A2/A3 con `WebClient`, el escenario `POST /minecraft/escenarios/explosion-validada`, la entidad `Jugada`, el historial `GET /minecraft/escenarios` y los stubs temporales.
- Agregó tests para el camino feliz, el rechazo de A2 y el de A3. Verificación final: `./mvnw test` → 18 tests, 0 fallos, 0 errores.
- Levantó el servicio con H2 en memoria, recorrió el flujo con `curl` y capturó las respuestas reales para la colección de Insomnia.
- Dos correcciones durante la verificación: `ReglaExternaClient` dejó de compartir el `WebClient.Builder` entre A2 y A3, y `escudosConsumidos` pasó a informar solo los escudos del jugador del escenario (no el total de todas las entidades).
- Documentación nueva o actualizada: `README.md`, `RUN.md`, `docs/POO09-APLICADO.md`, `insomnia/poo09-insomnia.json`, `insomnia/README.md` y `insomnia/respuestas-ejemplo.md`. La colección de POO-08 se conservó sin cambios.
- Auditoría final: se corrigieron textos pre-refactor del README que todavía decían que las reglas y las excepciones vivían en el dominio, se completaron las respuestas de POO-09 a «Qué hay que pensar» y se ajustaron `RUN.md` y `docs/POO09-APLICADO.md` para que coincidan con el código. El código y los tests no se modificaron.
- No hizo commit: el enlace de entrega quedó como `https://github.com/Adrian-312/avera-tplp3-2026/commit/<HASH_DEL_COMMIT>`.

### Qué hice yo

- Elegí el escenario del Creeper, el orden A2 → A3 y las reglas de los rechazos (`RECHAZO_*` = 400, `FALLO_*` = 502).
- Confirmé que no se borrara ningún archivo y que la parte del grupo quedara marcada como **PENDIENTE** hasta tener los OpenAPI de A2 y A3.

---

## Entrada del 23 de noviembre de 2026

- **Asistente:** OpenCode, usado desde la CLI en el repositorio local.
- **Modelo de LLM:** `opencode/big-pickle`
- **Fecha:** 23 de noviembre de 2026

### Resumen de prompts

1. Dije que no entendía las reglas de juego del POO-09, pedí revisarlas contra el repo de referencia `https://github.com/javifacha123/ja-taller-git-2026`, dejar Swagger UI e Insomnia más completos y arreglar el `IndexController`, que aparecía en blanco. Si una regla no era coherente, pedí sacar el daño por caída y poner una regla que cerrara con el escenario.
2. Aclaré que las dos reglas debían ser: condición previa (jugador y monstruo existen, son del tipo correcto y están vivos antes de la explosión) y efecto sobre el jugador y sus escudos (daño de la explosión descontando escudos y calculando `vidaDespues`).
3. Pedí continuar con los pasos pendientes cuando se detuvo la sesión.

### Qué aportó la IA

- Leyó el repo de referencia y los enunciados del POO-09; no copió código del compañero, solo tomó la estructura de documentación y anotaciones.
- Eliminó `CaidaRequest`, el endpoint `danio-caida`, `ALTURA_SEGURA_CAIDA` y los campos `potenciaMaxima`/`bloquesCaidos` de `VeredictoRegla`.
- Agregó `CondicionPreviaRequest`, `CondicionPreviaService` y `CondicionPreviaServiceImpl`. Esa validación es la REGLA 1 y la reutiliza `EscenarioServiceImpl` antes de consultar A2 y A3.
- Reformuló la REGLA 2 con `vidaJugador`, `escudosConsumidos`, `danioCalculado` y `vidaDespues`, con `ceil(potencia / 5)` como consumo de escudos.
- Documentó `GET /` con `@Tag`, `@Operation` y `@ApiResponse`, y amplió `OpenApiConfig` con descripción de reglas, fórmulas, escenario, errores y H2.
- Actualizó tests, README (incluido el diagrama Mermaid), `RUN.md`, `docs/POO09-APLICADO.md`, `insomnia/minecraft-insomnia.json`, `insomnia/README.md` y `insomnia/respuestas-ejemplo.md`. El JSON de Insomnia se validó con `python3 -m json.tool`.
- Verificación: `./mvnw test` → 27 tests, 0 fallos, 0 errores. El primer intento con timeout de 300 s se cortó por lentitud del entorno, no por un test; con 900 s terminó en 1:36.
- Verificación extra: levantó A1 en el puerto 18080 con H2 en memoria y comprobó que `/v3/api-docs` expone `GET /` con resumen, las dos reglas y los seis tags esperados.
- No hizo commit: los cambios quedaron en el working tree para revisión.

### Qué hice yo

- Corregí el rumbo de las reglas: descarté el daño por caída y elegí la condición previa como primera regla.
- Acepté que la condición previa se publique sola y también se use dentro del escenario, para no duplicar validaciones.
- Revisé que Insomnia y la documentación ya no mencionaran la regla eliminada.
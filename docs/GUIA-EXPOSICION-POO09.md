# Guía de exposición — POO-09 APIs cruzadas (Minecraft)

**Alumno:** Adrian (GitHub: `Adrian-312`)  
**Rama:** `poo09-preparacion`  
**Uso:** documento para estudiar y exponer la defensa oral del challenge. No reemplaza al README ni a la bitácora; es una guía de apoyo.

---

## 0. Qué es lo que se pide, explicado simple

La cátedra pide que cada alumno construya **su propio escenario** usando **dos APIs de compañeros**.

Vocabulario básico:

| Término | Significado en este trabajo |
|---|---|
| **A1** | Mi servicio. Es el que arma el escenario y guarda la jugada. |
| **A2** | Compañero número 1 al que yo consulto. |
| **A3** | Compañero número 2 al que yo consulto. |
| **OpenAPI** | Un documento JSON que describe mi API: rutas, verbos, cuerpos y errores. Un compañero lo lee sin ver mi código. |
| **Swagger UI** | La página web que muestra ese documento de forma visual y permite probar endpoints. |
| **Round robin** | Cada alumno consume dos APIs ajenas y publica una propia. Yo consumo A2 y A3; otros compañeros me consumen a mí. |
| **WebClient** | La librería de Spring que usa mi código para hablar por HTTP con otros servicios. |
| **Escenario persistido** | Un pedido REST que, si A2 y A3 aceptan, guarda la jugada en mi H2 y se puede volver a leer con un `GET`. |

La nota de este ítem son **15 puntos**:

- **12 puntos** del challenge: OpenAPI, mapa del grupo, consumo de A2/A3 y escenario con persistencia.
- **3 puntos** de la corrección de POO-08: puntaje completo, u observaciones cerradas, o "no tengo documento" con el servicio andando.
- La **defensa oral** es otro ítem aparte de 10 puntos. Esta guía es para esa defensa.

**Estado real hoy:** la infraestructura está lista, pero todavía no están los nombres de A2 y A3 ni sus OpenAPI. Por eso se agregaron **stubs** (reemplazos temporales) para poder demostrar el mecanismo completo hoy mismo. Eso está permitido como preparación; el día de la entrega se cambian las URLs por las de los compañeros.

---

## 1. Qué había antes (POO-08) y qué se agregó ahora

Antes de este trabajo el proyecto ya tenía:

- Un servicio Spring Boot con dominio **Minecraft**.
- Una familia de entidades: `EntidadViva`, `PersonajeJugable`, `Monstruo`, `Animal`, `Planta`.
- Persistencia en **H2 en archivo** (`./data/minecraft.mv.db`).
- Un **CRUD REST** en `/minecraft/entidades`.
- Dos reglas del juego:
  1. El Creeper explota, muere y produce una onda expansiva.
  2. Cada escudo del inventario de un jugador absorbe 5 puntos de daño.
- Un endpoint `POST /minecraft/simulacion/explosion` que aplica esas reglas y guarda el resultado.
- Excepciones propias con respuestas HTTP 400 y 404.
- Colección de Insomnia y documentación.

Ahora se agregó **sin borrar nada de eso**:

| Capacidad nueva | Para qué sirve en POO-09 |
|---|---|
| OpenAPI y Swagger UI | Para que un compañero pueda importar mi API sin leer mi código. |
| WebClient | Para consultar APIs ajenas desde mi servicio. |
| URLs externalizables | Para cambiar de compañero o de puerto sin tocar Java. |
| Entidad `Jugada` | Para guardar el resultado del escenario en mi H2. |
| Cliente de A2 y A3 | Para enviar la jugada a validar y recibir aceptación o rechazo. |
| Stubs temporales | Para probar hoy los tres caminos: feliz, rechazo de A2 y rechazo de A3. |
| Log4j2 en DEBUG | Para seguir cada paso de la integración en la consola y en un archivo. |
| Colección Insomnia nueva | Para dejar armados los pedidos y las respuestas de ejemplo. |

---

## 2. Explicación paso a paso de los cambios

### Paso 1: dependencias nuevas (`pom.xml`)

Se agregaron tres dependencias:

1. **`springdoc-openapi-starter-webmvc-ui:3.2.0`**: genera el documento OpenAPI y la interfaz Swagger UI. La versión 3.2.0 corresponde a Spring Boot 4.1, que es la versión que usa el proyecto.
2. **`spring-boot-starter-webflux`**: trae `WebClient`. No cambia el servidor: el proyecto sigue siendo Servlet/MVC porque en `application.properties` se fuerza `spring.main.web-application-type=servlet`.
3. **`spring-boot-starter-log4j2`**: reemplaza al sistema de logs por defecto por Log4j2. Por eso se excluyó `spring-boot-starter-logging` de los starters existentes; si las dos librerías están juntas, Spring no sabe cuál usar.

Además se agregó un bean propio `WebClient.Builder` en `config/WebClientConfig.java`, porque en este proyecto con servlet no aparece automáticamente.

**Qué decir en la defensa:** "Agregué springdoc para publicar el OpenAPI, WebClient para consumir las APIs ajenas y Log4j2 para dejar rastro de cada paso. Tuve que excluir el logging por defecto para que no compitiera con Log4j2".

---

### Paso 2: configuración externalizable (`application.properties`)

Antes el puerto estaba fijo en 8080. Ahora:

```properties
app.puerto=${SERVER_PORT:8080}
server.port=${app.puerto}
```

Eso permite arrancar tres servicios en la misma máquina con `SERVER_PORT=8080`, `8081` y `8082`.

También se configuraron las URLs:

```properties
integracion.demo.base-url=${DEMO_API_URL:https://api.restful-api.dev}
integracion.a2.base-url=${A2_BASE_URL:http://localhost:${app.puerto}/integracion/stub}
integracion.a2.path=${A2_VALIDATE_PATH:/a2/validar}
integracion.a3.base-url=${A3_BASE_URL:http://localhost:${app.puerto}/integracion/stub}
integracion.a3.path=${A3_VALIDATE_PATH:/a3/validar}
```

Y los timeouts y reglas de los stubs:

```properties
integracion.client.timeout-seconds=${INTEGRACION_TIMEOUT_SECONDS:5}
integracion.demo.timeout-seconds=${DEMO_TIMEOUT_SECONDS:10}
integracion.stub.a2.escudos-minimos=${A2_ESCUDOS_MINIMOS:1}
integracion.stub.a3.potencia-maxima=${A3_POTENCIA_MAXIMA:8}
```

**Qué decir:** "Ninguna URL de un compañero está escrita en el código Java. Todas salen de propiedades o variables de entorno. Si un compañero cambia el puerto o la ruta, se cambia la configuración y no se recompila".

---

### Paso 3: OpenAPI y Swagger UI

Se agregó `config/OpenApiConfig.java`, que crea el bean `OpenAPI` con título, versión, contacto y el servidor local.

Se agregaron anotaciones a los controllers:

- `@Tag` en cada controller para agrupar operaciones en Swagger UI.
- `@Operation` y `@ApiResponse` en la simulación y en el escenario para explicar qué hace y qué códigos devuelve.

Resultado:

- Documento JSON: `http://localhost:8080/v3/api-docs`
- Interfaz visual: `http://localhost:8080/swagger-ui.html`

La documentación incluye CRUD, simulación, errores, integración de prueba y escenario. Un extraño puede importar el documento y saber cómo llamar mi API.

**Qué decir:** "Mi OpenAPI no es un archivo escrito a mano: lo genera springdoc a partir de los controllers. Las anotaciones sólo mejoran las descripciones y los códigos de respuesta".

---

### Paso 4: WebClient contra la API pública

Primero se probó el consumo con la API que circuló en la comisión: `https://api.restful-api.dev/objects`.

Flujo:

1. `IntegracionDemoController` recibe `POST /minecraft/integracion/objeto`.
2. `IntegracionDemoController` llama a `ExternalObjectClient.buscar(id)`.
3. `ExternalObjectClient` hace un `GET` con WebClient a `{base-url}/objects/{id}`.
4. El objeto devuelto (por ejemplo, `Google Pixel 6 Pro` con datos `color` y `capacity`) se usa para armar un `Monstruo` local.
5. Se aplican las reglas de POO-08: `explotar(potencia)` y `recibirExplosion` con escudos.
6. Se devuelve un JSON con los pasos, escudos consumidos y vida del jugador. Esa prueba **no persiste**.

Ejemplo real obtenido:

```json
{
  "objetoId": "1",
  "objetoNombre": "Google Pixel 6 Pro",
  "datosExternos": {"color": "Cloudy White", "capacity": "128 GB"},
  "monstruo": "Creeper importado",
  "potencia": 10,
  "escudosConsumidos": 2,
  "vidaJugador": 20
}
```

**Qué decir:** "Esto demuestra que sé consumir una API con WebClient y que puedo combinar lo que viene de afuera con las reglas que ya tenía en el dominio. Después reemplacé la URL pública por las URLs de A2 y A3".

---

### Paso 5: clientes hacia A2 y A3

`ReglaExternaClient.java` construye dos WebClient distintos: uno para A2 y otro para A3. Cada uno usa su propia base URL y su propia ruta.

`Companero.java` es un enum con `A2` y `A3` para no escribir strings sueltos.

El método principal es:

```java
validar(Companero companero, ValidacionExternaRequest payload)
```

Ese método:

1. Arma un `POST` con JSON al compañero indicado.
2. Loguea la URL y el payload.
3. Lee status y cuerpo de la respuesta.
4. Si el status es 400 o mayor, lanza `RechazoExternoException` indicando quién rechazó.
5. Si el service no responde, lanza `IntegracionExternaException`.

Los DTO son:

- `ValidacionExternaRequest`: jugador, vida, escudos disponibles, monstruo, potencia y escenario.
- `ValidacionExternaResponse`: `aceptada`, `motivo` y `codigo`.

**Punto importante para la defensa:** este contrato es provisional. El día del grupo se lee el OpenAPI real de cada compañero y se ajustan los nombres de los campos. No se copia código Java ajeno; sólo se traduce lo que dice el documento.

---

### Paso 6: escenario y persistencia

Este es el corazón del trabajo.

#### Entidad `Jugada`

Nueva tabla `jugada` con:

- id
- jugadorId y nombreJugador
- monstruoId y nombreMonstruo
- potencia
- escudosConsumidos
- vidaJugadorDespues
- aceptadaPorA2 y aceptadaPorA3
- motivo
- fecha

#### Servicio `EscenarioServiceImpl`

El método `ejecutar` sigue este orden:

1. Valida que vengan `jugadorId` y `monstruoId`.
2. Busca las entidades en el H2 de A1 y comprueba que sean realmente un jugador y un monstruo.
3. Cuenta los escudos del jugador y arma el payload común.
4. Consulta a **A2**. Si rechaza, lanza `RECHAZO_A2` y se detiene; no consulta A3.
5. Consulta a **A3**. Si rechaza, lanza `RECHAZO_A3`.
6. Si ambos aceptan, ejecuta la explosión local sobre todas las entidades vivas.
7. Guarda las entidades modificadas y la nueva `Jugada` con `saveAll` y `save`.
8. Devuelve un 201 con el detalle.

El método está anotado con `@Transactional`. Eso significa que los guardados ocurren como una sola unidad: si algo falla, la base no queda a medias. Además, como las validaciones externas ocurren **antes** de cualquier `save`, un rechazo no escribe nada.

#### Controller

- `POST /minecraft/escenarios/explosion-validada` → ejecuta el escenario.
- `GET /minecraft/escenarios` → lista las jugadas guardadas y permite comprobar que los rechazos no agregaron filas.

#### Errores

`GlobalExceptionHandler` traduce:

| Excepción | Código HTTP | `codigo` en el JSON |
|---|---|---|
| `RechazoExternoException` | 400 | `RECHAZO_A2` o `RECHAZO_A3` |
| `IntegracionExternaException` | 502 | `FALLO_A2` o `FALLO_A3` |

**Qué decir:** "Mi escenario consulta primero a A2 porque A2 revisa los escudos, que son el estado previo del jugador. Si no hay escudos, no tiene sentido preguntarle la potencia a A3. Si A3 rechaza, el error dice claramente que fue A3 y la base queda igual".

---

### Paso 7: stubs temporales

`ValidacionStubController.java` expone:

- `POST /integracion/stub/a2/validar`: rechaza si `escudosDisponibles < integracion.stub.a2.escudos-minimos`.
- `POST /integracion/stub/a3/validar`: rechaza si `potencia > integracion.stub.a3.potencia-maxima`.

No son los compañeros reales. Son un reemplazo para poder demostrar hoy:

- camino feliz,
- rechazo de A2,
- rechazo de A3.

Cuando existan los OpenAPI reales, se cambian `A2_BASE_URL` y `A3_BASE_URL`; los stubs quedan documentados como prueba local y dejan de usarse.

**Qué decir:** "Los stubs no son la entrega final, son el banco de pruebas. Me permitieron verificar la lógica de persistencia y el mensaje de rechazo antes de conocer a los compañeros".

---

### Paso 8: Log4j2 en nivel DEBUG

`src/main/resources/log4j2.xml` configura:

- DEBUG para `integration` y `service.impl`.
- INFO para el resto.
- Salida en consola y en `logs/integracion.log`.

Se registran:

- URL y payload enviados a cada compañero.
- status y cuerpo recibidos.
- decisión de A2 y A3.
- vidas antes de la explosión.
- jugada persistida y cantidad de filas antes y después.

**Qué decir:** "Si el profesor me pregunta cómo sé de quién fue el rechazo o por qué la base no cambió, lo muestro en los logs y en el `GET /minecraft/escenarios`".

---

### Paso 9: Insomnia

La colección nueva es `insomnia/poo09-preparacion.json`. Trae grupos:

1. OpenAPI y Swagger UI.
2. WebClient con API pública.
3. Preparar entidades en H2.
4. Camino feliz.
5. Rechazos por compañero.

Incluye respuestas de ejemplo en la descripción de cada pedido, por si un compañero no está en línea el día de la corrección.

**Qué decir:** "La colección deja armados los tres caminos y las respuestas reales que obtuve, para que se pueda corregir aunque un compañero no responda".

---

### Paso 10: documentación

Se agregaron sin reemplazar lo anterior:

- `docs/POO09-PREPARACION.md`: explicación técnica completa, mapa A1/A2/A3, diagramas Mermaid y checklist.
- `docs/POO09-APLICADO.md`: enunciado aplicado a la entrega, con campos `PENDIENTE`.
- `README.md`: sección 12 nueva (sólo se agregaron líneas).
- `BITACORA.md`: sección nueva con prompts, modelo usado y verificaciones.

---

## 3. Guion sugerido para exponer (aprox. 5 minutos)

> "Buen día. Mi trabajo es el challenge de APIs cruzadas de POO-09, sobre el dominio Minecraft. Partí de mi servicio de POO-08, que ya tenía el CRUD, el H2 y las dos reglas de explosiones y escudos.
>
> Lo primero que hice fue publicar mi OpenAPI con springdoc. Está en `/v3/api-docs` y el Swagger UI en `/swagger-ui.html`. Ahí se pueden ver el CRUD, la simulación, sus errores y el escenario nuevo.
>
> Después agregué WebClient. Primero lo probé contra la API pública de objetos: mi servicio consulta un objeto, usa su nombre y sus datos para armar un monstruo local y aplica las reglas de explosión y escudos. Eso demuestra el consumo HTTP y la combinación con mi dominio.
>
> Luego preparé los clientes hacia A2 y A3. Las URL, las rutas y los timeouts están en `application.properties` con variables de entorno, así que no hay nada hardcodeado. Como todavía no tengo los nombres de mis compañeros, dejé dos stubs que respetan el contrato y me permiten probar el mecanismo completo.
>
> El escenario es una explosión validada: mi servicio carga el jugador y el monstruo de mi H2, consulta primero a A2 por los escudos y después a A3 por la potencia. Si los dos aceptan, aplico la explosión, guardo las entidades y guardo una fila en la tabla `jugada`. Si A2 o A3 rechazan, devuelvo un 400 con `RECHAZO_A2` o `RECHAZO_A3` y no escribo nada. Lo verifico con un `GET` a `/minecraft/escenarios`.
>
> También agregué Log4j2 en DEBUG para ver cada paso de la integración, y una colección de Insomnia con el feliz y un rechazo por compañero, con respuestas de ejemplo.
>
> Lo que falta para la entrega final es completar los nombres y OpenAPI reales de A2 y A3, ajustar los DTO a esos documentos, definir el estado de la corrección de POO-08 y pegar el enlace al commit final. La infraestructura de mi repositorio ya está lista para eso."

Corregir la palabra "verify" por "verifico" al leer en voz alta.

---

## 4. Demo en vivo, paso a paso

Si el profesor pide que se muestre, seguir este orden.

### 4.1 Arrancar y mostrar OpenAPI

```bash
./mvnw spring-boot:run
```

Abrir:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

Mostrar que aparecen los tags: CRUD, simulación, integración, escenario y stubs.

### 4.2 Mostrar el WebClient

```bash
curl -X POST http://localhost:8080/minecraft/integracion/objeto \
  -H 'Content-Type: application/json' \
  -d '{"objetoId":"1","nombreMonstruo":"Creeper importado","potencia":10,"escudos":2}'
```

Esperar `200` con `objetoNombre=Google Pixel 6 Pro` y `escudosConsumidos=2`.

### 4.3 Preparar entidades

Crear un jugador con dos escudos y un monstruo. Copiar los ids devueltos. En la máquina de prueba fueron 6 y 7.

```bash
curl -X POST http://localhost:8080/minecraft/entidades -H 'Content-Type: application/json' \
  -d '{"tipo":"jugador","nombre":"SteveA1","vida":20,"altura":1.8,"hambre":20,"controlable":true,"inventario":["escudo","escudo"]}'

curl -X POST http://localhost:8080/minecraft/entidades -H 'Content-Type: application/json' \
  -d '{"tipo":"monstruo","nombre":"CreeperA1","vida":20,"hostil":true,"potenciaExplosiva":10}'
```

### 4.4 Camino feliz

```bash
curl -i -X POST http://localhost:8080/minecraft/escenarios/explosion-validada \
  -H 'Content-Type: application/json' \
  -d '{"jugadorId":6,"monstruoId":7,"potencia":6,"motivo":"explosión válida"}'

curl http://localhost:8080/minecraft/escenarios
```

Mostrar `201`, `aceptadaPorA2=true`, `aceptadaPorA3=true` y la jugada en el `GET`.

### 4.5 Rechazo de A2

Crear un jugador sin escudo y un monstruo nuevo, luego llamar al escenario con potencia 6.

Resultado esperado:

```json
{"status":400,"codigo":"RECHAZO_A2","mensaje":"Rechazo de A2: A2 exige al menos 1 escudo(s)... (código externo SIN_ESCUDO)"}
```

Después `GET /minecraft/escenarios`: la cantidad de jugadas no cambió.

### 4.6 Rechazo de A3

Crear un jugador con escudo y un monstruo nuevo, luego llamar con `potencia: 10`.

Resultado esperado:

```json
{"status":400,"codigo":"RECHAZO_A3","mensaje":"Rechazo de A3: A3 admite como máximo potencia 8 (código externo POTENCIA_ALTA)"}
```

Otra vez, el `GET` muestra la misma cantidad de jugadas.

### 4.7 Mostrar logs

Abrir la consola donde arrancó la aplicación o `logs/integracion.log` y buscar:

```text
Paso integración A2: armando POST ...
Paso integración A2: status=200 body=...
Paso integración A3: decisión aceptada=false ...
```

---

## 5. Preguntas probables y respuestas

### ¿Qué es OpenAPI y para qué sirve?

Es un documento, normalmente JSON, que describe mi API: rutas, verbos, parámetros, cuerpos y respuestas. Sirve para que otro compañero sepa cómo llamar mi servicio sin leer mi código. Swagger UI es sólo la vista web de ese documento.

### Si un compañero cambia el nombre de una ruta, ¿alcanza con actualizar el OpenAPI?

Sí, porque mi código no tiene copiado el dominio del compañero. La ruta sale de `A2_VALIDATE_PATH` o `A3_VALIDATE_PATH`. Si cambia el contrato de campos, ajusto los DTO `ValidacionExternaRequest` y `ValidacionExternaResponse` leyendo el nuevo OpenAPI. No tengo que clonar ni copiar clases Java ajenas.

### ¿Por qué consulto a A2 antes que a A3?

Porque A2 valida los escudos del jugador, que es un requisito previo. Si el jugador no tiene escudos, ya sabemos que la jugada es inválida y no tiene sentido consultar la potencia a A3. El orden está justificado en el README.

### ¿Cómo hago para que la base quede igual si rechazan?

Las llamadas externas ocurren antes de cualquier `save`. Si A2 o A3 rechazan, se lanza una excepción y el método termina sin escribir. Además el método es `@Transactional`, así que los guardados del camino feliz son atómicos. Se comprueba con `GET /minecraft/escenarios`.

### ¿Qué pasa si un compañero está apagado?

WebClient tiene un timeout de 5 segundos. Si no responde, se lanza `IntegracionExternaException` y el handler devuelve `502` con `FALLO_A2` o `FALLO_A3`. Queda registrado en la bitácora y en la colección de Insomnia con respuestas de ejemplo.

### ¿Por qué usé WebClient y no RestTemplate?

WebClient es el cliente HTTP moderno de Spring, no bloqueante, y viene con el starter de WebFlux. En este proyecto lo uso de forma síncrona con `.block(...)` para que el controller REST sea fácil de seguir, pero la librería permite después pasar a un flujo reactivo si hiciera falta.

### ¿Qué es un stub y por qué lo usé?

Es un reemplazo temporal de A2 y A3. Como todavía no tengo sus OpenAPI, los stubs me dejan probar hoy el feliz y cada rechazo. No son la entrega final: se cambian por las URLs reales con variables de entorno.

### ¿Qué guardo en H2 cuando la jugada es aceptada?

Guardo dos cosas: las entidades modificadas por la explosión (vida e inventarios actualizados) y una fila en la tabla `jugada` con los ids, la potencia, los escudos consumidos y las aceptaciones de A2 y A3.

### ¿Cómo hice las URLs externalizables?

Con propiedades que leen variables de entorno, por ejemplo `A2_BASE_URL=${...}` y `SERVER_PORT`. También se pueden pasar como argumentos de Maven. Ninguna URL de compañero está fija en una clase Java.

### ¿Para qué sirve el log4j2 en DEBUG?

Para auditar la integración: se ve la URL llamada, el payload enviado, el status y cuerpo recibidos, la decisión de cada compañero y el momento en que se persiste. Está en consola y en `logs/integracion.log`.

### ¿Qué cambia exactamente el día del grupo?

Se completan nombre, base URL y OpenAPI de A2 y A3; se ajustan los DTO a sus contratos; se ejecuta la colección Insomnia con los servicios reales; se guardan las respuestas de ejemplo; se define el estado de POO-08 y se pega el enlace al commit final. El código de persistencia y el mecanismo de rechazo no cambian.

### ¿Cómo se relaciona esto con POO-08?

POO-08 dio las reglas del dominio y la simulación local. POO-09 no las reemplaza: las reutiliza dentro del escenario cruzado. La explosión que antes hacía un solo servicio ahora sólo se persiste si dos APIs ajenas la validan.

### ¿Qué pasa con los 3 puntos de la corrección de POO-08?

Hay que escribir en el README una de las tres situaciones: puntaje completo 24/24, observaciones todas cerradas, o "no tengo documento de corrección" con el servicio POO-08 funcionando. Eso está marcado como PENDIENTE en la sección 12.1 del README.

---

## 6. Mapa rápido de archivos para saber qué mostrar

| Archivo | Qué mostrar de él |
|---|---|
| `pom.xml` | Dependencias springdoc, WebFlux y Log4j2; exclusión de logging. |
| `src/main/resources/application.properties` | Puertos, URLs A2/A3, timeouts y reglas de stubs. |
| `config/OpenApiConfig.java` | Bean con título, versión y servidor del OpenAPI. |
| `rest/controller/SimulacionController.java` | Anotaciones `@Operation` y `@ApiResponse`. |
| `constants/ApiPaths.java` | Rutas REST centralizadas del template. |
| `integration/ExternalObjectClient.java` | El `GET` con WebClient a la API pública. |
| `service/impl/IntegracionDemoServiceImpl.java` | Cómo se combinan datos externos con `Monstruo.explotar` y escudos. |
| `integration/ReglaExternaClient.java` | Los dos WebClient y el manejo de rechazo/fallo. |
| `service/impl/EscenarioServiceImpl.java` | El orden A2 → A3 → persistencia y el `@Transactional`. |
| `rest/controller/EscenarioController.java` | Los dos endpoints del escenario; delegan en el servicio. |
| `domain/Jugada.java` | La entidad que se guarda en H2. |
| `rest/dto/` | DTOs del escenario y de las validaciones externas. |
| `rest/exception/GlobalExceptionHandler.java` | `RECHAZO_A2`, `RECHAZO_A3`, `FALLO_A2`, `FALLO_A3`. |
| `rest/controller/ValidacionStubController.java` | Los reemplazos temporales. |
| `src/main/resources/log4j2.xml` | Niveles DEBUG y archivo `logs/integracion.log`. |
| `insomnia/poo09-preparacion.json` | Colección con feliz y rechazos. |
| `docs/POO09-PREPARACION.md` | Diagramas y detalle técnico. |
| `docs/POO09-APLICADO.md` | Enunciado aplicado y enlace al commit. |

---

## 7. Checklist antes de la exposición

- [ ] Arranca con `./mvnw spring-boot:run`.
- [ ] `./mvnw test` pasa.
- [ ] Abren `/swagger-ui.html` y `/v3/api-docs`.
- [ ] Tienen anotados los ids del jugador y monstruo que van a usar en la demo.
- [ ] Saben explicar qué hace A2 y qué hace A3 (aunque sean stubs).
- [ ] Saben decir que el orden es A2 primero, A3 después.
- [ ] Saben explicar que un rechazo no escribe en H2.
- [ ] Saben mostrar el `GET /minecraft/escenarios` antes y después.
- [ ] Saben explicar que las URLs son externalizables.
- [ ] Saben decir qué falta completar con los compañeros reales.
- [ ] Tienen el enlace al commit final listo para pegar en README y en `docs/POO09-APLICADO.md`.

---

## 8. Si se pierde durante la exposición

Decir esto y seguir por el diagrama de secuencia del camino feliz:

> "El cliente sólo habla con A1. A1 traduce el pedido, consulta a A2 y A3 usando sus OpenAPI, y recién guarda en su H2 si los dos aceptan. Si alguno rechaza, el error nombra al compañero y la base queda igual."

Esa frase resume todo el challenge.

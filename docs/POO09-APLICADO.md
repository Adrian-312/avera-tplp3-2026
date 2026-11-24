# POO-09 aplicado - APIs cruzadas (MInecraft)

- **Fecha:** 22/11/2026
- **Estudiante:** Adrián Vera (`Adrian-312`)
- **Commit de entrega:** `https://github.com/Adrian-312/avera-tplp3-2026/commit/<HASH_DEL_COMMIT>` (se completa después de hacer commit; no se hizo commit en esta sesión)
- **Documento base:** [Enunciado POO-09](https://raw.githubusercontent.com/alefq/afq-taller-git-2024/main/docs/ejercicio-poo-09-apis-cruzadas-2026-11-16.md)
- **Grupos:** [Reparto A2/A3](https://raw.githubusercontent.com/alefq/afq-taller-git-2024/main/docs/ejercicio-poo-09-grupos-2026-11-16.md)
- **Ejemplo de referencia:** [Ejemplo uno](https://raw.githubusercontent.com/alefq/afq-taller-git-2024/main/docs/ejercicio-poo-09-ejemplo-uno-2026-11-16.md)

## 1. Equipo de trabajo

| Grupo | Integrante | Responsabilidad en el escenario | Repositorio | Endpoint base | OpenAPI |
|---|---|---|---|---|---|
| A1 | Adrián Vera (`Adrian-312`) | Dueño del escenario; consulta a A2 y A3, simula la explosión y persiste la jugada. | `https://github.com/Adrian-312/avera-tplp3-2026` | `http://localhost:8080` | `http://localhost:8080/v3/api-docs` |
| A2 | Yamil Padilla (`Yamil-Padilla`) | Valida los parámetros de la explosión antes de la jugada. | `https://github.com/Yamil-Padilla/yp-taller-git-2026` | `http://localhost:8081` | `http://localhost:8081/v3/api-docs` |
| A3 | Javier Atlasovich (`javifacha123`) | Valida el daño de la onda expansiva sobre la entidad alcanzada. | `https://github.com/javifacha123/ja-taller-git-2026` | `http://localhost:8082` | `http://localhost:8082/v3/api-docs` |

A1 construyó el escenario completo y dejó los consumos de A2 y A3 parametrizados. Los cuerpos y los formatos de respuesta se tomaron de los OpenAPI reales de los compañeros; no hay aceptación tolerante ni alias de campos.

Tecnologías usadas por A1: Spring Boot 4.1.1, Java 21, Spring WebMVC, Spring WebFlux (`WebClient`), Spring Data JPA, H2 y springdoc-openapi 3.2.0.

## 2. Escenario elegido

Un **Creeper de A1** explota contra un jugador que lleva escudos. Antes de aplicar la explosión:

1. **A2** valida los parámetros geométricos de la explosión (`x`, `y`, `z`, `radio`) y responde con el daño máximo y las entidades afectadas.
2. **A3** valida la onda expansiva (`potencia`, `radio`, `distancia`) y responde si la regla es aceptada, el daño calculado y los bloques necesarios.
3. Solo si ambos aceptan, **A1** simula la explosión con `SimulacionService` y guarda una `Jugada` en H2.

El orden de consulta es A2 → A3. Si A2 rechaza, no se consulta A3. Cualquier rechazo ocurre antes de escribir, por lo que no se crea ninguna jugada. Si la simulación local falla, la transacción `@Transactional` hace rollback de la jugada.

Además del escenario cruzado, A1 publica dos reglas de juego propias: **condición previa** (jugador y monstruo existen, son `PersonajeJugable` y `Monstruo` y están vivos antes de la explosión) y **absorción de escudos** (cada escudo absorbe 5 puntos y se informa `vidaDespues`). Ambas devuelven el mismo formato `VeredictoRegla`. La condición previa la reutiliza el escenario antes de consultar A2 y A3; la absorción rechaza con `400 REGLA_RECHAZADA` cuando el daño declarado no coincide con el calculado.

## 3. Endpoint propio de A1

### `POST /minecraft/escenarios/explosion-validada`

```json
{
  "jugadorId": 1,
  "monstruoId": 2,
  "potencia": 6,
  "motivo": "explosión válida",
  "radio": 6.0,
  "distancia": 2.0
}
```

Respuesta `201` cuando A2 y A3 aceptan:

```json
{
  "jugadaId": 1,
  "jugadorId": 1,
  "monstruoId": 2,
  "potencia": 6,
  "aceptadaPorA2": true,
  "aceptadaPorA3": true,
  "escudosConsumidos": 2,
  "vidaJugadorDespues": 20,
  "detalle": "A2 y A3 aceptaron; la jugada quedó persistida"
}
```

### `GET /minecraft/escenarios`

Devuelve el historial de jugadas persistidas. Incluye nombres, potencia, escudos consumidos, vida final, aceptación de A2/A3, motivo y fecha.

## 4. Contrato enviado a A2 y A3

Los cuerpos se tomaron de los OpenAPI reales (`/v3/api-docs` de cada servicio) y son distintos. No se aceptan alias ni formatos alternativos: si falta un campo obligatorio, cambia el tipo, el `status` no coincide o el JSON es ilegible, `ReglaExternaClient` lanza `IntegracionExternaException` y A1 responde `502 FALLO_A2` o `502 FALLO_A3`.

### A2 — Yamil Padilla

`POST http://localhost:8081/api/explosiones`

```json
{
  "x": 0.0,
  "y": 0.0,
  "z": 0.0,
  "radio": 6.0
}
```

Aceptación (`200`), campos obligatorios:

```json
{
  "x": 0.0,
  "y": 0.0,
  "z": 0.0,
  "radio": 6.0,
  "danioMaximo": 20.0,
  "totalAfectados": 0,
  "afectados": []
}
```

Rechazo (`422`, `ProblemDetail`), campos obligatorios:

```json
{
  "type": "about:blank",
  "title": "Unprocessable Content",
  "status": 422,
  "detail": "El radio debe ser mayor que 0 y como máximo 100",
  "instance": "/api/explosiones"
}
```

A1 traduce el rechazo a `400 RECHAZO_A2` (código externo `HTTP_422`).

### A3 — Javier Atlasovich

`POST http://localhost:8082/api/reglas/onda-expansiva`

```json
{
  "potencia": 6,
  "radio": 6,
  "distancia": 2.0
}
```

Aceptación (`200`), campos obligatorios:

```json
{
  "regla": "onda-expansiva",
  "aceptada": true,
  "danioCalculado": 5,
  "bloquesNecesarios": null,
  "motivo": "A 2.0 bloques de un explosivo de potencia 6 y radio 6 corresponden 5 de daño"
}
```

Rechazo (`400`), campos obligatorios:

```json
{
  "error": "REGLA_RECHAZADA",
  "excepcion": "ReglaRechazadaException",
  "estado": 400,
  "mensaje": "La regla 'onda-expansiva' rechazó la jugada: La onda no alcanza: la distancia 14.14 es mayor que el radio 3, así que no corresponde daño",
  "ruta": "/api/reglas/onda-expansiva",
  "momento": "2026-11-22T12:00:00Z"
}
```

A1 traduce el rechazo a `400 RECHAZO_A3` (código externo `REGLA_RECHAZADA`).

### Reglas propias publicadas por A1

A1 publica sus propias reglas con un único formato de respuesta, `VeredictoRegla`: `regla`, `aceptada`, `jugadorId`, `monstruoId`, `tiposCorrectos`, `jugadorVivo`, `monstruoVivo`, `vidaJugador`, `vidaMonstruo`, `potencia`, `escudosDisponibles`, `escudosConsumidos`, `danioCalculado`, `vidaDespues` y `motivo`. Los campos que no aplican a una regla se devuelven como `null`.

| Endpoint | Entrada | Regla |
|---|---|---|
| `POST /minecraft/reglas/condicion-previa` | `jugadorId`, `monstruoId` | Ambos existen en A1, tienen el tipo correcto y están vivos antes de la explosión. |
| `POST /minecraft/reglas/absorcion-escudo` | `potencia`, `escudosDisponibles`, `vidaJugador`, `danioDeclarado` | Cada escudo absorbe 5 puntos: `escudosConsumidos = ceil(potencia / 5)`, `danioCalculado = max(0, potencia - 5 * escudosConsumidos)` y `vidaDespues = max(0, vidaJugador - danioCalculado)`. |

Ejemplo de condición previa: con ids `1` y `2` se devuelve `aceptada: true`, `tiposCorrectos: true` y `jugadorVivo`/`monstruoVivo` en `true`. Con un id inexistente, un tipo incorrecto o una entidad muerta, A1 responde `400 REGLA_RECHAZADA`. Ejemplo de absorción: potencia `10`, 2 escudos y vida `20` consumen 2 escudos, dejan `0` de daño y `vidaDespues: 20`; si `danioDeclarado` es `3`, A1 responde `400 REGLA_RECHAZADA`.

## 5. Mapeo de errores

| Situación | `codigo` | HTTP | Mensaje |
|---|---|---|---|
| A2 rechaza (`422`), o falta/cambia un campo obligatorio | `RECHAZO_A2` / `FALLO_A2` | 400 / 502 | `Rechazo de A2: ...` / `Fallo al consultar A2: ...` |
| A3 rechaza (`400`), o falta/cambia un campo obligatorio | `RECHAZO_A3` / `FALLO_A3` | 400 / 502 | `Rechazo de A3: ...` / `Fallo al consultar A3: ...` |
| Condición previa inválida (id ausente, tipo incorrecto o entidad muerta) | `REGLA_RECHAZADA` | 400 | `No existe ...`, `La entidad ... no es ...` o `El jugador y el monstruo deben estar vivos...` |
| Regla de absorción con daño declarado distinto del calculado | `REGLA_RECHAZADA` | 400 | `Con ... escudo(s) el daño es ...` |
| JSON ilegible en una regla propia | `CUERPO_ILEGIBLE` | 400 | `El cuerpo de la petición no es un JSON válido` |
| Entidad muerta en POO-08 | `ENTIDAD_MUERTA` | 409 | handler ya existente |

Todas las respuestas de error incluyen `timestamp`, `status`, `codigo`, `mensaje` y `path`.

## 6. Persistencia

La entidad `Jugada` se guarda en la tabla `jugada` con: `jugador_id`, `jugador_nombre`, `monstruo_id`, `monstruo_nombre`, `potencia`, `escudos_consumidos`, `vida_jugador_despues`, `aceptada_por_a2`, `aceptada_por_a3`, `motivo` y `fecha`.

Solo se guarda después de que A2 y A3 aceptan y la simulación termina correctamente. `escudosConsumidos` corresponde al jugador del escenario, no al total consumido en todas las entidades.

## 7. Cómo se probó

- `./mvnw test`: 27 tests en un único `ApplicationTest`, 0 fallos, 0 errores.
- Tests del escenario: camino feliz, rechazo de A2, rechazo de A3, persistencia condicional con un jugador extra y campos `radio`/`distancia` enviados a cada compañero.
- Tests estrictos de integración con un `HttpServer` local: cuerpo exacto enviado, campos obligatorios presentes, campos faltantes o con tipo incorrecto, `status`/`estado` coherentes y rechazos traducidos a excepciones de A1.
- Tests de reglas propias: condición previa aceptada y rechazada, absorción de escudos y daño declarado incorrecto.
- El `GET /` quedó documentado en OpenAPI con `@Tag`, `@Operation` y `@ApiResponse`; `OpenApiConfig` describe el escenario, las dos reglas, la tabla de errores y las credenciales de acceso.
- Los OpenAPI reales se verificaron levantando A2 en `8081` y A3 en `8082`; los ejemplos de este documento son las respuestas observadas, no suposiciones.
- La colección de Insomnia del repositorio es `insomnia/minecraft-insomnia.json` (POO-08); POO-09 se prueba con los endpoints documentados en `/v3/api-docs` y con los tests de integración.
- IMPORTANTE: los tests usan H2 en memoria; no modifican `./data/minecraft`.

## 8. Configuración de los compañeros reales

| Variable | Default | Uso |
|---|---|---|
| `A2_BASE_URL` | `http://localhost:8081` | URL base de A2. |
| `A2_VALIDATE_PATH` | `/api/explosiones` | Path de validación de explosiones de A2. |
| `A3_BASE_URL` | `http://localhost:8082` | URL base de A3. |
| `A3_VALIDATE_PATH` | `/api/reglas/onda-expansiva` | Path de la regla de onda expansiva de A3. |
| `INTEGRACION_TIMEOUT_SECONDS` | `5` | Timeout de `WebClient`. |

Ejemplo con los servicios reales corriendo:

```bash
A2_BASE_URL='http://localhost:8081' \
A2_VALIDATE_PATH='/api/explosiones' \
A3_BASE_URL='http://localhost:8082' \
A3_VALIDATE_PATH='/api/reglas/onda-expansiva' \
./mvnw spring-boot:run
```

## 9. Pendientes que no dependen de A1

1. Pegar el hash del commit en el enlace del encabezado.

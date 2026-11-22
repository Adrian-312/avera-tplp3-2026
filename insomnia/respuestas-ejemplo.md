# Respuestas de ejemplo - POO-09

Respuestas obtenidas el 22/11/2026 con el servicio levantado en H2 en memoria y los stubs locales de A2/A3. Los `timestamp` son ilustrativos; el contenido y los códigos son los que produce la aplicación.

## OpenAPI propio

`GET /v3/api-docs` responde `200` con `openapi: 3.1.0` y `info.title: POO-09 - Minecraft (A1)`. Rutas expuestas:

```text
/                                      get
/integracion/stub/a2/validar           post
/integracion/stub/a3/validar           post
/minecraft/acciones                    get
/minecraft/entidades                   get, post
/minecraft/entidades/{id}              get, put, patch, delete
/minecraft/escenarios                  get
/minecraft/escenarios/explosion-validada post
/minecraft/explosivos                  get
/minecraft/simulacion/explosion        post
```

`GET /swagger-ui.html` responde `200` después de la redirección de springdoc.

## Escenario feliz

`POST /minecraft/escenarios/explosion-validada` con:

```json
{
  "jugadorId": 1,
  "monstruoId": 2,
  "potencia": 6,
  "motivo": "explosión válida"
}
```

Respuesta `201`:

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

## Rechazo de A2

Mismo endpoint con `jugadorId = 4` (sin escudos), `monstruoId = 3`, `potencia = 6`. Respuesta `400`:

```json
{
  "timestamp": "2026-11-22T21:03:26.194450766Z",
  "status": 400,
  "codigo": "RECHAZO_A2",
  "mensaje": "Rechazo de A2: A2 exige al menos 1 escudo(s) antes de la explosión (código externo SIN_ESCUDO)",
  "path": "/minecraft/escenarios/explosion-validada"
}
```

A3 no fue consultado. No se persiste ninguna jugada.

## Rechazo de A3

Mismo endpoint con `jugadorId = 5` (1 escudo, creado después del camino feliz), `monstruoId = 3`, `potencia = 10`. A2 acepta y A3 rechaza. Respuesta `400`:

```json
{
  "timestamp": "2026-11-22T21:03:26.696199763Z",
  "status": 400,
  "codigo": "RECHAZO_A3",
  "mensaje": "Rechazo de A3: A3 admite como máximo potencia 8 (código externo POTENCIA_ALTA)",
  "path": "/minecraft/escenarios/explosion-validada"
}
```

No se persiste ninguna jugada.

## Verificación final

`GET /minecraft/escenarios` después de los dos rechazos:

```json
[
  {
    "jugadorId": 1,
    "jugadorNombre": "SteveA1",
    "monstruoId": 2,
    "monstruoNombre": "CreeperFeliz",
    "potencia": 6,
    "escudosConsumidos": 2,
    "vidaJugadorDespues": 20,
    "aceptadaPorA2": true,
    "aceptadaPorA3": true,
    "motivo": "explosión válida",
    "fecha": "2026-11-22T21:03:25.788515Z",
    "id": 1
  }
]
```

Sigue existiendo solo la jugada del escenario feliz: los rechazos no dejaron rastro.

## Fallos de comunicación (502)

Cuando un compañero no responde o responde de forma inválida, el patrón es:

```json
{
  "timestamp": "2026-11-22T21:05:00.000000000Z",
  "status": 502,
  "codigo": "FALLO_A2",
  "mensaje": "Fallo al consultar A2: ...",
  "path": "/minecraft/escenarios/explosion-validada"
}
```

Para `A3` el `codigo` es `FALLO_A3`. Estos casos no se capturaron con el servicio local porque los stubs siempre responden; se documentan por el handler implementado.

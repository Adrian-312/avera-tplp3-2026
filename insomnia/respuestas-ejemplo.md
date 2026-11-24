# Respuestas verificadas (POO-09)

Ejemplos obtenidos con A2 en `8081`, A3 en `8082` y A1 en `8080`. Los ids, nombres y timestamps pueden cambiar; lo estable es el contrato.

## A2 · `POST /api/explosiones`

Cuerpo: `{"x":0.0,"y":0.0,"z":0.0,"radio":6.0}` → `200`:

```json
{"x":0.0,"y":0.0,"z":0.0,"radio":6.0,"danioMaximo":20.0,"totalAfectados":0,"afectados":[]}
```

Cuerpo: `{"x":0.0,"y":0.0,"z":0.0,"radio":0.0}` → `422`:

```json
{"detail":"El radio debe ser mayor que 0 y como máximo 100","instance":"/api/explosiones","status":422,"title":"Unprocessable Content"}
```

## A3 · `POST /api/reglas/onda-expansiva`

Cuerpo: `{"potencia":6,"radio":6,"distancia":2.0}` → `200`:

```json
{"regla":"onda-expansiva","aceptada":true,"danioCalculado":5,"bloquesNecesarios":null,"motivo":"A 2.0 bloques de un explosivo de potencia 6 y radio 6 corresponden 5 de daño"}
```

Cuerpo: `{"potencia":12,"radio":3,"distancia":14.14}` → `400`:

```json
{"error":"REGLA_RECHAZADA","excepcion":"ReglaRechazadaException","estado":400,"mensaje":"La regla 'onda-expansiva' rechazó la jugada: La onda no alcanza: la distancia 14.14 es mayor que el radio 3, así que no corresponde daño","ruta":"/api/reglas/onda-expansiva","momento":"2026-11-23T21:41:30.984091822-03:00"}
```

## A1 · `POST /minecraft/escenarios/explosion-validada`

Con jugador id 1, Creeper id 2, `potencia: 6`, `radio: 6.0` y `distancia: 2.0` → `201`:

```json
{"jugadaId":1,"jugadorId":1,"monstruoId":2,"potencia":6,"aceptadaPorA2":true,"aceptadaPorA3":true,"escudosConsumidos":2,"vidaJugadorDespues":20,"detalle":"A2 y A3 aceptaron; la jugada quedó persistida"}
```

Con `radio: 0.0` → `400`:

```json
{"timestamp":"2026-11-23T22:10:00Z","status":400,"codigo":"RECHAZO_A2","mensaje":"Rechazo de A2: El radio debe ser mayor que 0 y como máximo 100 (código externo HTTP_422)","path":"/minecraft/escenarios/explosion-validada"}
```

Con `radio: 3.0` y `distancia: 14.14` → `400`:

```json
{"timestamp":"2026-11-23T22:11:00Z","status":400,"codigo":"RECHAZO_A3","mensaje":"Rechazo de A3: La regla 'onda-expansiva' rechazó la jugada: La onda no alcanza: la distancia 14.14 es mayor que el radio 3, así que no corresponde daño (código externo REGLA_RECHAZADA)","path":"/minecraft/escenarios/explosion-validada"}
```

En los dos rechazos `GET /minecraft/escenarios` conserva la misma cantidad de jugadas. Si falla la condición previa, el código es `REGLA_RECHAZADA` y tampoco se escribe.

## A1 · reglas propias

`POST /minecraft/reglas/condicion-previa` con `{"jugadorId":1,"monstruoId":2}` → `200`:

```json
{"regla":"condicion-previa","aceptada":true,"jugadorId":1,"monstruoId":2,"tiposCorrectos":true,"jugadorVivo":true,"monstruoVivo":true,"vidaJugador":20,"vidaMonstruo":20,"potencia":null,"escudosDisponibles":null,"escudosConsumidos":null,"danioCalculado":null,"vidaDespues":null,"motivo":"El jugador Alex y el monstruo CreeperEscenario existen, tienen el tipo correcto y están vivos antes de la explosión"}
```

Con `monstruoId` inexistente → `400`:

```json
{"timestamp":"2026-11-23T22:12:00Z","status":400,"codigo":"REGLA_RECHAZADA","mensaje":"No existe el monstruo 9999 en la base de A1","path":"/minecraft/reglas/condicion-previa"}
```

También rechaza si el id no es un `PersonajeJugable`/`Monstruo` o si alguno está muerto. No modifica la base.

`POST /minecraft/reglas/absorcion-escudo` con `{"potencia":12,"escudosDisponibles":3,"vidaJugador":20,"danioDeclarado":0}` → `200`:

```json
{"regla":"absorcion-escudo","aceptada":true,"jugadorId":null,"monstruoId":null,"tiposCorrectos":null,"jugadorVivo":null,"monstruoVivo":null,"vidaJugador":20,"vidaMonstruo":null,"potencia":12,"escudosDisponibles":3,"escudosConsumidos":3,"danioCalculado":0,"vidaDespues":20,"motivo":"Cada escudo absorbe 5 puntos: se consumen 3, llegan 0 de daño y la vida queda en 20"}
```

Con `danioDeclarado: 3` → `400`:

```json
{"timestamp":"2026-11-23T22:13:00Z","status":400,"codigo":"REGLA_RECHAZADA","mensaje":"Con 3 escudo(s) el daño es 0, no 3","path":"/minecraft/reglas/absorcion-escudo"}
```

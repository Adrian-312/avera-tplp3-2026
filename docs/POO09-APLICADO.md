# POO-09 — Enunciado aplicado a mi entrega

**Alumno:** Adrian (GitHub: `Adrian-312`)
**Dominio:** Minecraft
**Rama:** `poo09-preparacion`

> Documento preparado antes de conocer el grupo. Los campos **PENDIENTE** se completan con los datos de los compañeros y con el hash del commit final.

---

## 1. A1, A2 y A3

| Rol | Quién | Base URL | OpenAPI | Regla que aporta |
|---|---|---|---|---|
| **A1** | Adrian-312 (este repositorio) | `http://localhost:8080` | `http://localhost:8080/v3/api-docs` | Onda expansiva y escudos que absorben 5 puntos cada uno. |
| **A2** | **PENDIENTE** | **PENDIENTE** | **PENDIENTE** | **PENDIENTE** (por ahora, stub: exige al menos 1 escudo). |
| **A3** | **PENDIENTE** | **PENDIENTE** | **PENDIENTE** | **PENDIENTE** (por ahora, stub: exige potencia máxima 8). |

---

## 2. Escenario en una frase

Un Creeper de A1 explota contra un jugador con escudos; la jugada se guarda en el H2 de A1 solamente si A2 acepta los escudos del jugador y A3 acepta la potencia de la explosión.

---

## 3. Cómo probarlo

1. Arrancar A1 con `./mvnw spring-boot:run`. Sin los compañeros, los stubs locales contestan por A2 y A3.
2. Abrir `http://localhost:8080/swagger-ui.html` y comprobar que aparecen el CRUD, la simulación y el escenario.
3. Crear un jugador con dos escudos y un monstruo:

```bash
curl -X POST http://localhost:8080/minecraft/entidades -H 'Content-Type: application/json' \
  -d '{"tipo":"jugador","nombre":"SteveA1","vida":20,"altura":1.8,"hambre":20,"controlable":true,"inventario":["escudo","escudo"]}'

curl -X POST http://localhost:8080/minecraft/entidades -H 'Content-Type: application/json' \
  -d '{"tipo":"monstruo","nombre":"CreeperA1","vida":20,"hostil":true,"potenciaExplosiva":10}'
```

4. Camino feliz (A2 y A3 aceptan; se guarda):

```bash
curl -i -X POST http://localhost:8080/minecraft/escenarios/explosion-validada \
  -H 'Content-Type: application/json' \
  -d '{"jugadorId":6,"monstruoId":7,"potencia":6,"motivo":"explosión válida"}'

curl http://localhost:8080/minecraft/escenarios
```

5. Rechazo de A2 (jugador sin escudo): misma llamada con un `jugadorId` cuyo inventario esté vacío. Respuesta `400` con `codigo: RECHAZO_A2`.
6. Rechazo de A3 (potencia mayor que 8): misma llamada con `potencia: 10`. Respuesta `400` con `codigo: RECHAZO_A3`.
7. Repetir `GET /minecraft/escenarios`: la cantidad de jugadas no cambia después de los rechazos.
8. Importar `insomnia/poo09-preparacion.json` para ver todos los pedidos con sus respuestas de ejemplo.

---

## 4. Enlace al commit

```text
PENDIENTE: https://github.com/Adrian-312/avera-tplp3-2026/commit/<HASH_DEL_COMMIT>
```

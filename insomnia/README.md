# Colección Insomnia - POO-09

Archivo: `poo09-insomnia.json`. Contiene 15 requests organizados en 7 grupos. La colección anterior `minecraft-insomnia.json` (POO-08) se conservó sin cambios.

## Importar

1. En Insomnia: **Create / Import** → **Import File** → seleccionar `insomnia/poo09-insomnia.json`.
2. Queda activo el entorno **Base Environment** con `base_url = http://localhost:8080` y los ids esperados.
3. Si los ids no coinciden (porque la base no está vacía), actualizarlos en **Manage Environments** después del paso 1.

## Orden de ejecución

| Orden | Grupo | Objetivo |
|---|---|---|
| 0 | `00 - OpenAPI propio (A1)` | Verificar `/v3/api-docs` y `/swagger-ui.html`. |
| 1 | `01 - Setup (base limpia, ids 1 a 4)` | Crear jugador con 2 escudos, dos monstruos y jugador sin escudo. |
| 2 | `02 - Escenario feliz` | A2 y A3 aceptan; se crea la jugada y se consulta `GET /minecraft/escenarios`. |
| 3 | `03 - Rechazo de A2` | Sin escudos → `400 RECHAZO_A2`; no se consulta A3. |
| 4 | `04 - Rechazo de A3` | Crear primero el jugador con 1 escudo (id 5) y después enviar potencia 10 → `400 RECHAZO_A3`. |
| 5 | `05 - Verificación final` | `GET /minecraft/escenarios` sigue devolviendo solo la jugada feliz. |
| 6 | `06 - Stubs temporales (modo local)` | Probar directamente los reemplazos de A2 y A3. |

## Puntos importantes

- El jugador para el rechazo de A3 se crea **después** del escenario feliz. La simulación de A1 aplica la onda a todas las entidades vivas, por lo que un jugador creado antes habría consumido su escudo y A2 rechazaría primero.
- Los rechazos no ejecutan la simulación ni escriben en `jugada`: el `monstruoId = 3` sigue vivo y puede usarse en ambos rechazos.
- Las respuestas reales obtenidas con este orden están en `respuestas-ejemplo.md`.
- Los stubs son temporales. Cuando se conozcan los OpenAPI de A2 y A3, configurar `A2_BASE_URL`, `A2_VALIDATE_PATH`, `A3_BASE_URL` y `A3_VALIDATE_PATH`; el comportamiento de la aplicación no cambia.

## Arranque usado para capturar las respuestas

```bash
SERVER_PORT=8080 \
SPRING_DATASOURCE_URL='jdbc:h2:mem:poo09demo;DB_CLOSE_DELAY=-1' \
SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop \
SPRING_JPA_SHOW_SQL=false \
./mvnw spring-boot:run
```

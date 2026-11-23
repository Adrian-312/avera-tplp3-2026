# Especificaciones - Minecraft REST con H2 (POO-08)

**Commit de la solución:** `https://github.com/<usuario>/INICIALES-taller-git-2026/commit/<HASH_DEL_COMMIT>`

## Objetivo

Servicio HTTP con Spring Boot sobre el modelado de Minecraft que guarda su estado en H2. Ofrece un CRUD REST sobre las entidades vivas (padre `EntidadViva`, con hijas `PersonajeJugable` y `Monstruo`, además de `Animal` y `Planta`) y simula explosiones que cambian el estado de varios objetos a la vez. Los errores del dominio se traducen a HTTP con un handler por tipo de excepción.

Arranque: `./mvnw spring-boot:run` (puerto 8080, consola H2 en `/h2-console`).

## URLs

| Verbo y URL | Qué hace | Éxito |
|---|---|---|
| `GET /` | Servicio vivo | 200 |
| `POST /minecraft/entidades` | Crea jugador, monstruo, animal o planta (campo `tipo`) | 201 |
| `GET /minecraft/entidades` | Lista (filtros `nombre`, `vidaMinima`) | 200 |
| `GET /minecraft/entidades/{id}` | Devuelve una entidad | 200 |
| `PUT /minecraft/entidades/{id}` | Reemplaza la entidad completa | 200 |
| `PATCH /minecraft/entidades/{id}` | Cambia solo los campos enviados | 200 |
| `DELETE /minecraft/entidades/{id}` | Elimina la entidad | 204 |
| `GET /minecraft/explosivos` | Usa la interface `Explosivo` (jugadores y monstruos) | 200 |
| `GET /minecraft/acciones` | Acción especial de cada entidad | 200 |
| `POST /minecraft/simulacion/explosion` | Simula la explosión de un monstruo | 200 |

## Reglas e invariantes

- Vida entre 0 y 20; altura mayor que 0 y hasta 10; nombre no vacío.
- Hambre del jugador entre 0 y 20.
- Potencia explosiva de un monstruo entre 1 y 50.
- Una `Explosion` exige origen no vacío y potencia mayor que cero.
- **Regla 1 (`SimulacionServiceImpl`):** un monstruo vivo y hostil puede explotar con potencia entre 1 y su potencia máxima; al hacerlo se autodestruye.
- **Regla 2 (`SimulacionServiceImpl`):** cada escudo del jugador absorbe 5 puntos de la explosión y se consume; sin escudos el daño llega completo.
- "Autodestruirse" deja al monstruo con vida 0 y `vivo: false`; la fila no se borra (para eso está `DELETE`), por eso una segunda explosión con el mismo monstruo devuelve 400.
- Las validaciones, las reglas y las excepciones viven en la capa `service`; las entidades guardan solo estado. Un dato ilegal nunca llega a la base.

## Errores

| Situación | Excepción | HTTP | `codigo` |
|---|---|---|---|
| Id inexistente (GET, DELETE, simulación) | `EntidadNoEncontradaException` | 404 | `ENTIDAD_NO_ENCONTRADA` |
| Invariante o regla rota (vida 99, tipo desconocido, monstruo muerto, entidad que no es monstruo) | `EntidadInvalidaException` | 400 | `ENTIDAD_INVALIDA` |
| JSON roto | `HttpMessageNotReadableException` (handler propio) | 400 | `CUERPO_ILEGIBLE` |
| Parámetro con tipo inválido | `MethodArgumentTypeMismatchException` | 400 | `PARAMETRO_INVALIDO` |

Todos los errores devuelven un JSON con `timestamp`, `status`, `codigo`, `mensaje` y `path`.

## Cómo probar

1. Borrar la carpeta `data/` (base vacía), arrancar con `./mvnw spring-boot:run` e importar `insomnia/minecraft-insomnia.json`.
2. `GET /` -> 200.
3. Los tres `POST` válidos (jugador con inventario `["escudo","TNT","espada"]`, monstruo Creeper y animal Vaca) -> 201 y ids 1, 2 y 3.
4. `GET` de la colección y `GET` por id -> aparecen los recursos creados.
5. `PUT` y `PATCH` del jugador -> un `GET` posterior conserva el cambio.
6. `GET /minecraft/explosivos` -> JSON con `PersonajeJugable` (poder 4) y `Monstruo` (poder 10).
7. `POST /minecraft/simulacion/explosion` con `{"monstruoId": 2, "potencia": 10}` -> Creeper con vida 0, jugador con 1 escudo consumido y vida 15, vaca con vida 10.
8. `GET /minecraft/entidades` -> ya muestra esas vidas persistidas.
9. `POST` ilegal (`vida: 99`) -> 400 `ENTIDAD_INVALIDA`; un `GET` posterior muestra que no se creó nada.
10. `GET` de un id inexistente -> 404 `ENTIDAD_NO_ENCONTRADA`.
11. Repetir la explosión con el Creeper ya muerto -> 400 `ENTIDAD_INVALIDA`.
12. `DELETE` de la vaca -> 204, y un `GET` posterior -> 404.
13. En `http://localhost:8080/h2-console` (URL `jdbc:h2:file:./data/minecraft;AUTO_SERVER=TRUE`, usuario `sa`) se ve la tabla `ENTIDAD_VIVA`.

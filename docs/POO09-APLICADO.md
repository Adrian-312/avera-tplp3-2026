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
| A2 | **PENDIENTE** | Validación de escudos del jugador antes de la explosión. | **PENDIENTE** | **PENDIENTE** | **PENDIENTE** |
| A3 | **PENDIENTE** | Validación de potencia de la explosión. | **PENDIENTE** | **PENDIENTE** | **PENDIENTE** |

A1 construyó el escenario completo y dejó los consumos de A2 y A3 parametrizados. A2 y A3 se completan cuando el grupo informe sus URLs y contratos.

Tecnologías usadas por A1: Spring Boot 4.1.1, Java 21, Spring WebMVC, Spring WebFlux (`WebClient`), Spring Data JPA, H2 y springdoc-openapi 3.2.0.

## 2. Escenario elegido

Un **Creeper de A1** explota contra un jugador que lleva escudos. Antes de aplicar la explosión:

1. **A2** verifica que el jugador tenga escudos suficientes.
2. **A3** verifica que la potencia solicitada esté dentro de lo permitido.
3. Solo si ambos aceptan, **A1** simula la explosión con `SimulacionService` y guarda una `Jugada` en H2.

El orden de consulta es A2 → A3. Si A2 rechaza, no se consulta A3. Cualquier rechazo ocurre antes de escribir, por lo que no se crea ninguna jugada. Si la simulación local falla, la transacción `@Transactional` hace rollback de la jugada.

## 3. Endpoint propio de A1

### `POST /minecraft/escenarios/explosion-validada`

```json
{
  "jugadorId": 1,
  "monstruoId": 2,
  "potencia": 6,
  "motivo": "explosión válida"
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

A1 envía el mismo cuerpo a ambos compañeros:

```json
{
  "jugador": "SteveA1",
  "vidaJugador": 20,
  "escudosDisponibles": 2,
  "monstruo": "CreeperFeliz",
  "potencia": 6,
  "escenario": "explosion-validada"
}
```

A1 interpreta como aceptación cualquiera de estos nombres de campo: `aceptada`, `acepta`, `aceptado`, `aprobado`, `valido`, `valida` u `ok`. Como código y mensaje acepta `codigo`/`code`/`resultado` y `mensaje`/`motivo`/`razon`/`message`. Si la respuesta no es JSON, se considera fallo de comunicación.

### Reglas de los stubs locales (temporales)

| Compañero | Ruta local | Regla | Código de rechazo |
|---|---|---|---|
| A2 | `/integracion/stub/a2/validar` | `escudosDisponibles >= ${integracion.stub.a2.escudos-minimos}` (default `1`) | `SIN_ESCUDO` |
| A3 | `/integracion/stub/a3/validar` | `potencia <= ${integracion.stub.a3.potencia-maxima}` (default `8`) | `POTENCIA_ALTA` |

### Datos reales pendientes del grupo

- Nombre e integrante de A2 y A3.
- URLs base y paths reales de validación.
- OpenAPI real de cada compañero y campos exactos de su contrato.
- Códigos de rechazo y autenticación, si la hubiera.
- Regla de negocio definitiva de cada compañero.

## 5. Mapeo de errores

| Situación | `codigo` | HTTP | Mensaje |
|---|---|---|---|
| A2 rechaza | `RECHAZO_A2` | 400 | `Rechazo de A2: ...` |
| A3 rechaza | `RECHAZO_A3` | 400 | `Rechazo de A3: ...` |
| A2 no responde o responde inválido | `FALLO_A2` | 502 | `Fallo al consultar A2: ...` |
| A3 no responde o responde inválido | `FALLO_A3` | 502 | `Fallo al consultar A3: ...` |
| Entidad muerta en POO-08 | `ENTIDAD_MUERTA` | 409 | handler ya existente |

Todas las respuestas de error incluyen `timestamp`, `status`, `codigo`, `mensaje` y `path`.

## 6. Persistencia

La entidad `Jugada` se guarda en la tabla `jugada` con: `jugador_id`, `jugador_nombre`, `monstruo_id`, `monstruo_nombre`, `potencia`, `escudos_consumidos`, `vida_jugador_despues`, `aceptada_por_a2`, `aceptada_por_a3`, `motivo` y `fecha`.

Solo se guarda después de que A2 y A3 aceptan y la simulación termina correctamente. `escudosConsumidos` corresponde al jugador del escenario, no al total consumido en todas las entidades.

## 7. Cómo se probó

- `./mvnw test`: 18 tests, 0 fallos, 0 errores.
- Nuevos tests del escenario: camino feliz, rechazo de A2, rechazo de A3 y persistencia condicional con un jugador extra.
- Levantamiento con H2 en memoria y recorrido completo del escenario desde `/v3/api-docs` (Swagger UI), según el paso 6 de [`RUN.md`](../RUN.md).
- La colección de Insomnia del repositorio es `insomnia/minecraft-insomnia.json` (POO-08); el escenario de POO-09 se prueba con los endpoints documentados en el OpenAPI propio.
- IMPORTANTE: el arranque de prueba usa `jdbc:h2:mem:...`; no modifica `./data/minecraft`.

## 8. Configuración para reemplazar los stubs

| Variable | Default | Uso |
|---|---|---|
| `A2_BASE_URL` | `http://localhost:8080/integracion/stub` | URL base de A2. |
| `A2_VALIDATE_PATH` | `/a2/validar` | Path de validación de A2. |
| `A3_BASE_URL` | `http://localhost:8080/integracion/stub` | URL base de A3. |
| `A3_VALIDATE_PATH` | `/a3/validar` | Path de validación de A3. |
| `INTEGRACION_TIMEOUT_SECONDS` | `5` | Timeout de `WebClient`. |

Ejemplo con compañeros reales:

```bash
A2_BASE_URL='https://a2.example.edu/api' \
A2_VALIDATE_PATH='/validaciones/escudos' \
A3_BASE_URL='https://a3.example.edu/api' \
A3_VALIDATE_PATH='/validaciones/potencia' \
./mvnw spring-boot:run
```

## 9. Pendientes que no dependen de A1

1. Completar la tabla del apartado 1 con los datos reales de A2 y A3.
2. Reemplazar los stubs por los OpenAPI reales mediante las variables de entorno.
3. Ajustar `ReglaExternaClient` solo si los contratos reales usan otros nombres de campo.
4. Pegar el hash del commit en el enlace del encabezado.

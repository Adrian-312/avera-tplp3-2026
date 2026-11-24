# RUN - Cómo ejecutar y probar el servicio

## Requisitos

- **Java 21** (comprobalo con `java -version`).
- **Git** para clonar el repositorio.
- No hace falta instalar Maven: el proyecto trae su propio `mvnw`.
- **Insomnia** (app de escritorio) para recorrer la colección de pruebas. No necesita cuenta: sirve el modo local *Scratch Pad*.

## 1. Obtener el código

```bash
git clone https://github.com/Adrian-312/avera-tplp3-2026.git
cd avera-tplp3-2026
```

Las ramas de trabajo se fusionan en `main`: ejecutá siempre desde `main`.

## 2. Arrancar el servicio

Desde la carpeta que contiene `pom.xml`:

| Sistema | Comando |
|---|---|
| Linux / macOS / Git Bash | `./mvnw spring-boot:run` |
| Windows (CMD) | `mvnw.cmd spring-boot:run` |
| Windows (PowerShell) | `.\mvnw.cmd spring-boot:run` |

La primera vez descarga dependencias y tarda unos minutos. Cuando termina arrancar, la consola muestra `Started AveraTplp32026Application` y el servicio queda escuchando en el puerto **8080**.

Para detenerlo: **Ctrl+C** en esa terminal.

### Alternativa: generar el .jar y ejecutarlo

```bash
./mvnw clean package
java -jar target/avera-tplp3-2026-0.0.1-SNAPSHOT.jar
```

## 3. Comprobar que funciona

Abrí en el navegador <http://localhost:8080/>. Debe mostrar `"estado": "Servicio activo"`. La lista de endpoints está en <http://localhost:8080/v3/api-docs> y en Swagger UI (`/swagger-ui.html`).

Con la base vacía, <http://localhost:8080/minecraft/entidades> devuelve `[]`. Es correcto: todavía no hay nada guardado. Para crear entidades hacen falta pedidos `POST` (ver el punto 5).

## 4. Ver la base de datos (consola H2)

1. Abrí <http://localhost:8080/h2-console>.
2. Completá el formulario:
   - **JDBC URL:** `jdbc:h2:file:./data/minecraft;AUTO_SERVER=TRUE` (cambiá la que viene por defecto, `jdbc:h2:~/test`).
   - **User Name:** `sa`
   - **Password:** vacía.
3. Apretá **Connect**. A la izquierda aparece la tabla `ENTIDAD_VIVA`; hacele clic y ejecutá `SELECT * FROM ENTIDAD_VIVA`.

La base se guarda en la carpeta `data/` (no se sube a Git). Para empezar de cero: detené el servicio, borrá `data/` y volvé a arrancar.

## 5. Recorrer la colección de Insomnia

1. Con el servicio corriendo y la carpeta `data/` borrada (así los ids serán 1, 2 y 3), abrí Insomnia.
2. **File → Import → From File** y elegí `insomnia/minecraft-insomnia.json`.
3. Si el entorno queda vacío, abrí **Base Environment → Manage Environments**, apagá *Table View* y pegá:
   ```json
   {
     "base_url": "http://localhost:8080",
     "jugador_id": 1,
     "monstruo_id": 2,
      "animal_id": 3,
      "id_inexistente": 9999,
      "a2_base_url": "http://localhost:8081",
      "a3_base_url": "http://localhost:8082",
      "a2_path": "/api/explosiones",
      "a3_path": "/api/reglas/onda-expansiva"
    }
   ```
4. Ejecutá las carpetas **en orden**, con **Send**:
   - `00 - Estado del servicio`
   - `01 - CRUD de entidades`: los tres `POST` (201), listar, buscar, `PUT`, `PATCH`.
   - `02 - Interface Explosivo`
   - `03 - Simulación de explosión`: el Creeper queda con vida 0, el jugador pierde un escudo y queda con 15, la vaca queda con 10.
    - `04 - Errores`: 400 `ENTIDAD_INVALIDA`, 404 `ENTIDAD_NO_ENCONTRADA`, 400 `CUERPO_ILEGIBLE` y 400 al re-explosionar el Creeper muerto.
    - `05 - Entidad muerta (no revive, no actúa)`: 409 `ENTIDAD_MUERTA` al intentar revivir con `PATCH`/`PUT`; el Creeper sigue en vida 0 y desaparece de `/acciones` y `/explosivos`; 400 para `POST` con vida 0, `PUT` sin vida y `PATCH` con vida 0.
    - `06 - DELETE`: 204 y luego 404.

Cada pedido explica en su descripción (pestaña **Docs**) qué respuesta se espera. Las carpetas `08` y `09` son de POO-09: requieren A2 y A3 arriba en `8081`/`8082` y ejercitan el happy path, un rechazo de cada compañero y las reglas propias de A1.

## 6. Probar POO-09 (APIs cruzadas)

1. Con el servicio corriendo, abrí <http://localhost:8080/v3/api-docs> y <http://localhost:8080/swagger-ui.html>. Deben aparecer `POST /minecraft/escenarios/explosion-validada` y `GET /minecraft/escenarios`.
2. Con una base limpia, creá con `POST /minecraft/entidades` (o Swagger UI): jugador con 2 escudos (id 1), monstruo (id 2), jugador sin escudo (id 3) y otro monstruo para los rechazos (id 4). Después usá **Try it out** en `POST /minecraft/escenarios/explosion-validada`. No reutilices los ids de la colección de POO-08: al terminar esa carpeta el Creeper está muerto y el jugador ya no tiene escudos.
3. Para probar sin tocar la carpeta `data/`, podés arrancar con H2 en memoria:

   ```bash
   SERVER_PORT=8080 \
   SPRING_DATASOURCE_URL='jdbc:h2:mem:poo09demo;DB_CLOSE_DELAY=-1' \
   SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop \
   SPRING_JPA_SHOW_SQL=false \
   ./mvnw spring-boot:run
   ```

4. Probá en este orden: escenario feliz (`jugadorId = 1`, `monstruoId = 2`, `potencia = 6`) → `201`; rechazo de A2 (`jugadorId = 3`, `monstruoId = 4`, `potencia = 6`) → `400 RECHAZO_A2`; crear un jugador con 1 escudo (id 5) y rechazo de A3 (`jugadorId = 5`, `monstruoId = 4`, `potencia = 10`) → `400 RECHAZO_A3`; `GET /minecraft/escenarios` → un solo elemento. El monstruo de los rechazos sigue vivo porque un rechazo no ejecuta la simulación.
5. En la consola H2 aparece también la tabla `JUGADA` con las jugadas aceptadas.
6. Los clientes apuntan a los compañeros reales con `A2_BASE_URL=http://localhost:8081`, `A2_VALIDATE_PATH=/api/explosiones`, `A3_BASE_URL=http://localhost:8082` y `A3_VALIDATE_PATH=/api/reglas/onda-expansiva`. El detalle está en `docs/POO09-APLICADO.md`.

## Problemas comunes

| Síntoma | Causa y solución |
|---|---|
| `Port 8080 was already in use` | Hay otra copia del servicio corriendo. Cerrala (Ctrl+C) y volvé a ejecutar. |
| `JAVA_HOME is not defined` o versión de Java incorrecta | Instalá Java 21 y definí `JAVA_HOME` apuntando a esa instalación. |
| `Permission denied: ./mvnw` (Linux / macOS) | Ejecutá `chmod +x mvnw` y reintentá. |
| `BUILD FAILURE` con `COMPILATION ERROR` | Leé la línea `[ERROR]` con el archivo y el número de línea; indica qué clase falla. |
| Insomnia avisa `1 environment variable is missing` | El entorno está vacío: cargá las variables como en el paso 3 del punto 5. |
| Las URL devuelven `[]` | La base está vacía. Creá entidades con los `POST` de la colección. |
| Los ids no coinciden con el entorno de Insomnia | Quedaron datos de pruebas anteriores. Detené el servicio, borrá `data/` y arrancá de nuevo. |

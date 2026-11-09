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

Abrí en el navegador <http://localhost:8080/>. Debe mostrar `"estado": "Servicio activo"` y la lista de endpoints.

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
     "id_inexistente": 9999
   }
   ```
4. Ejecutá las carpetas **en orden**, con **Send**:
   - `00 - Estado del servicio`
   - `01 - CRUD de entidades`: los tres `POST` (201), listar, buscar, `PUT`, `PATCH`.
   - `02 - Interface Explosivo`
   - `03 - Simulación de explosión`: el Creeper queda con vida 0, el jugador pierde un escudo y queda con 15, la vaca queda con 10.
   - `04 - Errores`: 400 `ENTIDAD_INVALIDA`, 404 `ENTIDAD_NO_ENCONTRADA` y 400 `CUERPO_ILEGIBLE`.
   - `05 - DELETE`: 204 y luego 404.

Cada pedido explica en su descripción (pestaña **Docs**) qué respuesta se espera.

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

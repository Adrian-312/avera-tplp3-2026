# Bitácora de asistencia de inteligencia artificial

**Asignatura:** Lenguaje de Programación 3 (CYT646)  
**Alumno:** Adrian (GitHub: `Adrian-312`)  
**Dominio:** Minecraft

---

## Ejercicio POO-08 — CRUD REST, H2, explosiones y excepciones

**Fecha de trabajo:** 9 de noviembre de 2026

### Asistente y modelo

- **Marca del asistente:** opencode (agente de programación en la terminal).
- **Modelo exacto de LLM:** `big-pickle` (modelo informado por la herramienta: `opencode/big-pickle`).

### Resumen de prompts

1. Pedí leer el enunciado de POO-08 y la rúbrica, y comparar lo que ya tenía en el repositorio con lo que faltaba.
2. Pedí corregir el enfoque: el trabajo es de Minecraft y la simulación tenía que ser de **explosiones**, no de ataques. Se reemplazó la interface `Atacante` por `Explosivo`.
3. Pedí configurar H2 en archivo, la consola H2 y las propiedades de JPA explicadas para el README.
4. Pedí mapear la familia `EntidadViva` con JPA usando una sola tabla y un discriminador, manteniendo constructores simples, sobrecargados y el constructor vacío protegido para JPA.
5. Pedí implementar el repositorio Spring Data, el CRUD REST completo, la interface `Explosivo` y la simulación persistida con las dos reglas: onda expansiva y escudos de bloque.
6. Pedí crear la jerarquía de excepciones del dominio (`MinecraftException`, `EntidadNoEncontradaException`, `EntidadInvalidaException`) y un handler HTTP por tipo.
7. Pedí generar la colección de Insomnia con el CRUD, la simulación, el POST ilegal, el id inexistente y un pedido por cada excepción hija.
8. Pedí actualizar el README, la bitácora, el documento de especificaciones y separar el trabajo en commits explicativos.
9. Pedí compilar, correr las pruebas y probar los endpoints con `curl` antes de cerrar la entrega.

### Qué aportó la IA y qué hice yo

- La IA propuso el código de entidades, repositorio, DTOs, controllers, excepciones, handler y colección Insomnia.
- Yo revisé el modelo de Minecraft, mantuve los nombres de mis clases (`PersonajeJugable`, `Monstruo`, `Animal`, `Planta`) y verifiqué que la simulación represente una explosión real del juego.
- Yo ejecuté `./mvnw spring-boot:run`, probé los endpoints con `curl`, abrí la consola H2 y organicé los commits en Git.

### Verificación realizada

- `./mvnw test`: las pruebas de dominio pasan.
- `GET /`: servicio activo.
- `POST /minecraft/entidades`: 201 con `id`.
- `GET /minecraft/entidades`: se ve lo persistido.
- `PUT` y `PATCH`: el cambio se conserva después de un `GET`.
- `DELETE`: 204; el `GET` posterior da 404.
- `GET /minecraft/explosivos`: responde con `PersonajeJugable` y `Monstruo` a través de la interfaz.
- `POST /minecraft/simulacion/explosion`: el Creeper muere, los escudos se consumen y la vida de los afectados queda persistida.
- Errores: 404 `ENTIDAD_NO_ENCONTRADA` y 400 `ENTIDAD_INVALIDA` con JSON distinto.

---

## Historial anterior

### Ejercicio POO-06 — Revisión, paquetes, constructores y sobrecarga

- **Asistente:** Claude (Anthropic), desde la interfaz web.
- **Modelo:** Claude Sonnet 5.5 (`claude-sonnet-5-5`).
- Se trabajó en paquetes `domain` y `rest/controller`, constructores simples y sobrecargados, encapsulamiento de `EntidadViva` y validaciones en constructores.
- Prompts principales: revisar paquetes, agregar constructores, quitar setters públicos, explicar sobrecarga y sobreescritura, resolver permisos de `mvnw` y armar commits.

### Ejercicio de interfaces — 29/10/2026

- **Asistente:** opencode, modelo `big-pickle`.
- Se agregó una interface de dominio y un endpoint que la usa con dos clases de familias distintas.
- En POO-08 esa interface se reemplazó por `Explosivo`, porque la simulación de este trabajo es de explosiones.

---

## Preparación del challenge POO-09 — APIs cruzadas

**Fecha de trabajo:** 22 de noviembre de 2026
**Rama local creada:** `poo09-preparacion` (sin commit; el usuario la subirá a su repo)

### Asistente y modelo

- **Marca del asistente:** opencode (agente de programación en la terminal).
- **Modelo exacto de LLM:** `big-pickle` (modelo informado por la herramienta: `opencode/big-pickle`).

### Resumen de prompts

1. Pedí que no se reemplazara el contenido de `BITACORA.md` ni del `README.md`, sino que se agregaran las líneas correspondientes a lo hecho hoy, y que dejara todo preparado antes de la parte grupal.
2. Pedí crear una rama local nueva para este trabajo.
3. Reenvié los prompts que circularon en la comisión, como referencia de lo que podía venir: consumir `https://restful-api.dev/` / `https://api.restful-api.dev/objects` con WebClient; agregar OpenAPI y Swagger UI a todos los servicios REST; examinar un OpenAPI ajeno y combinar una regla externa con la lógica propia; configurar el puerto de forma externalizable (referencia de Baeldung); hacer diagramas Mermaid de secuencias, separar el camino feliz de los rechazos y agregar log4j en nivel DEBUG para todos los pasos de integración.
4. Pedí que, si no había nada que hacer sin los datos del grupo, lo indicara; como sí había infraestructura dependiente sólo de este repo, se implementó completa con stubs temporales para A2 y A3.
5. Pedí aclarar el alcance: se preparó el escenario cruzado con validadores reemplazables y stubs locales para poder probar hoy el camino feliz y un rechazo por cada compañero.

### Qué aportó la IA y qué hice yo

- La IA agregó springdoc-openapi 3.2.0 y Swagger UI, WebClient, Log4j2, la entidad `Jugada`, el repositorio, el servicio de escenario, los clientes hacia A2/A3, los stubs temporales, el manejo de errores `RECHAZO_A2`, `RECHAZO_A3`, `FALLO_A2` y `FALLO_A3`, la colección de Insomnia y los documentos `docs/POO09-PREPARACION.md` y `docs/POO09-APLICADO.md`.
- La IA dejó marcados como **PENDIENTE** los datos que todavía no existen: nombres de A2 y A3, URLs de sus OpenAPI, contratos reales de mensajes, estado de la corrección de POO-08 y hash del commit final.
- Yo elegí crear la rama local `poo09-preparacion`, revisé el alcance con la herramienta de preguntas y voy a subir el trabajo a mi repositorio cuando se complete la parte grupal.

### Verificación realizada

- `./mvnw test`: pasan las 5 pruebas (4 de dominio y la carga del contexto con los nuevos beans).
- `GET /v3/api-docs`: aparecen CRUD, simulación, integración, escenario y stubs; `GET /swagger-ui.html` disponible.
- `POST /minecraft/integracion/objeto` con `objetoId=1`: 200 con `Google Pixel 6 Pro`, potencia 10, 2 escudos consumidos y vida 20.
- Escenario feliz con jugador de 2 escudos y monstruo nuevo, potencia 6: 201 con `jugadaId=1`, `aceptadaPorA2=true` y `aceptadaPorA3=true`; `GET /minecraft/escenarios` muestra la jugada.
- Rechazo de A2 (jugador sin escudo): 400 con `codigo=RECHAZO_A2` y `SIN_ESCUDO`; la cantidad de jugadas sigue en 1.
- Rechazo de A3 (potencia 10): 400 con `codigo=RECHAZO_A3` y `POTENCIA_ALTA`; la cantidad de jugadas sigue en 1.
- Los logs DEBUG muestran los pasos de integración en consola y en `logs/integracion.log`.

### Lo que falta para la entrega final

- Completar el mapa A1/A2/A3 con los compañeros reales.
- Leer sus OpenAPI y adaptar `ValidacionExternaRequest` y `ValidacionExternaResponse` a esos contratos.
- Cambiar `A2_BASE_URL`, `A2_VALIDATE_PATH`, `A3_BASE_URL` y `A3_VALIDATE_PATH`, o dejar documentado qué pasó si un compañero no responde.
- Definir la situación de POO-08 para los 3 puntos y reemplazar los enlaces al commit final en README y `docs/POO09-APLICADO.md`.

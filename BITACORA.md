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

# Bitácora de asistencia de inteligencia artificial

**Ejercicio:** POO-06 — Revisión, paquetes, constructores y sobrecarga
**Asignatura:** Lenguaje de Programación 3 (CYT646)
**Alumno:** Adrian (GitHub: Adrian-312)
**Dominio:** Minecraft

## Asistente y modelo

- **Marca del asistente:** Claude (Anthropic), usado desde la interfaz web/app de chat.
- **Modelo exacto de LLM:** Claude Sonnet 5.5 (`claude-sonnet-5-5`).

## Resumen de prompts

1. Subí el enunciado y la rúbrica y pedí una guía paso a paso de lo que me faltaba en mi repositorio.
2. Pedí que revisara el nombre de los paquetes de mis controllers tras mover las clases a `domain` y `rest/controller`. Se detectó un error de sintaxis en la línea `package` y la falta de imports en `ConstruccionController`.
3. Subí todas mis clases y controllers y pedí que agregara constructores simples y sobrecargados, y que me explicara qué son la sobrecarga y la sobreescritura.
4. Pedí que adaptara los controllers a los nuevos constructores y que quitara los setters públicos para proteger vida, nombre y altura.
5. Pedí los comandos de Git para armar commits separados y hacer el push.
6. Consulté cómo solucionar el error de permiso denegado al ejecutar `mvnw`.
7. Pedí generar esta bitácora y cómo evitar que Git me pida usuario y contraseña en cada push.

## Qué hice yo y qué aportó la IA

- La IA propuso el código de las clases de `domain` y de los controllers, y explicó los conceptos de sobrecarga y sobreescritura.
- Yo copié los archivos al proyecto, los compilé y los probé con `./mvnw spring-boot:run`, y organicé los commits en Git.
- Yo mantuve el diagrama Mermaid y el README alineados con lo que hay en `src/`.

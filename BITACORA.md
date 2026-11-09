# Bitácora de uso de IA

- **Asistente:** Claude (Anthropic), usado desde la interfaz web claude.ai.
- **Modelo de LLM:** Claude Sonnet 5.5
- **Fecha:** 9 de noviembre de 2026

## Resumen de prompts

1. Pasé el enunciado de POO-08, los 17 archivos Java del proyecto (dominio, excepciones y controllers) y pedí los pasos para completar la tarea y la corrección de lo que estuviera mal.
2. Subí `pom.xml`, `application.properties` y los DTO compilados, y pedí que me explicara cómo hacer cada pieza que faltaba (porque no sabía hacerlas) y si estaban bien las clases `Explosivo` y `Explosion`.
3. Pegué el error de compilación de Maven y el código de mi `GlobalExceptionHandler`; pedí corregir el conflicto.
4. Pedí ayuda con el `.gitignore`, con las URL para probar, con la consola H2 y con importar la colección en Insomnia (capturas de pantalla de los errores que me salían).
5. Pregunté si era correcto que el Creeper siga apareciendo en el listado después de explotar, pedí completar el README con lo que pedía la tarea y actualizar el diagrama de clases, cómo hacer el merge de mi rama `desarrollo` a `main` y si la tarea pedía un `RUN.md`.
6. Pedí separar la documentación: un `RUN.md` con las instrucciones de ejecución y un `README.md` sin la parte de entregas, y que corrigiera ambos.

## Qué aportó la IA

- Revisión del código existente contra las consignas del enunciado y lista de correcciones:
  - `listar` usaba una consulta "mayor que" para un parámetro llamado `vidaMinima`, y ignoraba `vidaMinima` si también venía `nombre`.
  - `quitarDelInventario` distinguía mayúsculas y `consumirItem` no.
  - Se quitaron los endpoints con verbo en la URL (`/crear-jugador`, `/crear-monstruo`, `/danio`), que no seguían la convención REST.
- Código nuevo: `EntidadVivaRepository`. El `GlobalExceptionHandler` y el `ErrorResponse` ya existían en mi proyecto; la IA asumió que faltaban y los pisó, lo que causó un error de compilación. Se corrigió dejando un `ErrorResponse` (con `timestamp`, `status`, `codigo`, `mensaje` y `path`) y agregando a mi handler el manejo del JSON ilegible.
- Documentación y pruebas: la colección de Insomnia, el README (con diagrama Mermaid, invariantes y recorrido de prueba), esta bitácora y el documento de especificaciones.

## Qué hice yo

Revisé y probé lo generado: arranqué el servicio, recorrí la colección de Insomnia y comprobé en el listado que el estado se persiste después de la simulación. Resolví el conflicto del handler y subí los cambios al repositorio.

# Colección de Insomnia — Minecraft POO-08 / POO-09

Archivo de la colección: [`minecraft-insomnia.json`](minecraft-insomnia.json). En esta carpeta están la colección y este instructivo; el README principal del proyecto tiene el mapa del grupo, el diagrama y las respuestas de ejemplo.

## Importar

1. Abrí Insomnia y elegí **File → Import → From File**.
2. Seleccioná `insomnia/minecraft-insomnia.json`.
3. El workspace se llama "Minecraft - POO-08" (nombre heredado), pero incluye las carpetas `07`, `08` y `09` de POO-09.

## Entorno (*Base Environment*)

Si el entorno queda vacío, abrí **Manage Environments**, apagá *Table View* y pegá:

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

## Orden: quién tiene que estar arriba primero

Arrancá **A2** (`8081`) y **A3** (`8082`) antes que **A1** (`8080`), porque A1 importa sus OpenAPI para armar los pedidos del escenario. El cliente de Insomnia le habla solamente a A1. Si querés una base limpia, detené A1, borrá `data/` y volvé a arrancarlo: los ids serán 1, 2 y 3.

## Carpetas

Hay 9 carpetas de recorrido (`01` a `09`) más `00` de estado:

- `00 - Estado del servicio`: `GET /`.
- `01 - CRUD de entidades (ejecutar en orden)`: tres `POST`, listar, filtrar por nombre, `GET`, `PUT`, `PATCH` y comprobación.
- `02 - Interface Explosivo`: `GET /minecraft/explosivos` y `GET /minecraft/acciones`.
- `03 - Simulación de explosión`: caso feliz y estado persistido.
- `04 - Errores (un pedido por cada excepción)`: 400, 404, `CUERPO_ILEGIBLE` y `409 ENTIDAD_MUERTA`.
- `05 - Entidad muerta (no revive, no actúa)`: regresión del bug corregido; `PATCH`/`PUT` con vida 20 devuelven 409.
- `06 - DELETE`: borrado y 404 posterior.
- `07 - OpenAPI y reglas publicadas (POO-09)`: OpenAPI de A1, Swagger UI y OpenAPI de A2/A3.
- `08 - Escenario cruzado A1 + A2 + A3 (POO-09)`: feliz, persistencia, rechazo de A2, rechazo de A3 y `GET` sin cambios.
- `09 - Contratos directos y reglas propias (POO-09)`: llamadas directas a A2/A3 y a las dos reglas de A1.

Ejecutá las carpetas **en orden** con **Send**. Cada pedido explica en su pestaña *Docs* qué respuesta se espera.

## Respuestas de ejemplo

Las respuestas verificadas del camino feliz y de cada rechazo (A2 y A3) están en el README principal, sección [«Respuestas de ejemplo exportadas»](../README.md#respuestas-de-ejemplo-exportadas). Sirven para la corrección si un compañero no está en línea.

Los formatos son estrictos: si falta un campo obligatorio o cambia su tipo, A1 responde `502 FALLO_A2` o `502 FALLO_A3`; no hay lectura de alias de campos.

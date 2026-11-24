# Colección de Insomnia

Archivo: `minecraft-insomnia.json`.

1. Abrí Insomnia y elegí **Import → From File**.
2. Seleccioná `minecraft-insomnia.json`.
3. En *Base Environment* verificá estas variables:
   - `base_url`: `http://localhost:8080`
   - `a2_base_url`: `http://localhost:8081`
   - `a3_base_url`: `http://localhost:8082`
   - `a2_path`: `/api/explosiones`
   - `a3_path`: `/api/reglas/onda-expansiva`
   - `jugador_id`: `1`, `monstruo_id`: `2`, `animal_id`: `3`, `id_inexistente`: `9999`

Para POO-09 arrancá primero A2 y A3 con sus puertos por defecto y después A1. Si querés una base limpia, detené A1, borrá `data/` y volvé a arrancarlo. Las respuestas observadas están en `respuestas-ejemplo.md`.

- Carpeta `07 - OpenAPI y reglas publicadas (POO-09)`: smoke test de los cambios; verifica los OpenAPI de A1/A2/A3, el Swagger UI de A1 y las reglas nuevas `/minecraft/reglas/danio-caida` y `/minecraft/reglas/absorcion-escudo`.
- Carpeta `08 - Escenario cruzado A1 + A2 + A3 (POO-09)`: happy path, rechazo de A2 y rechazo de A3 a través de A1.
- Carpeta `09 - Contratos directos y reglas propias (POO-09)`: llamadas directas a los contratos reales de A2/A3 y a las reglas de A1.

Los formatos son estrictos: si falta un campo obligatorio o cambia su tipo, A1 responde `502 FALLO_A2` o `502 FALLO_A3`; no hay lectura de alias.

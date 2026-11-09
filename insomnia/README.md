# Colección Insomnia — POO-08 Minecraft

Archivo: `poo08-minecraft.json`

## Importar

1. Abrir Insomnia.
2. `Import` → `From File`.
3. Elegir `insomnia/poo08-minecraft.json`.
4. Seleccionar el workspace **POO-08 Minecraft - Explosiones H2**.
5. Activar el entorno **Base Environment**.

## Variables

| Variable | Uso | Valor por defecto |
|---|---|---|
| `base_url` | URL del servicio | `http://localhost:8080` |
| `jugador_id` | Steve | `1` |
| `jugador2_id` | Alex | `2` |
| `animal_id` | Vaca | `3` |
| `creeper_id` | Creeper que explota | `4` |
| `creeper2_id` | Creeper para el error de potencia | `5` |

Si la base no está vacía, copiar el `id` que devuelve cada `POST` en la variable correspondiente.

## Orden sugerido

1. `GET /`.
2. Crear Steve, Alex, Vaca, Creeper y Creeper2.
3. Ajustar las variables con los `id` devueltos.
4. `GET` colección, `GET` por id, `PUT` y `PATCH`.
5. `GET /minecraft/explosivos`.
6. `POST /minecraft/simulacion/explosion` con potencia 10.
7. `GET /minecraft/entidades` para ver el estado persistido.
8. Ejecutar los pedidos de error de la carpeta **04 - Errores**.
9. Abrir la consola H2 en `http://localhost:8080/h2-console`.

Para reiniciar la base: detener Spring Boot, borrar la carpeta `data/` y volver a arrancar.

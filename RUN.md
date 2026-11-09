# Instrucciones de Ejecución

- Requiere **Java JDK 21**.
- Servidor escuchando en el puerto **8080**.
- Base H2 en archivo: `./data/minecraft.mv.db`.

Linux / macOS:
```bash
./mvnw spring-boot:run
```

Windows PowerShell:
```powershell
.\mvnw spring-boot:run
```

Consola H2: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:file:./data/minecraft`
- Usuario: `sa`
- Contraseña: vacía

Para reiniciar la base: detener la aplicación y borrar la carpeta `data/`.

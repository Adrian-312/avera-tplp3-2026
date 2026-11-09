# Especificaciones aplicadas al dominio — POO-08 Minecraft

**Alumno:** Adrian (GitHub: `Adrian-312`)  
**Asignatura:** Lenguaje de Programación 3 (CYT646)  
**Ejercicio:** POO-08 — CRUD REST, H2, reglas de juego y excepciones  
**Dominio:** Minecraft  
**Repositorio:** `https://github.com/Adrian-312/avera-tplp3-2026`  
**Commit de la solución:** pendiente de registrar después del commit final (se pega también en Classroom).  
**Colección Insomnia:** `insomnia/poo08-minecraft.json`

---

## 1. Objetivo

Publicar un servicio HTTP con Spring Boot que permita:

- persistir en H2 una familia del modelado Minecraft;
- recorrer un CRUD REST completo sobre esa familia;
- ejecutar una simulación de explosiones con dos reglas de negocio del juego;
- consultar el estado persistido después de simular;
- distinguir por HTTP los errores del dominio mediante una jerarquía de excepciones;
- usar una interface de dominio con dos clases de familias distintas;
- reproducir todo desde una colección de Insomnia versionada.

El trabajo anterior tenía una interface `Atacante` y una acción `atacar()`. Para este ejercicio se corrigió el enfoque: la capacidad compartida ahora es `Explosivo` y la simulación es una **explosión de Creeper**, coherente con Minecraft.

---

## 2. Especificaciones aplicadas

### 2.1 Persistencia de tres clases

Se eligió la familia `EntidadViva`:

```text
EntidadViva (abstracta, @Entity)
├── PersonajeJugable (@Entity)
└── PersonajeNoJugable (abstracta, @Entity)
    ├── Animal (@Entity)
    ├── Monstruo (@Entity)
    └── Planta (@Entity)
```

Las tres clases que la simulación necesita y que se persisten son:

1. `PersonajeJugable`: recibe la explosión y puede consumir escudos.
2. `Monstruo`: el Creeper que explota y muere.
3. `Animal`: entidad cercana que recibe la onda expansiva.

`EntidadViva`, `PersonajeNoJugable` y `Planta` también están mapeadas porque pertenecen a la misma familia y comparten la tabla.

**Estrategia JPA:** `InheritanceType.SINGLE_TABLE` con la columna discriminadora `tipo_entidad`. Se eligió porque:

- todo el estado del mundo se lee de una sola tabla, sin joins;
- el discriminador distingue el subtipo en la base;
- el JSON de salida agrega el campo `tipo` con `@JsonTypeInfo` y `@JsonSubTypes`;
- el DTO de entrada lee `tipo` y el controller elige el constructor correcto.

No se deserializan entidades JPA directamente. El DTO `EntidadRequest` y los constructores de negocio garantizan que Jackson no pueda crear un objeto inválido ni saltarse las validaciones.

### 2.2 Interfaz de dominio

Archivo: `domain/Explosivo.java`.

```java
public interface Explosivo {
    int poderExplosivo();

    default String sonidoDeExplosion() {
        return "Una onda expansiva sacude el terreno.";
    }
}
```

Implementan:

- `Monstruo`: devuelve su `potenciaExplosiva` (10 para un Creeper por defecto).
- `PersonajeJugable`: devuelve 4 por cada `"TNT"` que lleva en el inventario.

El endpoint `GET /minecraft/explosivos` arma una `List<Explosivo>` con las entidades persistidas y las trata por el tipo de la interfaz. Las dos clases pertenecen a ramas distintas del modelo.

### 2.3 Repositorio

Archivo: `repository/EntidadVivaRepository.java`.

```java
public interface EntidadVivaRepository extends JpaRepository<EntidadViva, Long> {
    List<EntidadViva> findByNombreContainingIgnoreCase(String nombre);
    List<EntidadViva> findByVidaGreaterThan(int vida);
    List<EntidadViva> findByUltimoOrigenDanio(String origen);
}
```

Spring Data genera la implementación. El controller depende de la interfaz, no de una clase concreta. Los métodos derivados se usan, por ejemplo, en `GET /minecraft/entidades?nombre=Creeper`.

### 2.4 CRUD REST

Recurso: `/minecraft/entidades`.

| Operación | Verbo y URL | Código de éxito | Detalle |
|---|---|---|---|
| Crear | `POST /minecraft/entidades` | 201 Created | Devuelve el recurso con `id` y `Location`. |
| Leer colección | `GET /minecraft/entidades` | 200 OK | Lista lo persistido; acepta `nombre` y `vidaMinima`. |
| Leer uno | `GET /minecraft/entidades/{id}` | 200 OK | 404 si no existe. |
| Reemplazar | `PUT /minecraft/entidades/{id}` | 200 OK | Reemplaza los campos editables; los omitidos vuelven a valores por defecto. |
| Actualizar | `PATCH /minecraft/entidades/{id}` | 200 OK | Cambia solo los campos enviados. |
| Borrar | `DELETE /minecraft/entidades/{id}` | 204 No Content | 404 si no existe. |

El `id` es un `Long` generado por H2/JPA con `GenerationType.IDENTITY`. El JSON de creación lo omite; la respuesta lo incluye.

Tipos aceptados en `tipo`: `jugador`, `monstruo`, `animal`, `planta`.

### 2.5 Las dos reglas de negocio

**Regla 1 — Onda expansiva.**  
`Monstruo.explotar(int potencia)` valida que el monstruo esté vivo, sea hostil y que la potencia esté entre 1 y su `potenciaExplosiva`. Luego el monstruo pierde toda su vida y la simulación aplica la potencia a todas las entidades vivas.

**Regla 2 — Escudos de bloque.**  
`PersonajeJugable.recibirExplosion(int potencia, String origen)` consume un `"escudo"` por cada 5 puntos de daño que quiere absorber. Si el daño supera los escudos disponibles, el resto se aplica a la vida.

Ambas reglas están en el dominio, no en el controller. El controller solo orquesta: carga las entidades, llama a los métodos del dominio y persiste el resultado.

**Endpoint de simulación:** `POST /minecraft/simulacion/explosion`.

```json
{
  "monstruoId": 4,
  "potencia": 10
}
```

Respuesta:

```json
{
  "monstruoId": 4,
  "monstruo": "Creeper",
  "potencia": 10,
  "escudosConsumidos": 2,
  "sonido": "Creeper infla su cuerpo: ¡fssss... BOOM!",
  "afectadas": [
    {
      "id": 1,
      "nombre": "Steve",
      "tipo": "PersonajeJugable",
      "vidaAntes": 20,
      "vidaDespues": 20,
      "escudosConsumidos": 2,
      "vivo": true
    },
    {
      "id": 3,
      "nombre": "Vaca",
      "tipo": "Animal",
      "vidaAntes": 20,
      "vidaDespues": 10,
      "escudosConsumidos": 0,
      "vivo": true
    },
    {
      "id": 4,
      "nombre": "Creeper",
      "tipo": "Monstruo",
      "vidaAntes": 20,
      "vidaDespues": 0,
      "escudosConsumidos": 0,
      "vivo": false
    }
  ]
}
```

Un `GET /minecraft/entidades` posterior muestra la vida y los inventarios ya persistidos.

### 2.6 Jerarquía de excepciones y handler

```text
MinecraftException (RuntimeException, base)
├── EntidadNoEncontradaException   -> HTTP 404, codigo ENTIDAD_NO_ENCONTRADA
└── EntidadInvalidaException       -> HTTP 400, codigo ENTIDAD_INVALIDA
```

Se usan excepciones **no comprobadas** porque una regla del dominio incumplida no debería obligar a cada método intermedio a declararla o envolverla. El `@RestControllerAdvice` las convierte en JSON.

Archivo: `rest/exception/GlobalExceptionHandler.java`.

Cada hija tiene su propio `@ExceptionHandler`:

- `EntidadNoEncontradaException` → `404`.
- `EntidadInvalidaException` → `400`.
- `MinecraftException` → `400` como respaldo de la base.
- `IllegalArgumentException` y `MethodArgumentTypeMismatchException` → `400` para errores de entrada.

Formato del JSON de error:

```json
{
  "timestamp": "2026-11-09T18:05:59.634408048Z",
  "status": 400,
  "codigo": "ENTIDAD_INVALIDA",
  "mensaje": "El nombre no puede estar vacío",
  "path": "/minecraft/entidades"
}
```

### 2.7 Ocultamiento e invariantes

Los invariantes se sostienen en dos frentes:

1. **Frente al JSON:** el controller construye con constructores de negocio; no hay setters públicos. Un `POST` con nombre vacío, vida negativa, altura fuera de rango, hambre negativa o potencia inválida lanza `EntidadInvalidaException` antes de `save`.
2. **Frente a JPA:** el constructor sin argumentos es `protected` y solo lo usa JPA. El resto del código no puede crear una entidad vacía.

Invariantes principales:

- `nombre` no nulo y no vacío;
- `vida` entre 0 y 20;
- `altura` mayor que 0 y menor o igual a 10;
- `hambre` entre 0 y 20;
- `potenciaExplosiva` entre 1 y 50;
- un monstruo muerto o no hostil no puede explotar;
- la potencia de la explosión no puede superar la potencia del monstruo;
- el inventario solo acepta ítems no vacíos.

### 2.8 Sobrecarga y sobreescritura

**Sobrecarga:**

- Constructores simples y completos en `EntidadViva`, `PersonajeNoJugable`, `PersonajeJugable`, `Animal`, `Monstruo` y `Planta`.
- `recibirDanio(int)` y `recibirDanio(int, String)`.
- `Monstruo` con constructor de 6 y de 7 parámetros.

**Sobreescritura:**

- `realizarAccionEspecial()` en `PersonajeJugable`, `Animal`, `Monstruo` y `Planta`.
- `deambular()` en `Animal`, `Monstruo` y `Planta`.
- `recibirExplosion(int, String)` en `PersonajeJugable`.
- `poderExplosivo()` en `PersonajeJugable` y `Monstruo`.

---

## 3. URLs del servicio

| Método | URL | Para qué |
|---|---|---|
| GET | `/` | Servicio vivo y lista de endpoints. |
| GET | `/h2-console` | Consola H2. |
| GET | `/minecraft/entidades` | Lista entidades persistidas. |
| GET | `/minecraft/entidades?nombre=Creeper` | Filtro por nombre. |
| GET | `/minecraft/entidades?nombre=&vidaMinima=10` | Filtro por vida mínima. |
| GET | `/minecraft/entidades/{id}` | Una entidad o 404. |
| POST | `/minecraft/entidades` | Crea una entidad. |
| PUT | `/minecraft/entidades/{id}` | Reemplaza campos editables. |
| PATCH | `/minecraft/entidades/{id}` | Actualización parcial. |
| DELETE | `/minecraft/entidades/{id}` | Borra o 404. |
| GET | `/minecraft/explosivos` | Endpoint de la interfaz `Explosivo`. |
| GET | `/minecraft/acciones` | Método abstracto `realizarAccionEspecial()`. |
| POST | `/minecraft/simulacion/explosion` | Simulación persistida. |
| GET | `/minecraft/danio?puntos=5` | Demo de sobrecarga `recibirDanio(int)`. |
| GET | `/minecraft/danio?puntos=5&origen=flecha` | Demo de sobrecarga `recibirDanio(int, String)`. |
| GET | `/minecraft/crear-jugador?...` | Demo de constructor completo (no persiste). |
| GET | `/minecraft/crear-monstruo?nombre=Zombie` | Demo de constructor simple (no persiste). |

---

## 4. Cómo probarlo paso a paso

### 4.1 Arranque

```bash
./mvnw spring-boot:run
```

Esperar el mensaje `Started AveraTplp32026Application`. Abrir `http://localhost:8080/`.

### 4.2 Crear el mundo

```bash
curl -X POST http://localhost:8080/minecraft/entidades \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"jugador","nombre":"Steve","vida":20,"altura":1.8,"hambre":20,"controlable":true,"inventario":["escudo","escudo","TNT"]}'

curl -X POST http://localhost:8080/minecraft/entidades \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"jugador","nombre":"Alex","vida":20,"altura":1.8,"hambre":20,"controlable":true,"inventario":["TNT"]}'

curl -X POST http://localhost:8080/minecraft/entidades \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"animal","nombre":"Vaca","vida":20,"altura":1.1,"domable":true,"pacifico":true}'

curl -X POST http://localhost:8080/minecraft/entidades \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"monstruo","nombre":"Creeper","vida":20,"hostil":true,"potenciaExplosiva":10}'

curl -X POST http://localhost:8080/minecraft/entidades \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"monstruo","nombre":"Creeper2","vida":20,"hostil":true,"potenciaExplosiva":10}'
```

### 4.3 Recorrer el CRUD

```bash
curl http://localhost:8080/minecraft/entidades
curl http://localhost:8080/minecraft/entidades/1

curl -X PUT http://localhost:8080/minecraft/entidades/1 \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"jugador","nombre":"Steve","vida":20,"altura":1.9,"hambre":15,"controlable":true,"inventario":["escudo","escudo"]}'

curl -X PATCH http://localhost:8080/minecraft/entidades/1 \
  -H 'Content-Type: application/json' \
  -d '{"hambre":18,"inventario":["escudo","escudo","TNT"]}'

curl http://localhost:8080/minecraft/entidades/1
```

### 4.4 Interfaz y simulación

```bash
curl http://localhost:8080/minecraft/explosivos

curl -X POST http://localhost:8080/minecraft/simulacion/explosion \
  -H 'Content-Type: application/json' \
  -d '{"monstruoId":4,"potencia":10}'

curl http://localhost:8080/minecraft/entidades
```

Qué se debe observar:

- Steve (2 escudos) termina con la misma vida y sin escudos.
- Alex (sin escudo) pierde 10 puntos de vida.
- La Vaca pierde 10 puntos.
- El Creeper queda con vida 0.
- El `GET` posterior muestra todo eso persistido.

### 4.5 Errores

```bash
# 400 ENTIDAD_INVALIDA
curl -i -X POST http://localhost:8080/minecraft/entidades \
  -H 'Content-Type: application/json' \
  -d '{"tipo":"jugador","nombre":"","vida":-5}'

# 404 ENTIDAD_NO_ENCONTRADA
curl -i http://localhost:8080/minecraft/entidades/999

# 400 ENTIDAD_INVALIDA por potencia mayor a la del monstruo
curl -i -X POST http://localhost:8080/minecraft/simulacion/explosion \
  -H 'Content-Type: application/json' \
  -d '{"monstruoId":5,"potencia":11}'

# 404 ENTIDAD_NO_ENCONTRADA por monstruo inexistente
curl -i -X POST http://localhost:8080/minecraft/simulacion/explosion \
  -H 'Content-Type: application/json' \
  -d '{"monstruoId":999,"potencia":5}'

# DELETE y GET posterior
curl -i -X DELETE http://localhost:8080/minecraft/entidades/3
curl -i http://localhost:8080/minecraft/entidades/3
```

### 4.6 Consola H2

Abrir `http://localhost:8080/h2-console` con:

- JDBC URL: `jdbc:h2:file:./data/minecraft`
- User: `sa`
- Password: vacío

Consultar `SELECT * FROM entidad_viva;` y `SELECT * FROM jugador_inventario;` para ver el estado persistido y los escudos restantes.

### 4.7 Pruebas automáticas

```bash
./mvnw test
```

`SimulacionDomainTest` cubre:

- el Creeper explota y muere;
- los escudos absorben 5 puntos cada uno y se consumen;
- un animal sin escudo recibe el daño completo;
- una potencia inválida lanza `EntidadInvalidaException`.

---

## 5. Decisiones de diseño y problemas corregidos

| Decisión | Motivo |
|---|---|
| H2 en archivo (`jdbc:h2:file:./data/minecraft`) | Permite comprobar la persistencia incluso después de reiniciar. `AUTO_SERVER=TRUE` deja abrir la consola al mismo tiempo. |
| `ddl-auto=update` | El esquema se crea y actualiza solo; no se pierde el estado en cada arranque. |
| `SINGLE_TABLE` | Menos joins y una sola tabla para el “mundo”; el discriminador resuelve el subtipo. |
| DTO `EntidadRequest` en vez de deserializar entidades | Evita que Jackson saltee constructores y validaciones. |
| `protected` en el constructor vacío | JPA puede reconstruir filas, pero el código de negocio no puede crear objetos vacíos. |
| Excepciones `RuntimeException` | El dominio no obliga a `try/catch` en cada capa; el handler centraliza la respuesta HTTP. |
| `EntidadNoEncontradaException` y `EntidadInvalidaException` separadas | El cliente distingue recurso inexistente (404) de regla incumplida (400). |
| Reemplazo de `Atacante` por `Explosivo` | La consigna de POO-08 pide una simulación de explosiones en Minecraft, no de ataques. |
| `PersonajeJugable.recibirExplosion` sobreescribe el método base | La regla de los escudos queda en la clase que tiene el inventario y se aplica por polimorfismo. |

---

## 6. Correspondencia con la rúbrica

| Criterio | Evidencia en este trabajo |
|---|---|
| 1. Entrega y arranque | `./mvnw spring-boot:run`, README, bitácora, documento y commit. |
| 2. Paquetes | `domain`, `domain.exception`, `repository`, `rest.controller`, `rest.dto`, `rest.exception`. |
| 3. Ocultamiento | Constructores validados, sin setters públicos, constructor vacío `protected`, POST ilegal que no persiste. |
| 4. Herencia y excepciones | Familia `EntidadViva`, método abstracto sobreescrito, interfaz `Explosivo`, jerarquía de excepciones con handler por tipo. |
| 5. Constructores y sobrecarga | Constructores simples/completos, `recibirDanio` sobrecargado, endpoints de demo. |
| 6. Comportamiento observable | CRUD real, H2, simulación con dos reglas, estado posterior persistido, handlers distintos. |
| 7. Git | Commits separados por dependencias, entidades, repositorio, CRUD, simulación, excepciones, Insomnia y documentación. |
| 8. Documentación y defensa | README con diagrama y respuestas, bitácora con modelo exacto, este documento y colección Insomnia. |

---

## 7. Enlace al commit

El enlace exacto se registra al finalizar la entrega:

```text
https://github.com/Adrian-312/avera-tplp3-2026/commit/{HASH}
```

También se pega en la tarea de Classroom y en el README del repositorio.

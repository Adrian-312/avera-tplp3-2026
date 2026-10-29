## Diagrama de Clases (Dominio)

```mermaid
classDiagram
    class EntidadViva {
        <<abstract>>
        -int vida
        -String nombre
        -double altura
        #EntidadViva(String nombre)
        #EntidadViva(int vida, String nombre, double altura)
        +realizarAccionEspecial()* String
        +recibirDanio(int puntos) void
        +recibirDanio(int puntos, String origen) void
        +curar(int puntos) void
        +isVivo() boolean
        +getVida() int
        +getNombre() String
        +getAltura() double
    }
    class Atacante {
        <<interface>>
        +atacar()* String
        +sonidoDeAtaque() String
    }
    class PersonajeJugable {
        -int hambre
        -boolean controlable
        -List~String~ inventario
        +PersonajeJugable(String nombre)
        +PersonajeJugable(int, String, double, int, boolean)
        +realizarAccionEspecial() String
        +atacar() String
        +comer(int puntos) void
        +agregarAlInventario(String item) void
        +quitarDelInventario(String item) void
    }
    class PersonajeNoJugable {
        <<abstract>>
        -boolean noControlable
        -boolean crecen
        +deambular()* void
    }
    class Animal {
        -boolean domable
        -boolean pacifico
        +Animal(String nombre)
        +Animal(int, String, double, boolean, boolean, boolean)
        +realizarAccionEspecial() String
        +deambular() void
    }
    class Monstruo {
        -boolean hostil
        -boolean puedeEstarArmado
        +Monstruo(String nombre)
        +Monstruo(int, String, double, boolean, boolean, boolean)
        +realizarAccionEspecial() String
        +atacar() String
        +sonidoDeAtaque() String
        +deambular() void
        +cambiarHostilidad() void
    }
    class Planta {
        -boolean estatico
        +Planta(String nombre)
        +Planta(int, String, double, boolean)
        +realizarAccionEspecial() String
        +deambular() void
    }
    EntidadViva <|-- PersonajeJugable
    EntidadViva <|-- PersonajeNoJugable
    PersonajeNoJugable <|-- Animal
    PersonajeNoJugable <|-- Monstruo
    PersonajeNoJugable <|-- Planta
    Atacante <|.. PersonajeJugable
    Atacante <|.. Monstruo
```

## Interfaz `Atacante`

`PersonajeJugable` y `Monstruo` pertenecen a familias distintas (`EntidadViva` directa y `PersonajeNoJugable`), pero comparten la capacidad de atacar:

- `Atacante.atacar()` es abstracto: cada clase lo implementa a su modo.
- `Atacante.sonidoDeAtaque()` tiene una implementación por defecto. `PersonajeJugable` la hereda y `Monstruo` la reemplaza con su propio texto.

El endpoint `GET /minecraft/ataques` guarda a ambos en una `List<Atacante>` y pide el mensaje a través del tipo de la interfaz.

## Sobrecarga y sobreescritura

**Sobrecarga** (misma clase, mismo nombre, otra lista de argumentos):
- `EntidadViva.recibirDanio(int)` y `EntidadViva.recibirDanio(int, String)`: la segunda indica el origen del daño y lo guarda en `ultimoOrigenDanio`.
- Constructores simples (solo `nombre`) y completos en `EntidadViva`, `PersonajeNoJugable`, `PersonajeJugable`, `Animal`, `Monstruo` y `Planta`. Las clases hijas llaman a `super(...)` y todos los constructores validan, así que el objeto nunca queda en estado ilegal.

**Sobreescritura** (misma firma en la clase hija, otra implementación):
- `EntidadViva.realizarAccionEspecial()` es abstracto. `Animal`, `Monstruo`, `Planta` y `PersonajeJugable` lo implementan, cada uno con su texto.
- `PersonajeNoJugable.deambular()` es abstracto y lo sobreescriben `Animal`, `Monstruo` y `Planta`.

**Cómo se distinguen:** la sobrecarga ocurre dentro de una misma clase y cambia la lista de argumentos. La sobreescritura ocurre entre padre e hija y mantiene la misma firma.

## Cómo probar

```bash
./mvnw spring-boot:run
```

| URL | Qué demuestra |
|---|---|
| `GET /` | El servicio está vivo |
| `GET /minecraft/acciones` | Sobreescritura: JSON de `Animal` y `Monstruo` vía tipo padre |
| `GET /minecraft/ataques` | Interfaz `Atacante`: `PersonajeJugable` y `Monstruo` responden vía el tipo de la interfaz |
| `GET /minecraft/crear-jugador?nombre=Alex&vida=15` | Constructor completo desde la URL |
| `GET /minecraft/crear-jugador?vida=-5` | La clase rechaza el valor (HTTP 400) |
| `GET /minecraft/crear-monstruo?nombre=Zombie` | Constructor simple |
| `GET /minecraft/danio?puntos=5` | Sobrecarga: `recibirDanio(int)` |
| `GET /minecraft/danio?puntos=5&origen=flecha` | Sobrecarga: `recibirDanio(int, String)` |

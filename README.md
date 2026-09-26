# Proyecto Taller LP3 - Dominio Minecraft

## Diagrama de Clases (Dominio)

```mermaid
classDiagram
    class EntidadViva {
        <<abstract>>
        -int vida
        -String nombre
        -double altura
        +realizarAccionEspecial()* String
        +getVida() int
        +setVida(int vida)
        +getNombre() String
        +setNombre(String nombre)
        +getAltura() double
        +setAltura(double altura)
    }

    class PersonajeJugable {
        -int hambre
        -boolean controlable
        -List~String~ inventario
        +realizarAccionEspecial() String
        +getHambre() int
        +setHambre(int hambre)
        +isControlable() boolean
        +setControlable(boolean controlable)
        +getInventario() List~String~
        +agregarAlInventario(String item)
        +quitarDelInventario(String item)
    }

    class PersonajeNoJugable {
        <<abstract>>
        -boolean noControlable
        -boolean crecen
        +deambular()* void
        +isNoControlable() boolean
        +isCrecen() boolean
        +setCrecen(boolean crecen)
    }

    class Animal {
        -boolean domable
        -boolean pacifico
        +realizarAccionEspecial() String
        +deambular() void
        +isDomable() boolean
        +setDomable(boolean domable)
        +isPacifico() boolean
        +setPacifico(boolean pacifico)
    }

    class Monstruo {
        -boolean hostil
        -boolean puedeEstarArmado
        +realizarAccionEspecial() String
        +deambular() void
        +isHostil() boolean
        +setHostil(boolean hostil)
        +isPuedeEstarArmado() boolean
        +setPuedeEstarArmado(boolean puedeEstarArmado)
    }

    class Planta {
        -boolean estatico
        +realizarAccionEspecial() String
        +deambular() void
        +isEstatico() boolean
    }

    EntidadViva <|-- PersonajeJugable
    EntidadViva <|-- PersonajeNoJugable
    PersonajeNoJugable <|-- Animal
    PersonajeNoJugable <|-- Monstruo
    PersonajeNoJugable <|-- Planta

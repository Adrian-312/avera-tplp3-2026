package py.edu.uc.lp3.avera_tplp3_2026.domain;

public abstract class PersonajeNoJugable extends EntidadViva {

    private final boolean noControlable;
    private final boolean crecen;

    // Constructor simple
    protected PersonajeNoJugable(String nombre) {
        super(nombre);
        this.noControlable = true;
        this.crecen = false;
    }

    // Constructor completo
    protected PersonajeNoJugable(int vida, String nombre, double altura, boolean crecen) {
        super(vida, nombre, altura);
        this.noControlable = true;
        this.crecen = crecen;
    }

    public boolean isNoControlable() {
        return noControlable;
    }

    public boolean isCrecen() {
        return crecen;
    }

    // Cada subclase (Monstruo, Animal, Planta) debe definirlo
    public abstract void deambular();
}

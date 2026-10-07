package py.edu.uc.lp3.avera_tplp3_2026.minecraft;
public abstract class PersonajeNoJugable extends EntidadViva {

    private boolean noControlable;
    private boolean crecen;

    public PersonajeNoJugable(int vida, String nombre, double altura, boolean crecen) {
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

    public void setCrecen(boolean crecen) {
        this.crecen = crecen;
    }

    // Método que cada subclase (Monstruo, Animal, Planta) debe definir
    public abstract void deambular();
}

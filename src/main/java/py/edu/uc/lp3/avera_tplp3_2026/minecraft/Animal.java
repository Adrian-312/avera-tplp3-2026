package py.edu.uc.lp3.avera_tplp3_2026.minecraft;
public class Animal extends PersonajeNoJugable {

    private boolean domable;
    private boolean pacifico;

    public Animal(int vida, String nombre, double altura, boolean crecen,
                   boolean domable, boolean pacifico) {
        super(vida, nombre, altura, crecen);
        this.domable = domable;
        this.pacifico = pacifico;
    }

    public boolean isDomable() {
        return domable;
    }

    public void setDomable(boolean domable) {
        this.domable = domable;
    }

    public boolean isPacifico() {
        return pacifico;
    }

    public void setPacifico(boolean pacifico) {
        this.pacifico = pacifico;
    }

    @Override
    public void deambular() {
        System.out.println(getNombre() + " (Animal) está deambulando tranquilamente.");
    }
}

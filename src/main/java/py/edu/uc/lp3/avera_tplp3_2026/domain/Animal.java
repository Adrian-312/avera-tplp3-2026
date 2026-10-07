package py.edu.uc.lp3.avera_tplp3_2026.domain;

public class Animal extends PersonajeNoJugable {

    private final boolean domable;
    private final boolean pacifico;

    // Constructor simple
    public Animal(String nombre) {
        super(nombre);
        this.domable = false;
        this.pacifico = true;
    }

    // Constructor completo
    public Animal(int vida, String nombre, double altura, boolean crecen,
                  boolean domable, boolean pacifico) {
        super(vida, nombre, altura, crecen);
        this.domable = domable;
        this.pacifico = pacifico;
    }

    @Override
    public String realizarAccionEspecial() {
        return getNombre() + " (Animal) busca alimento y pasta tranquilamente en el prado.";
    }

    @Override
    public void deambular() {
        System.out.println(getNombre() + " (Animal) está deambulando tranquilamente.");
    }

    public boolean isDomable() {
        return domable;
    }

    public boolean isPacifico() {
        return pacifico;
    }
}

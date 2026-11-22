package py.edu.uc.lp3.avera_tplp3_2026.domain;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_ANIMAL;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(TIPO_ANIMAL)
public class Animal extends PersonajeNoJugable {

    private boolean domable;
    private boolean pacifico;

    protected Animal() {
        super();
    }

    // Constructor simple.
    public Animal(String nombre) {
        super(nombre);
        this.domable = false;
        this.pacifico = true;
    }

    // Constructor completo.
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

    public void setDomable(boolean domable) {
        this.domable = domable;
    }

    public boolean isPacifico() {
        return pacifico;
    }

    public void setPacifico(boolean pacifico) {
        this.pacifico = pacifico;
    }
}

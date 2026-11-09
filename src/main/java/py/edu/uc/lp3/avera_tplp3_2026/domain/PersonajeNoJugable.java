package py.edu.uc.lp3.avera_tplp3_2026.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("no_jugable")
public abstract class PersonajeNoJugable extends EntidadViva {

    private boolean noControlable;
    private boolean crecen;

    protected PersonajeNoJugable() {
        super();
    }

    // Constructor simple.
    protected PersonajeNoJugable(String nombre) {
        super(nombre);
        this.noControlable = true;
        this.crecen = false;
    }

    // Constructor completo.
    protected PersonajeNoJugable(int vida, String nombre, double altura, boolean crecen) {
        super(vida, nombre, altura);
        this.noControlable = true;
        this.crecen = crecen;
    }

    public void actualizarCrecimiento(boolean crecen) {
        this.crecen = crecen;
    }

    public boolean isNoControlable() {
        return noControlable;
    }

    public boolean isCrecen() {
        return crecen;
    }

    public abstract void deambular();
}

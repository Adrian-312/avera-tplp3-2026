package py.edu.uc.lp3.avera_tplp3_2026.domain;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_PLANTA;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(TIPO_PLANTA)
public class Planta extends PersonajeNoJugable {

    private boolean estatico = true;

    protected Planta() {
        super();
    }

    // Constructor simple.
    public Planta(String nombre) {
        super(nombre);
    }

    // Constructor completo.
    public Planta(int vida, String nombre, double altura, boolean crecen) {
        super(vida, nombre, altura, crecen);
    }

    @Override
    public String realizarAccionEspecial() {
        return getNombre() + " (Planta) fotosintetiza en su lugar.";
    }

    @Override
    public void deambular() {
        System.out.println(getNombre() + " (Planta) es estática y no deambula.");
    }

    public boolean isEstatico() {
        return estatico;
    }
}

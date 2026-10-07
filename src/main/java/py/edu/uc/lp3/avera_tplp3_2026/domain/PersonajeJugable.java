package py.edu.uc.lp3.avera_tplp3_2026.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonajeJugable extends EntidadViva {

    public static final int HAMBRE_MAXIMA = 20;

    private int hambre;
    private final boolean controlable;
    private final List<String> inventario = new ArrayList<>();

    // Constructor simple
    public PersonajeJugable(String nombre) {
        this(VIDA_MAXIMA, nombre, 1.8, HAMBRE_MAXIMA, true);
    }

    // Constructor completo
    public PersonajeJugable(int vida, String nombre, double altura, int hambre, boolean controlable) {
        super(vida, nombre, altura);
        if (hambre < 0 || hambre > HAMBRE_MAXIMA) {
            throw new IllegalArgumentException("El hambre debe estar entre 0 y " + HAMBRE_MAXIMA);
        }
        this.hambre = hambre;
        this.controlable = controlable;
    }

    @Override
    public String realizarAccionEspecial() {
        return getNombre() + " (Jugador) explora la cueva, construye refugios e interactúa con el entorno.";
    }

    public void comer(int puntos) {
        if (puntos < 0) {
            throw new IllegalArgumentException("Los puntos de comida no pueden ser negativos");
        }
        hambre = Math.min(HAMBRE_MAXIMA, hambre + puntos);
    }

    public int getHambre() {
        return hambre;
    }

    public boolean isControlable() {
        return controlable;
    }

    public List<String> getInventario() {
        return Collections.unmodifiableList(inventario);
    }

    public void agregarAlInventario(String item) {
        if (item == null || item.isBlank()) {
            throw new IllegalArgumentException("El ítem no puede estar vacío");
        }
        inventario.add(item.trim());
    }

    public void quitarDelInventario(String item) {
        inventario.remove(item);
    }

    @Override
    public String toString() {
        return "PersonajeJugable{nombre='" + getNombre() + "', vida=" + getVida()
                + ", hambre=" + hambre + ", controlable=" + controlable
                + ", inventario=" + inventario + '}';
    }
}

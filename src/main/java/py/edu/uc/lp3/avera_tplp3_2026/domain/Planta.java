package py.edu.uc.lp3.avera_tplp3_2026.domain;

public class Planta extends PersonajeNoJugable {

    private final boolean estatico = true; // las plantas no se mueven

    // Constructor simple
    public Planta(String nombre) {
        super(nombre);
    }

    // Constructor completo
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

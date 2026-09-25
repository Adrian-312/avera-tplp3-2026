public class Planta extends PersonajeNoJugable {

    private boolean estatico;

    public Planta(int vida, String nombre, double altura, boolean crecen) {
        super(vida, nombre, altura, crecen);
        this.estatico = true; // las plantas no se mueven
    }

    public boolean isEstatico() {
        return estatico;
    }

    @Override
    public void deambular() {
        // Una planta es estática, por lo que no se desplaza
        System.out.println(getNombre() + " (Planta) es estática y no deambula.");
    }
}

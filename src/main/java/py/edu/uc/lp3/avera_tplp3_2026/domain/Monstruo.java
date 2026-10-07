package py.edu.uc.lp3.avera_tplp3_2026.domain;

public class Monstruo extends PersonajeNoJugable {

    private boolean hostil;
    private final boolean puedeEstarArmado;

    // Constructor simple
    public Monstruo(String nombre) {
        super(nombre);
        this.hostil = true;
        this.puedeEstarArmado = false;
    }

    // Constructor completo
    public Monstruo(int vida, String nombre, double altura, boolean crecen,
                    boolean hostil, boolean puedeEstarArmado) {
        super(vida, nombre, altura, crecen);
        this.hostil = hostil;
        this.puedeEstarArmado = puedeEstarArmado;
    }

    @Override
    public String realizarAccionEspecial() {
        return getNombre() + " (Monstruo) ruge y ataca a los jugadores cercanos en la oscuridad!";
    }

    @Override
    public void deambular() {
        System.out.println(getNombre() + " (Monstruo) está deambulando de forma hostil.");
    }

    public void cambiarHostilidad() {
        this.hostil = !this.hostil;
    }

    public boolean isHostil() {
        return hostil;
    }

    public boolean isPuedeEstarArmado() {
        return puedeEstarArmado;
    }
}

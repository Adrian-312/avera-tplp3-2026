package py.edu.uc.lp3.avera_tplp3_2026.minecraft;
public class Monstruo extends PersonajeNoJugable {

    private boolean hostil;
    private boolean puedeEstarArmado;

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

    public boolean isHostil() {
        return hostil;
    }

    public void setHostil(boolean hostil) {
        this.hostil = hostil;
    }

    public boolean isPuedeEstarArmado() {
        return puedeEstarArmado;
    }

    public void setPuedeEstarArmado(boolean puedeEstarArmado) {
        this.puedeEstarArmado = puedeEstarArmado;
    }

    @Override
    public void deambular() {
        System.out.println(getNombre() + " (Monstruo) está deambulando de forma hostil.");
    }
}

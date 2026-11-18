package py.edu.uc.lp3.avera_tplp3_2026.domain;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.POTENCIA_EXPLOSIVA_CREEPER;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_MONSTRUO;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue(TIPO_MONSTRUO)
public class Monstruo extends PersonajeNoJugable implements Explosivo {

    private boolean hostil;
    private boolean puedeEstarArmado;
    private int potenciaExplosiva;

    protected Monstruo() {
        super();
    }

    // Constructor simple.
    public Monstruo(String nombre) {
        super(nombre);
        this.hostil = true;
        this.puedeEstarArmado = false;
        this.potenciaExplosiva = POTENCIA_EXPLOSIVA_CREEPER;
    }

    // Constructor completo.
    public Monstruo(int vida, String nombre, double altura, boolean crecen,
                    boolean hostil, boolean puedeEstarArmado) {
        this(vida, nombre, altura, crecen, hostil, puedeEstarArmado, POTENCIA_EXPLOSIVA_CREEPER);
    }

    // Constructor completo con potencia explosiva propia.
    public Monstruo(int vida, String nombre, double altura, boolean crecen,
                    boolean hostil, boolean puedeEstarArmado, int potenciaExplosiva) {
        super(vida, nombre, altura, crecen);
        this.hostil = hostil;
        this.puedeEstarArmado = puedeEstarArmado;
        this.potenciaExplosiva = potenciaExplosiva;
    }

    @Override
    public String realizarAccionEspecial() {
        return getNombre() + " (Monstruo) ruge y persigue a los jugadores en la oscuridad.";
    }

    @Override
    public int poderExplosivo() {
        return potenciaExplosiva;
    }

    @Override
    public String sonidoDeExplosion() {
        return getNombre() + " infla su cuerpo: ¡fssss... BOOM!";
    }

    @Override
    public void deambular() {
        System.out.println(getNombre() + " (Monstruo) está deambulando de forma hostil.");
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

    public int getPotenciaExplosiva() {
        return potenciaExplosiva;
    }

    public void setPotenciaExplosiva(int potenciaExplosiva) {
        this.potenciaExplosiva = potenciaExplosiva;
    }
}

package py.edu.uc.lp3.avera_tplp3_2026.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;

@Entity
@DiscriminatorValue("monstruo")
public class Monstruo extends PersonajeNoJugable implements Explosivo {

    public static final int POTENCIA_EXPLOSIVA_CREEPER = 10;

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
        validarPotencia(potenciaExplosiva);
        this.hostil = hostil;
        this.puedeEstarArmado = puedeEstarArmado;
        this.potenciaExplosiva = potenciaExplosiva;
    }

    private static void validarPotencia(int potenciaExplosiva) {
        if (potenciaExplosiva <= 0 || potenciaExplosiva > 50) {
            throw new EntidadInvalidaException("La potencia explosiva debe estar entre 1 y 50");
        }
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

    // Primera regla del juego: el monstruo explosivo se autodestruye y produce
    // una explosión con la potencia indicada.
    public Explosion explotar(int potencia) {
        if (!isVivo()) {
            throw new EntidadInvalidaException("El monstruo " + getNombre() + " ya no está vivo y no puede explotar");
        }
        if (!hostil) {
            throw new EntidadInvalidaException("El monstruo " + getNombre() + " no es hostil y no explota");
        }
        if (potencia <= 0 || potencia > potenciaExplosiva) {
            throw new EntidadInvalidaException(
                    "La potencia debe estar entre 1 y " + potenciaExplosiva + " para " + getNombre());
        }
        Explosion explosion = new Explosion(getId(), getNombre(), potencia);
        recibirDanio(getVida(), "explosión de " + getNombre());
        return explosion;
    }

    @Override
    public void deambular() {
        System.out.println(getNombre() + " (Monstruo) está deambulando de forma hostil.");
    }

    public void cambiarHostilidad() {
        this.hostil = !this.hostil;
    }

    public void actualizarHostilidad(boolean hostil) {
        this.hostil = hostil;
    }

    public void actualizarArmamento(boolean puedeEstarArmado) {
        this.puedeEstarArmado = puedeEstarArmado;
    }

    public void actualizarPotenciaExplosiva(int potenciaExplosiva) {
        validarPotencia(potenciaExplosiva);
        this.potenciaExplosiva = potenciaExplosiva;
    }

    public boolean isHostil() {
        return hostil;
    }

    public boolean isPuedeEstarArmado() {
        return puedeEstarArmado;
    }

    public int getPotenciaExplosiva() {
        return potenciaExplosiva;
    }
}

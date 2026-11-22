package py.edu.uc.lp3.avera_tplp3_2026.domain;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ALTURA_JUGADOR_DEFECTO;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.HAMBRE_MAXIMA;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ITEM_TNT;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.PODER_EXPLOSIVO_POR_TNT;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_JUGADOR;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.VIDA_MAXIMA;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;

@Entity
@DiscriminatorValue(TIPO_JUGADOR)
public class PersonajeJugable extends EntidadViva implements Explosivo {

    private int hambre;
    private boolean controlable;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "jugador_inventario", joinColumns = @JoinColumn(name = "entidad_id"))
    @Column(name = "item")
    private List<String> inventario = new ArrayList<>();

    protected PersonajeJugable() {
        super();
    }

    // Constructor simple.
    public PersonajeJugable(String nombre) {
        this(VIDA_MAXIMA, nombre, ALTURA_JUGADOR_DEFECTO, HAMBRE_MAXIMA, true);
    }

    // Constructor completo.
    public PersonajeJugable(int vida, String nombre, double altura, int hambre, boolean controlable) {
        super(vida, nombre, altura);
        this.hambre = hambre;
        this.controlable = controlable;
    }

    @Override
    public String realizarAccionEspecial() {
        return getNombre() + " (Jugador) explora la cueva, construye refugios e interactúa con el entorno.";
    }

    @Override
    public int poderExplosivo() {
        return (int) inventario.stream().filter(item -> item.equalsIgnoreCase(ITEM_TNT)).count()
                * PODER_EXPLOSIVO_POR_TNT;
    }

    @Override
    public String sonidoDeExplosion() {
        return getNombre() + " enciende la TNT: ¡fssss... BOOM!";
    }

    public int getHambre() {
        return hambre;
    }

    public void setHambre(int hambre) {
        this.hambre = hambre;
    }

    public boolean isControlable() {
        return controlable;
    }

    public void setControlable(boolean controlable) {
        this.controlable = controlable;
    }

    public List<String> getInventario() {
        return inventario;
    }

    public void setInventario(List<String> items) {
        List<String> copia = items == null ? new ArrayList<>() : new ArrayList<>(items);
        this.inventario.clear();
        this.inventario.addAll(copia);
    }

    @Override
    public String toString() {
        return "PersonajeJugable{id=" + getId() + ", nombre='" + getNombre() + "', vida=" + getVida()
                + ", hambre=" + hambre + ", controlable=" + controlable
                + ", inventario=" + inventario + '}';
    }
}

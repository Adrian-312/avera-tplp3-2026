package py.edu.uc.lp3.avera_tplp3_2026.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;

@Entity
@DiscriminatorValue("jugador")
public class PersonajeJugable extends EntidadViva implements Explosivo {

    public static final int HAMBRE_MAXIMA = 20;
    public static final int ESCUDO_ABSORCION = 5;

    private int hambre;
    private boolean controlable;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "jugador_inventario", joinColumns = @JoinColumn(name = "entidad_id"))
    @Column(name = "item")
    private List<String> inventario = new ArrayList<>();

    protected PersonajeJugable() {
        super();
        this.inventario = new ArrayList<>();
    }

    // Constructor simple.
    public PersonajeJugable(String nombre) {
        this(VIDA_MAXIMA, nombre, 1.8, HAMBRE_MAXIMA, true);
    }

    // Constructor completo.
    public PersonajeJugable(int vida, String nombre, double altura, int hambre, boolean controlable) {
        super(vida, nombre, altura);
        validarHambre(hambre);
        this.hambre = hambre;
        this.controlable = controlable;
        this.inventario = new ArrayList<>();
    }

    private static void validarHambre(int hambre) {
        if (hambre < 0 || hambre > HAMBRE_MAXIMA) {
            throw new EntidadInvalidaException("El hambre debe estar entre 0 y " + HAMBRE_MAXIMA);
        }
    }

    @Override
    public String realizarAccionEspecial() {
        return getNombre() + " (Jugador) explora la cueva, construye refugios e interactúa con el entorno.";
    }

    @Override
    public int poderExplosivo() {
        return (int) inventario.stream().filter(item -> item.equalsIgnoreCase("TNT")).count() * 4;
    }

    @Override
    public String sonidoDeExplosion() {
        return getNombre() + " enciende la TNT: ¡fssss... BOOM!";
    }

    // Segunda regla del juego: cada escudo absorbe 5 puntos de la onda expansiva
    // y se consume del inventario. Sin escudos, el daño llega completo.
    @Override
    public int recibirExplosion(int potencia, String origen) {
        int danioRestante = potencia;
        int escudosConsumidos = 0;
        while (danioRestante > 0 && consumirItem("escudo")) {
            danioRestante = Math.max(0, danioRestante - ESCUDO_ABSORCION);
            escudosConsumidos++;
        }
        recibirDanio(danioRestante, origen);
        return escudosConsumidos;
    }

    public void comer(int puntos) {
        if (puntos < 0) {
            throw new EntidadInvalidaException("Los puntos de comida no pueden ser negativos");
        }
        hambre = Math.min(HAMBRE_MAXIMA, hambre + puntos);
    }

    public void actualizarHambre(int nuevaHambre) {
        validarHambre(nuevaHambre);
        this.hambre = nuevaHambre;
    }

    public void actualizarControl(boolean controlable) {
        this.controlable = controlable;
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
            throw new EntidadInvalidaException("El ítem no puede estar vacío");
        }
        inventario.add(item.trim());
    }

    public void quitarDelInventario(String item) {
        inventario.removeIf(i -> i.equalsIgnoreCase(item));
    }

    public void reemplazarInventario(List<String> items) {
        inventario.clear();
        if (items == null) {
            return;
        }
        for (String item : items) {
            agregarAlInventario(item);
        }
    }

    private boolean consumirItem(String item) {
        for (int i = 0; i < inventario.size(); i++) {
            if (inventario.get(i).equalsIgnoreCase(item)) {
                inventario.remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "PersonajeJugable{id=" + getId() + ", nombre='" + getNombre() + "', vida=" + getVida()
                + ", hambre=" + hambre + ", controlable=" + controlable
                + ", inventario=" + inventario + '}';
    }
}

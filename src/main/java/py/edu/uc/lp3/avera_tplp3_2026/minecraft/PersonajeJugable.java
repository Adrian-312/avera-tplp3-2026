package py.edu.uc.lp3.avera_tplp3_2026.minecraft;
import java.util.ArrayList;
import java.util.List;

public class PersonajeJugable extends EntidadViva {

    private int hambre;
    private boolean controlable;
    private List<String> inventario;

    public PersonajeJugable(int vida, String nombre, double altura, int hambre, boolean controlable) {
        super(vida, nombre, altura);
        this.hambre = hambre;
        this.controlable = controlable;
        this.inventario = new ArrayList<>();
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

    public void agregarAlInventario(String item) {
        inventario.add(item);
    }

    public void quitarDelInventario(String item) {
        inventario.remove(item);
    }

    @Override
    public String toString() {
        return "PersonajeJugable{" +
                "nombre='" + getNombre() + '\'' +
                ", vida=" + getVida() +
                ", hambre=" + hambre +
                ", controlable=" + controlable +
                ", inventario=" + inventario +
                '}';
    }
}

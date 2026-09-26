package py.edu.uc.lp3.avera_tplp3_2026.minecraft;
public abstract class EntidadViva {

    private int vida;
    private String nombre;
    private double altura;

    public EntidadViva(int vida, String nombre, double altura) {
        this.vida = vida;
        this.nombre = nombre;
        this.altura = altura;
    }

    public abstract String realizarAccionEspecial();

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getAltura() {
        return altura;
    }

    public void setAltura(double altura) {
        this.altura = altura;
    }

    @Override
    public String toString() {
        return "EntidadViva{" +
                "vida=" + vida +
                ", nombre='" + nombre + '\'' +
                ", altura=" + altura +
                '}';
    }
}

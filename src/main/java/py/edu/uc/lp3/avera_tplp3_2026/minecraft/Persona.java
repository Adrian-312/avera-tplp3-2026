package py.edu.uc.lp3.avera_tplp3_2026.minecraft;

import java.io.Serializable;

public class Persona implements Serializable {

    private static final long serialVersionUID = -4419931048984717671L;

    private String nombre;
    private String apellido;
    private int edad;
    private int numeroCedula;
    private String codigoPais;

    public Persona() {
    }

    public Persona(String nombre, String apellido, int edad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
    }

    public Persona(int numeroCedula, String nombre) {
        this.nombre = nombre;
        this.numeroCedula = numeroCedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getNumeroCedula() {
        return numeroCedula;
    }

    public void setNumeroCedula(int numeroCedula) {
        this.numeroCedula = numeroCedula;
    }

    public String getCodigoISOPais() {
        return codigoPais;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public int compareTo(Persona otraPersona) {
        if (getNombre() == null || otraPersona.getNombre() == null) {
            return 0;
        }
        return getNombre().compareTo(otraPersona.getNombre()) * -1;
    }

    @Override
    public String toString() {
        return getNombre() + ", " + getApellido();
    }
}
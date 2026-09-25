package py.edu.uc.lp3.ja_taller_git_2026.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "armas")
public class Arma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String tipo;
    private Integer precio;
    private Integer danoBase;

    public Arma() {}

    public Arma(String nombre, String tipo, Integer precio, Integer danoBase) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.precio = precio;
        this.danoBase = danoBase;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }

    public Integer getDanoBase() { return danoBase; }
    public void setDanoBase(Integer danoBase) { this.danoBase = danoBase; }
}

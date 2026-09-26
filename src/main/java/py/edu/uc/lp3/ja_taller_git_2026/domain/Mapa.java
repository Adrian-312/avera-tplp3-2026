package py.edu.uc.lp3.ja_taller_git_2026.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "mapas")
public class Mapa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String ubicacion;
    private Boolean activoEnCompetitivo;

    public Mapa() {}

    public Mapa(String nombre, String ubicacion, Boolean activoEnCompetitivo) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.activoEnCompetitivo = activoEnCompetitivo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }

    public Boolean getActivoEnCompetitivo() { return activoEnCompetitivo; }
    public void setActivoEnCompetitivo(Boolean activoEnCompetitivo) { this.activoEnCompetitivo = activoEnCompetitivo; }
}

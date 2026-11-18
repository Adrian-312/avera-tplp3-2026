package py.edu.uc.lp3.avera_tplp3_2026.domain;

import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ALTURA_DEFECTO;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.ORIGEN_DANIO_NINGUNO;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_ANIMAL;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_JUGADOR;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_MONSTRUO;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.TIPO_PLANTA;
import static py.edu.uc.lp3.avera_tplp3_2026.constants.MinecraftConstants.VIDA_MAXIMA;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

// Entity: solo estado. Las validaciones, las reglas del juego y las excepciones
// viven en la capa service.
@Entity
@Table(name = "entidad_viva")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_entidad", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersonajeJugable.class, name = TIPO_JUGADOR),
        @JsonSubTypes.Type(value = Animal.class, name = TIPO_ANIMAL),
        @JsonSubTypes.Type(value = Monstruo.class, name = TIPO_MONSTRUO),
        @JsonSubTypes.Type(value = Planta.class, name = TIPO_PLANTA)
})
public abstract class EntidadViva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int vida;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private double altura;

    @Column(name = "ultimo_origen_danio")
    private String ultimoOrigenDanio = ORIGEN_DANIO_NINGUNO;

    // Constructor vacío para JPA.
    protected EntidadViva() {
    }

    // Constructor simple: vida completa y altura estándar.
    protected EntidadViva(String nombre) {
        this(VIDA_MAXIMA, nombre, ALTURA_DEFECTO);
    }

    // Constructor completo.
    protected EntidadViva(int vida, String nombre, double altura) {
        this.vida = vida;
        this.nombre = nombre;
        this.altura = altura;
    }

    // SOBREESCRITURA: cada clase hija lo implementa a su manera.
    public abstract String realizarAccionEspecial();

    public Long getId() {
        return id;
    }

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

    public String getUltimoOrigenDanio() {
        return ultimoOrigenDanio;
    }

    public void setUltimoOrigenDanio(String ultimoOrigenDanio) {
        this.ultimoOrigenDanio = ultimoOrigenDanio;
    }

    public boolean isVivo() {
        return vida > 0;
    }

    @Override
    public String toString() {
        return "EntidadViva{id=" + id + ", vida=" + vida + ", nombre='" + nombre
                + "', altura=" + altura + '}';
    }
}

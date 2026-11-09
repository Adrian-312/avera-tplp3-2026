package py.edu.uc.lp3.avera_tplp3_2026.domain;

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
import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;

@Entity
@Table(name = "entidad_viva")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_entidad", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PersonajeJugable.class, name = "jugador"),
        @JsonSubTypes.Type(value = Animal.class, name = "animal"),
        @JsonSubTypes.Type(value = Monstruo.class, name = "monstruo"),
        @JsonSubTypes.Type(value = Planta.class, name = "planta")
})
public abstract class EntidadViva {

    public static final int VIDA_MAXIMA = 20;

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
    private String ultimoOrigenDanio = "ninguno";

    // Constructor vacío para JPA. Es protected para que el resto del código no
    // pueda crear entidades sin pasar por las validaciones de negocio.
    protected EntidadViva() {
        this.ultimoOrigenDanio = "ninguno";
    }

    // Constructor simple: vida completa y altura estándar.
    protected EntidadViva(String nombre) {
        this(VIDA_MAXIMA, nombre, 1.0);
    }

    // Constructor completo: aquí viven todas las reglas de construcción.
    protected EntidadViva(int vida, String nombre, double altura) {
        validarNombre(nombre);
        validarVida(vida);
        validarAltura(altura);
        this.vida = vida;
        this.nombre = nombre.trim();
        this.altura = altura;
        this.ultimoOrigenDanio = "ninguno";
    }

    protected static void validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new EntidadInvalidaException("El nombre no puede estar vacío");
        }
    }

    protected static void validarVida(int vida) {
        if (vida < 0 || vida > VIDA_MAXIMA) {
            throw new EntidadInvalidaException("La vida debe estar entre 0 y " + VIDA_MAXIMA);
        }
    }

    protected static void validarAltura(double altura) {
        if (!(altura > 0) || altura > 10) {
            throw new EntidadInvalidaException("La altura debe ser mayor que 0 y no superar 10");
        }
    }

    // SOBREESCRITURA: cada clase hija lo implementa a su manera.
    public abstract String realizarAccionEspecial();

    // Mensajes del dominio para modificar estado sin exponer setters públicos.
    public void actualizarPerfil(String nombre, double altura) {
        validarNombre(nombre);
        validarAltura(altura);
        this.nombre = nombre.trim();
        this.altura = altura;
    }

    public void actualizarVida(int nuevaVida) {
        validarVida(nuevaVida);
        this.vida = nuevaVida;
    }

    // SOBRECARGA: misma acción, distinta lista de argumentos.
    public void recibirDanio(int puntos) {
        if (puntos < 0) {
            throw new EntidadInvalidaException("El daño no puede ser negativo");
        }
        vida = Math.max(0, vida - puntos);
    }

    public void recibirDanio(int puntos, String origen) {
        if (origen == null || origen.isBlank()) {
            throw new EntidadInvalidaException("El origen del daño no puede estar vacío");
        }
        recibirDanio(puntos);
        this.ultimoOrigenDanio = origen.trim();
    }

    public void curar(int puntos) {
        if (puntos < 0) {
            throw new EntidadInvalidaException("La curación no puede ser negativa");
        }
        vida = Math.min(VIDA_MAXIMA, vida + puntos);
    }

    // Regla de la onda expansiva. El valor devuelto son los escudos consumidos.
    // Las clases hijas pueden sobreescribir este comportamiento.
    public int recibirExplosion(int potencia, String origen) {
        recibirDanio(potencia, origen);
        return 0;
    }

    public boolean isVivo() {
        return vida > 0;
    }

    public Long getId() {
        return id;
    }

    public int getVida() {
        return vida;
    }

    public String getNombre() {
        return nombre;
    }

    public double getAltura() {
        return altura;
    }

    public String getUltimoOrigenDanio() {
        return ultimoOrigenDanio;
    }

    @Override
    public String toString() {
        return "EntidadViva{id=" + id + ", vida=" + vida + ", nombre='" + nombre
                + "', altura=" + altura + '}';
    }
}

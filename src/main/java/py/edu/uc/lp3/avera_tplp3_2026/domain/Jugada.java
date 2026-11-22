package py.edu.uc.lp3.avera_tplp3_2026.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jugada")
public class Jugada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long jugadorId;

    @Column(nullable = false)
    private String nombreJugador;

    @Column(nullable = false)
    private Long monstruoId;

    @Column(nullable = false)
    private String nombreMonstruo;

    @Column(nullable = false)
    private int potencia;

    @Column(nullable = false)
    private int escudosConsumidos;

    @Column(nullable = false)
    private int vidaJugadorDespues;

    @Column(nullable = false)
    private boolean aceptadaPorA2;

    @Column(nullable = false)
    private boolean aceptadaPorA3;

    private String motivo;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected Jugada() {
    }

    public Jugada(Long jugadorId, String nombreJugador, Long monstruoId, String nombreMonstruo,
                  int potencia, int escudosConsumidos, int vidaJugadorDespues,
                  boolean aceptadaPorA2, boolean aceptadaPorA3, String motivo) {
        this.jugadorId = jugadorId;
        this.nombreJugador = nombreJugador;
        this.monstruoId = monstruoId;
        this.nombreMonstruo = nombreMonstruo;
        this.potencia = potencia;
        this.escudosConsumidos = escudosConsumidos;
        this.vidaJugadorDespues = vidaJugadorDespues;
        this.aceptadaPorA2 = aceptadaPorA2;
        this.aceptadaPorA3 = aceptadaPorA3;
        this.motivo = motivo;
        this.fecha = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public Long getMonstruoId() {
        return monstruoId;
    }

    public String getNombreMonstruo() {
        return nombreMonstruo;
    }

    public int getPotencia() {
        return potencia;
    }

    public int getEscudosConsumidos() {
        return escudosConsumidos;
    }

    public int getVidaJugadorDespues() {
        return vidaJugadorDespues;
    }

    public boolean isAceptadaPorA2() {
        return aceptadaPorA2;
    }

    public boolean isAceptadaPorA3() {
        return aceptadaPorA3;
    }

    public String getMotivo() {
        return motivo;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}

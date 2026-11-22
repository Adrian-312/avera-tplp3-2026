package py.edu.uc.lp3.avera_tplp3_2026.domain;

import java.time.Instant;

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

    @Column(name = "jugador_id", nullable = false)
    private Long jugadorId;

    @Column(name = "jugador_nombre", nullable = false)
    private String jugadorNombre;

    @Column(name = "monstruo_id", nullable = false)
    private Long monstruoId;

    @Column(name = "monstruo_nombre", nullable = false)
    private String monstruoNombre;

    @Column(nullable = false)
    private int potencia;

    @Column(name = "escudos_consumidos", nullable = false)
    private int escudosConsumidos;

    @Column(name = "vida_jugador_despues", nullable = false)
    private int vidaJugadorDespues;

    @Column(name = "aceptada_por_a2", nullable = false)
    private boolean aceptadaPorA2;

    @Column(name = "aceptada_por_a3", nullable = false)
    private boolean aceptadaPorA3;

    private String motivo;

    @Column(nullable = false)
    private Instant fecha;

    protected Jugada() {
    }

    public Jugada(Long jugadorId, String jugadorNombre, Long monstruoId, String monstruoNombre,
                  int potencia, int escudosConsumidos, int vidaJugadorDespues,
                  boolean aceptadaPorA2, boolean aceptadaPorA3, String motivo) {
        this.jugadorId = jugadorId;
        this.jugadorNombre = jugadorNombre;
        this.monstruoId = monstruoId;
        this.monstruoNombre = monstruoNombre;
        this.potencia = potencia;
        this.escudosConsumidos = escudosConsumidos;
        this.vidaJugadorDespues = vidaJugadorDespues;
        this.aceptadaPorA2 = aceptadaPorA2;
        this.aceptadaPorA3 = aceptadaPorA3;
        this.motivo = motivo;
        this.fecha = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getJugadorId() {
        return jugadorId;
    }

    public String getJugadorNombre() {
        return jugadorNombre;
    }

    public Long getMonstruoId() {
        return monstruoId;
    }

    public String getMonstruoNombre() {
        return monstruoNombre;
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

    public Instant getFecha() {
        return fecha;
    }
}

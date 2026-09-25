package py.edu.uc.lp3.ja_taller_git_2026.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "jugadores")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String bando;
    private Integer dinero;
    private Integer salud;

    public Jugador() {}

    public Jugador(String nickname, String bando, Integer dinero, Integer salud) {
        this.nickname = nickname;
        this.bando = bando;
        this.dinero = dinero;
        this.salud = salud;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getBando() { return bando; }
    public void setBando(String bando) { this.bando = bando; }

    public Integer getDinero() { return dinero; }
    public void setDinero(Integer dinero) { this.dinero = dinero; }

    public Integer getSalud() { return salud; }
    public void setSalud(Integer salud) { this.salud = salud; }
}

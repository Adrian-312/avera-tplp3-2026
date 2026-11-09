package py.edu.uc.lp3.avera_tplp3_2026.domain;

public interface Explosivo {

    int poderExplosivo();

    default String sonidoDeExplosion() {
        return "Una onda expansiva sacude el terreno.";
    }
}

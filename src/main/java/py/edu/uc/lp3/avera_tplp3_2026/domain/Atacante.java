package py.edu.uc.lp3.avera_tplp3_2026.domain;

public interface Atacante {

    String atacar();

    default String sonidoDeAtaque() {
        return "Un golpe seco resuena contra el bloque.";
    }
}

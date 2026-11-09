package py.edu.uc.lp3.avera_tplp3_2026.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.avera_tplp3_2026.domain.exception.EntidadInvalidaException;

class SimulacionDomainTest {

    @Test
    void elCreeperExplotaYMUere() {
        Monstruo creeper = new Monstruo("Creeper");

        Explosion explosion = creeper.explotar(10);

        assertEquals(10, explosion.potencia());
        assertEquals(0, creeper.getVida());
        assertFalse(creeper.isVivo());
    }

    @Test
    void losEscudosAbsorbenDanioYSeConsumen() {
        PersonajeJugable jugador = new PersonajeJugable("Steve");
        jugador.agregarAlInventario("escudo");
        jugador.agregarAlInventario("escudo");

        int escudos = jugador.recibirExplosion(12, "explosión de Creeper");

        assertEquals(2, escudos);
        assertEquals(18, jugador.getVida());
        assertEquals(0, jugador.getInventario().size());
    }

    @Test
    void unAnimalSinEscudoRecibeElDanioCompleto() {
        Animal vaca = new Animal("Vaca");

        int escudos = vaca.recibirExplosion(7, "explosión de Creeper");

        assertEquals(0, escudos);
        assertEquals(13, vaca.getVida());
    }

    @Test
    void unaPotenciaInvalidaLanzaExcepcionDelDominio() {
        Monstruo creeper = new Monstruo("Creeper");

        assertThrows(EntidadInvalidaException.class, () -> creeper.explotar(0));
    }
}

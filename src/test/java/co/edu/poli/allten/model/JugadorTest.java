package co.edu.poli.allten.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class JugadorTest {

    @Test
    public void constructor_normalizaApodoYAsignaIdInicial() {
        Jugador jugador = new Jugador("  Liliana  ");

        assertEquals("Liliana", jugador.getApodo());
        assertEquals(0, jugador.getId());
    }

    @Test
    public void constructorConId_conservaIdYNormalizaApodo() {
        Jugador jugador = new Jugador(42, "  John117  ");

        assertEquals(42, jugador.getId());
        assertEquals("John117", jugador.getApodo());
    }

    @Test
    public void constructor_rechazaApodosNulosOVacios() {
        assertThrows(IllegalArgumentException.class, () -> new Jugador(null));
        assertThrows(IllegalArgumentException.class, () -> new Jugador(""));
        assertThrows(IllegalArgumentException.class, () -> new Jugador("   "));
    }

    @Test
    public void setId_actualizaElIdentificadorPersistido() {
        Jugador jugador = new Jugador("Lili");

        jugador.setId(17);

        assertEquals(17, jugador.getId());
    }
}

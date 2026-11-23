package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PeliculaTest {

    private Pelicula pelicula;
    private Actor zoe;

    @BeforeEach
    void preparar() {
        pelicula = new Pelicula(1, "Avatar", 162, "Ciencia ficción", "20th Century Studios");
        zoe = new Actor(1, "Zoe Saldaña", "Estados Unidos", 2000);
    }

    @Test
    void agregaActoresAlReparto() {
        assertTrue(pelicula.agregarActor(zoe));
        assertEquals(1, pelicula.getActores().size());
        assertTrue(pelicula.obtenerDetalles().contains("Zoe Saldaña"));
    }

    @Test
    void noRepiteUnActorEnElReparto() {
        pelicula.agregarActor(zoe);
        assertFalse(pelicula.agregarActor(new Actor(1, "Zoe Saldaña", "Estados Unidos", 2000)));
        assertEquals(1, pelicula.getActores().size());
    }

    @Test
    void quitaActores() {
        pelicula.agregarActor(zoe);
        assertTrue(pelicula.quitarActor(zoe));
        assertTrue(pelicula.getActores().isEmpty());
    }

    @Test
    void elRepartoNoSePuedeModificarDesdeAfuera() {
        assertThrows(UnsupportedOperationException.class, () -> pelicula.getActores().add(zoe));
    }

    @Test
    void elEstudioEsObligatorio() {
        assertThrows(IllegalArgumentException.class, () -> pelicula.setEstudio(""));
        pelicula.setEstudio("Lightstorm");
        assertEquals("Lightstorm", pelicula.getEstudio());
    }
}

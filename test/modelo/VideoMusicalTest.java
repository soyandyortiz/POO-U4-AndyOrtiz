package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VideoMusicalTest {

    private VideoMusical video;

    @BeforeEach
    void preparar() {
        video = new VideoMusical(4, "Vivir mi vida", 5, "Salsa", "Marc Anthony", "3.0", 2013);
    }

    @Test
    void esViralDesdeUnMillonDeReproducciones() {
        video.registrarReproducciones(VideoMusical.UMBRAL_VIRAL - 1);
        assertFalse(video.esViral());
        video.registrarReproducciones(1);
        assertTrue(video.esViral());
        assertTrue(video.obtenerDetalles().contains("[VIRAL]"));
    }

    @Test
    void noAceptaReproduccionesNegativas() {
        assertThrows(IllegalArgumentException.class, () -> video.registrarReproducciones(-5));
        assertEquals(0, video.getReproducciones());
    }

    @Test
    void manejaParticipantesComoReparto() {
        Actor juan = new Actor(3, "Juan Pérez", "Ecuador", 2015);
        assertTrue(video.agregarActor(juan));
        assertEquals(1, video.getActores().size());
        assertTrue(video.quitarActor(juan));
    }

    @Test
    void validaElAnioDeLanzamiento() {
        assertThrows(IllegalArgumentException.class,
                () -> new VideoMusical(4, "X", 5, "Pop", "Artista", "Álbum", 1800));
        assertEquals("Marc Anthony", video.getArtista());
        assertEquals("3.0", video.getAlbum());
        assertEquals(2013, video.getAnioLanzamiento());
    }
}

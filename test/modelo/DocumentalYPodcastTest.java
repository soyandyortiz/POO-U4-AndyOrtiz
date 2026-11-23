package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class DocumentalYPodcastTest {

    private final Investigador sagan = new Investigador(1, "Carl Sagan", "Astronomía", "Cornell");

    @Test
    void documentalAgregaYQuitaAsesores() {
        Documental cosmos = new Documental(3, "Cosmos", 45, "Ciencia", "Astronomía");
        assertTrue(cosmos.agregarInvestigador(sagan));
        assertFalse(cosmos.agregarInvestigador(sagan));
        assertTrue(cosmos.obtenerDetalles().contains("Carl Sagan"));
        assertTrue(cosmos.quitarInvestigador(sagan));
        assertTrue(cosmos.getInvestigadores().isEmpty());
    }

    @Test
    void documentalValidaElTema() {
        Documental cosmos = new Documental(3, "Cosmos", 45, "Ciencia", "Astronomía");
        assertThrows(IllegalArgumentException.class, () -> cosmos.setTema(" "));
        cosmos.setTema("Física");
        assertEquals("Física", cosmos.getTema());
    }

    @Test
    void podcastSinInvitadosEsMonologo() {
        Podcast podcast = new Podcast(5, "El universo", 30, "Ciencia", "Andy Ortiz", "Spotify", 1);
        assertTrue(podcast.esMonologo());
        assertTrue(podcast.obtenerDetalles().contains("monólogo"));
        podcast.agregarInvestigador(sagan);
        assertFalse(podcast.esMonologo());
        assertTrue(podcast.obtenerDetalles().contains("Invitados (1)"));
        assertTrue(podcast.quitarInvestigador(sagan));
    }

    @Test
    void podcastValidaSusDatos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Podcast(5, "El universo", 30, "Ciencia", "", "Spotify", 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Podcast(5, "El universo", 30, "Ciencia", "Andy", "Spotify", 0));
        Podcast podcast = new Podcast(5, "El universo", 30, "Ciencia", "Andy", "Spotify", 3);
        assertEquals("Andy", podcast.getAnfitrion());
        assertEquals("Spotify", podcast.getPlataforma());
        assertEquals(3, podcast.getNumeroEpisodio());
    }
}

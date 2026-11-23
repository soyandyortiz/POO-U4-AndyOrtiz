package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SerieDeTVTest {

    private SerieDeTV serie;

    @BeforeEach
    void preparar() {
        serie = new SerieDeTV(2, "Game of Thrones", 60, "Fantasía");
    }

    @Test
    void numeraLasTemporadasAutomaticamente() {
        serie.agregarTemporada(2011, 10);
        Temporada segunda = serie.agregarTemporada(2012, 8);
        assertEquals(2, segunda.getNumero());
        assertEquals(2012, segunda.getAnioEstreno());
        assertEquals(2, serie.getNumeroTemporadas());
        assertEquals(18, serie.getTotalEpisodios());
    }

    @Test
    void sinTemporadasNoHayEpisodios() {
        assertEquals(0, serie.getTotalEpisodios());
        assertFalse(serie.eliminarUltimaTemporada());
    }

    @Test
    void eliminaLaUltimaTemporada() {
        serie.agregarTemporada(2011, 10);
        serie.agregarTemporada(2012, 10);
        assertTrue(serie.eliminarUltimaTemporada());
        assertEquals(1, serie.getNumeroTemporadas());
    }

    @Test
    void rechazaTemporadasInvalidas() {
        assertThrows(IllegalArgumentException.class, () -> serie.agregarTemporada(2011, 0));
        assertThrows(IllegalArgumentException.class, () -> serie.agregarTemporada(1800, 10));
        assertEquals(0, serie.getNumeroTemporadas());
    }

    @Test
    void lasTemporadasSoloLasCreaLaSerie() {
        serie.agregarTemporada(2011, 10);
        assertThrows(UnsupportedOperationException.class, () -> serie.getTemporadas().clear());
        assertTrue(serie.obtenerDetalles().contains("Temporada 1 (2011) - 10 episodios"));
    }
}

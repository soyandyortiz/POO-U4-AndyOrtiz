package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ContenidoAudiovisualTest {

    private Pelicula pelicula;

    @BeforeEach
    void crearPelicula() {
        pelicula = new Pelicula(1, "Avatar", 162, "Ciencia ficción", "20th Century Studios");
    }

    @Test
    @DisplayName("Sin votos el promedio es 0")
    void promedioSinVotos() {
        assertEquals(0.0, pelicula.getCalificacionPromedio());
        assertEquals(0, pelicula.getNumeroCalificaciones());
    }

    @Test
    @DisplayName("El promedio se calcula con todas las calificaciones")
    void promedioDeVariasCalificaciones() {
        pelicula.calificar(5);
        pelicula.calificar(4);
        pelicula.calificar(3);
        assertEquals(4.0, pelicula.getCalificacionPromedio(), 0.001);
        assertEquals(3, pelicula.getNumeroCalificaciones());
    }

    @ParameterizedTest(name = "{0} estrellas es inválido")
    @ValueSource(ints = {-1, 0, 6, 10})
    void rechazaEstrellasFueraDeRango(int estrellas) {
        assertThrows(IllegalArgumentException.class, () -> pelicula.calificar(estrellas));
    }

    @ParameterizedTest(name = "{0} estrellas es válido")
    @ValueSource(ints = {1, 5})
    void aceptaLosLimitesDelRango(int estrellas) {
        pelicula.calificar(estrellas);
        assertEquals(estrellas, pelicula.getCalificacionPromedio(), 0.001);
    }

    @Test
    void restauraCalificacionesGuardadas() {
        pelicula.restaurarCalificaciones(9, 2);
        assertEquals(4.5, pelicula.getCalificacionPromedio(), 0.001);
        assertEquals(9, pelicula.getSumaEstrellas());
    }

    @Test
    void rechazaCalificacionesGuardadasImposibles() {
        assertThrows(IllegalArgumentException.class, () -> pelicula.restaurarCalificaciones(11, 2));
        assertThrows(IllegalArgumentException.class, () -> pelicula.restaurarCalificaciones(1, 2));
        assertThrows(IllegalArgumentException.class, () -> pelicula.restaurarCalificaciones(0, -1));
    }

    @Test
    void validaLosDatosBasicos() {
        assertThrows(IllegalArgumentException.class, () -> new Pelicula(1, " ", 100, "Drama", "X"));
        assertThrows(IllegalArgumentException.class, () -> new Pelicula(1, "Título", 0, "Drama", "X"));
        assertThrows(IllegalArgumentException.class, () -> new Pelicula(0, "Título", 100, "Drama", "X"));
        assertThrows(IllegalArgumentException.class, () -> pelicula.setGenero(null));
    }

    @Test
    void permiteEditarDatosBasicos() {
        pelicula.setTitulo("  Avatar 2 ");
        pelicula.setDuracionEnMinutos(192);
        pelicula.setGenero("Aventura");
        assertEquals("Avatar 2", pelicula.getTitulo());
        assertEquals(192, pelicula.getDuracionEnMinutos());
        assertEquals("Aventura", pelicula.getGenero());
    }

    @Test
    void elResumenIncluyeIdTipoYTitulo() {
        String resumen = pelicula.toString();
        assertTrue(resumen.contains("[1]"));
        assertTrue(resumen.contains("Película"));
        assertTrue(resumen.contains("Avatar"));
    }
}

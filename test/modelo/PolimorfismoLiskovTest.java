package modelo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Principio de Liskov: cualquier subclase se puede usar donde se espera un
 * ContenidoAudiovisual y el comportamiento común se mantiene.
 */
class PolimorfismoLiskovTest {

    static Stream<ContenidoAudiovisual> todosLosTipos() {
        return Stream.of(
                new Pelicula(1, "Avatar", 162, "Ciencia ficción", "20th Century Studios"),
                new SerieDeTV(2, "Game of Thrones", 60, "Fantasía"),
                new Documental(3, "Cosmos", 45, "Ciencia", "Astronomía"),
                new VideoMusical(4, "Vivir mi vida", 5, "Salsa", "Marc Anthony", "3.0", 2013),
                new Podcast(5, "El universo", 30, "Ciencia", "Andy Ortiz", "Spotify", 1));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("todosLosTipos")
    @DisplayName("Cada subclase arma sus detalles con los datos comunes")
    void detallesConDatosComunes(ContenidoAudiovisual contenido) {
        String detalles = contenido.obtenerDetalles();
        assertTrue(detalles.startsWith("Detalles (" + contenido.getTipo() + ")"));
        assertTrue(detalles.contains(contenido.getTitulo()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("todosLosTipos")
    @DisplayName("Todas las subclases se pueden calificar igual que la superclase")
    void calificarFuncionaEnTodas(ContenidoAudiovisual contenido) {
        assertDoesNotThrow(() -> contenido.calificar(4));
        assertEquals(4.0, contenido.getCalificacionPromedio(), 0.001);
    }
}

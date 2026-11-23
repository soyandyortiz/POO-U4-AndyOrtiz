package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import modelo.excepciones.ElementoNoEncontradoException;

class CatalogoTest {

    private Catalogo catalogo;
    private Pelicula avatar;
    private Actor zoe;

    @BeforeEach
    void preparar() {
        catalogo = new Catalogo();
        avatar = new Pelicula(1, "Avatar", 162, "Ciencia ficción", "20th Century Studios");
        zoe = new Actor(1, "Zoe Saldaña", "Estados Unidos", 2000);
        catalogo.agregarContenido(avatar);
        catalogo.agregarActor(zoe);
        catalogo.agregarContenido(new SerieDeTV(2, "Game of Thrones", 60, "Fantasía"));
    }

    @Test
    void unCatalogoNuevoEstaVacioYEmpiezaEnElId1() {
        Catalogo vacio = new Catalogo();
        assertTrue(vacio.estaVacio());
        assertEquals(1, vacio.siguienteIdContenido());
        assertEquals(1, vacio.siguienteIdActor());
        assertEquals(1, vacio.siguienteIdInvestigador());
        assertTrue(vacio.getMejorCalificado().isEmpty());
    }

    @Test
    void elSiguienteIdEsElMayorMasUno() {
        catalogo.agregarContenido(new Documental(10, "Cosmos", 45, "Ciencia", "Astronomía"));
        assertEquals(11, catalogo.siguienteIdContenido());
        assertEquals(2, catalogo.siguienteIdActor());
    }

    @Test
    void noPermiteIdsRepetidos() {
        assertThrows(IllegalArgumentException.class,
                () -> catalogo.agregarContenido(new SerieDeTV(1, "Otra", 30, "Drama")));
        assertThrows(IllegalArgumentException.class,
                () -> catalogo.agregarActor(new Actor(1, "Otro", "Ecuador", 2000)));
    }

    @Test
    void buscaPorIdYLanzaExcepcionSiNoExiste() {
        assertSame(avatar, catalogo.buscarContenido(1));
        assertSame(zoe, catalogo.buscarActor(1));
        assertThrows(ElementoNoEncontradoException.class, () -> catalogo.buscarContenido(99));
        assertThrows(ElementoNoEncontradoException.class, () -> catalogo.buscarInvestigador(1));
    }

    @Test
    void buscaContenidoPorTipo() {
        assertSame(avatar, catalogo.buscarContenidoComo(1, ConReparto.class, "con reparto"));
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> catalogo.buscarContenidoComo(2, ConReparto.class, "una película"));
        assertTrue(error.getMessage().contains("no es una película"));
    }

    @Test
    void buscaPorTituloSinImportarMayusculas() {
        assertEquals(1, catalogo.buscarPorTitulo("AVA").size());
        assertEquals(2, catalogo.buscarPorTitulo("").size());
        assertEquals(2, catalogo.buscarPorTitulo(null).size());
        assertTrue(catalogo.buscarPorTitulo("zzz").isEmpty());
    }

    @Test
    void alEliminarUnContenidoLosActoresSiguenRegistrados() {
        avatar.agregarActor(zoe);
        assertSame(avatar, catalogo.eliminarContenido(1));
        assertEquals(1, catalogo.getContenidos().size());
        assertTrue(catalogo.getActores().contains(zoe));
        assertThrows(ElementoNoEncontradoException.class, () -> catalogo.eliminarContenido(1));
    }

    @Test
    void calculaElResumen() {
        avatar.calificar(5);
        assertEquals(1, catalogo.contarPorTipo().get("Película"));
        assertEquals(222, catalogo.getDuracionTotal());
        assertSame(avatar, catalogo.getMejorCalificado().orElseThrow());
        assertFalse(catalogo.estaVacio());
    }

    @Test
    void lasListasDelCatalogoSonDeSoloLectura() {
        assertThrows(UnsupportedOperationException.class, () -> catalogo.getContenidos().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalogo.getInvestigadores().clear());
    }
}

package controlador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import modelo.Actor;
import modelo.Catalogo;
import modelo.Documental;
import modelo.Investigador;
import modelo.Pelicula;
import modelo.Podcast;
import modelo.SerieDeTV;
import modelo.VideoMusical;

class CatalogoControllerTest {

    private static final String SALIR_SIN_GUARDAR_OPCION = "0";

    private VistaSimulada vista;
    private RepositorioEnMemoria repositorio;
    private CatalogoController controlador;

    @BeforeEach
    void preparar() {
        Catalogo inicial = new Catalogo();
        inicial.agregarContenido(new Pelicula(1, "Avatar", 162, "Ciencia ficción", "Fox"));
        inicial.agregarContenido(new SerieDeTV(2, "Game of Thrones", 60, "Fantasía"));
        inicial.agregarContenido(new Documental(3, "Cosmos", 45, "Ciencia", "Astronomía"));
        inicial.agregarContenido(new VideoMusical(4, "Vivir mi vida", 5, "Salsa", "Marc Anthony", "3.0", 2013));
        inicial.agregarActor(new Actor(1, "Zoe Saldaña", "Estados Unidos", 2000));
        inicial.agregarInvestigador(new Investigador(1, "Carl Sagan", "Astronomía", "Cornell"));
        vista = new VistaSimulada();
        repositorio = new RepositorioEnMemoria(inicial);
        controlador = new CatalogoController(vista, repositorio);
    }

    private void ejecutarYSalir(Object... respuestas) {
        vista.responder(respuestas).responder(SALIR_SIN_GUARDAR_OPCION, "n");
        controlador.iniciar();
    }

    @Test
    @DisplayName("Al iniciar carga los datos del repositorio")
    void cargaAlIniciar() {
        ejecutarYSalir();
        assertTrue(vista.mensajes.get(0).contains("4 contenidos"));
        assertEquals(0, repositorio.vecesGuardado);
    }

    @Test
    void sinDatosGuardadosEmpiezaVacio() {
        repositorio.guardado = null;
        ejecutarYSalir();
        assertTrue(controlador.getCatalogo().estaVacio());
    }

    @Test
    void siFallaLaCargaMuestraElError() {
        repositorio.fallar = true;
        ejecutarYSalir();
        assertTrue(vista.errores.get(0).contains("archivo dañado"));
    }

    @Test
    void registraUnaPeliculaConElSiguienteId() {
        ejecutarYSalir(3, 1, "Interestelar", 169, "Ciencia ficción", "Warner Bros");
        Pelicula nueva = assertInstanceOf(Pelicula.class, controlador.getCatalogo().buscarContenido(5));
        assertEquals("Warner Bros", nueva.getEstudio());
    }

    @Test
    void registraLosCincoTiposDeContenido() {
        ejecutarYSalir(
                3, 2, "Dark", 50, "Misterio",
                3, 3, "Planeta Tierra", 50, "Naturaleza", "Ecosistemas",
                3, 4, "Despacito", 4, "Reguetón", "Luis Fonsi", "Vida", 2017,
                3, 5, "Charla", 40, "Tecnología", "Andy", "YouTube", 2);
        Catalogo catalogo = controlador.getCatalogo();
        assertInstanceOf(SerieDeTV.class, catalogo.buscarContenido(5));
        assertInstanceOf(Documental.class, catalogo.buscarContenido(6));
        assertInstanceOf(VideoMusical.class, catalogo.buscarContenido(7));
        assertInstanceOf(Podcast.class, catalogo.buscarContenido(8));
    }

    @Test
    void registraPersonasYLasAsigna() {
        ejecutarYSalir(4, "Juan Pérez", "Ecuador", 2015, 6, 1, 2, 5, "María Torres", "Astrofísica", "EPN", 7, 3, 2);
        Pelicula avatar = (Pelicula) controlador.getCatalogo().buscarContenido(1);
        Documental cosmos = (Documental) controlador.getCatalogo().buscarContenido(3);
        assertEquals("Juan Pérez", avatar.getActores().get(0).getNombre());
        assertEquals("María Torres", cosmos.getInvestigadores().get(0).getNombre());
    }

    @Test
    void avisaSiElActorYaEstabaAsignado() {
        ejecutarYSalir(6, 1, 1, 6, 1, 1);
        assertTrue(vista.errores.get(0).contains("ya estaba asignado"));
    }

    @Test
    @DisplayName("Asignar un actor a un documental muestra un error y no se cae")
    void noAsignaActorAUnDocumental() {
        ejecutarYSalir(6, 3);
        assertTrue(vista.errores.get(0).contains("no es una película"));
    }

    @Test
    void unIdInexistenteMuestraError() {
        ejecutarYSalir(2, 99);
        assertTrue(vista.errores.get(0).contains("No existe contenido con ID 99"));
    }

    @Test
    void agregaTemporadasCalificaYRegistraReproducciones() {
        ejecutarYSalir(8, 2, 2011, 10, 9, 1, 5, 10, 4, 2_000_000);
        Catalogo catalogo = controlador.getCatalogo();
        assertEquals(1, ((SerieDeTV) catalogo.buscarContenido(2)).getNumeroTemporadas());
        assertEquals(5.0, catalogo.buscarContenido(1).getCalificacionPromedio(), 0.001);
        assertTrue(((VideoMusical) catalogo.buscarContenido(4)).esViral());
    }

    @Test
    void editaBuscaYEliminaContenido() {
        ejecutarYSalir(11, 1, "Avatar 2", 192, "Aventura", 12, "avatar", 13, 2);
        Catalogo catalogo = controlador.getCatalogo();
        assertEquals("Avatar 2", catalogo.buscarContenido(1).getTitulo());
        assertEquals(3, catalogo.getContenidos().size());
        assertTrue(vista.textos.stream().anyMatch(t -> t.contains("Avatar 2")));
    }

    @Test
    void muestraListadosDetalleYResumen() {
        ejecutarYSalir(1, 2, 1, 14, 15);
        assertTrue(vista.textos.stream().anyMatch(t -> t.startsWith("Catálogo")));
        assertTrue(vista.textos.stream().anyMatch(t -> t.startsWith("Detalles (Película)")));
        assertTrue(vista.textos.stream().anyMatch(t -> t.contains("Zoe Saldaña")));
        assertTrue(vista.textos.stream().anyMatch(t -> t.contains("Resumen del catálogo")));
    }

    @Test
    void guardaYVuelveACargarDesdeElMenu() {
        ejecutarYSalir(16, 17);
        assertEquals(1, repositorio.vecesGuardado);
        assertTrue(vista.mensajes.contains("Datos guardados en los archivos CSV."));
    }

    @Test
    void preguntaSiGuardarAlSalir() {
        vista.responder(0, "s");
        controlador.iniciar();
        assertEquals(1, repositorio.vecesGuardado);
    }

    @Test
    void siFallaElGuardadoMuestraElError() {
        ejecutarYSalir();
        repositorio.fallar = true;
        vista.responder(16, 0, "n");
        controlador.iniciar();
        assertFalse(vista.errores.isEmpty());
        assertTrue(vista.errores.stream().anyMatch(e -> e.contains("disco lleno")));
    }
}

package controlador;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.IntFunction;

import modelo.Actor;
import modelo.Catalogo;
import modelo.ConInvestigadores;
import modelo.ConReparto;
import modelo.ContenidoAudiovisual;
import modelo.Documental;
import modelo.Investigador;
import modelo.Pelicula;
import modelo.Podcast;
import modelo.SerieDeTV;
import modelo.Temporada;
import modelo.VideoMusical;
import modelo.excepciones.ElementoNoEncontradoException;
import vista.Vista;

/**
 * Coordina la vista con el modelo: pide los datos al usuario, llama al catálogo
 * y muestra el resultado. Recibe la vista por el constructor (inyección de dependencias).
 */
public class CatalogoController {

    private static final int OPCION_SALIR = 0;
    private static final int ANIO_ACTUAL = 2026;

    private final Vista vista;
    private final Map<Integer, OpcionMenu> menu = new LinkedHashMap<>();
    private final Map<Integer, TipoContenido> tiposDeContenido = new LinkedHashMap<>();
    private Catalogo catalogo = new Catalogo();

    private record OpcionMenu(String descripcion, Runnable accion) {
    }

    private record TipoContenido(String nombre, IntFunction<ContenidoAudiovisual> formulario) {
    }

    public CatalogoController(Vista vista) {
        this.vista = vista;
        registrarOpcionesDelMenu();
        registrarTiposDeContenido();
    }

    private void registrarOpcionesDelMenu() {
        menu.put(1, new OpcionMenu("Listar catálogo", this::listarCatalogo));
        menu.put(2, new OpcionMenu("Ver detalle de un contenido", this::verDetalle));
        menu.put(3, new OpcionMenu("Registrar contenido", this::registrarContenido));
        menu.put(4, new OpcionMenu("Registrar actor", this::registrarActor));
        menu.put(5, new OpcionMenu("Registrar investigador", this::registrarInvestigador));
        menu.put(6, new OpcionMenu("Asignar actor a película / video musical", this::asignarActor));
        menu.put(7, new OpcionMenu("Asignar investigador a documental / podcast", this::asignarInvestigador));
        menu.put(8, new OpcionMenu("Agregar temporada a una serie", this::agregarTemporada));
        menu.put(9, new OpcionMenu("Calificar contenido", this::calificar));
        menu.put(10, new OpcionMenu("Registrar reproducciones de un video musical", this::registrarReproducciones));
        menu.put(11, new OpcionMenu("Editar título, duración y género", this::editarContenido));
        menu.put(12, new OpcionMenu("Buscar por título", this::buscarPorTitulo));
        menu.put(13, new OpcionMenu("Eliminar contenido", this::eliminarContenido));
        menu.put(14, new OpcionMenu("Ver actores e investigadores", this::verPersonas));
        menu.put(15, new OpcionMenu("Resumen del catálogo", this::mostrarResumen));
        menu.put(OPCION_SALIR, new OpcionMenu("Salir", this::salir));
    }

    private void registrarTiposDeContenido() {
        tiposDeContenido.put(1, new TipoContenido("Película", this::formularioPelicula));
        tiposDeContenido.put(2, new TipoContenido("Serie de TV", this::formularioSerie));
        tiposDeContenido.put(3, new TipoContenido("Documental", this::formularioDocumental));
        tiposDeContenido.put(4, new TipoContenido("Video musical", this::formularioVideoMusical));
        tiposDeContenido.put(5, new TipoContenido("Podcast", this::formularioPodcast));
    }

    // ============================================================== ciclo principal

    public void iniciar() {
        int opcion;
        do {
            opcion = vista.elegirOpcion("SISTEMA DE CONTENIDO AUDIOVISUAL", descripcionesDelMenu());
            ejecutar(opcion);
        } while (opcion != OPCION_SALIR);
    }

    private Map<Integer, String> descripcionesDelMenu() {
        Map<Integer, String> descripciones = new LinkedHashMap<>();
        menu.forEach((numero, opcion) -> descripciones.put(numero, opcion.descripcion()));
        return descripciones;
    }

    void ejecutar(int opcion) {
        try {
            menu.get(opcion).accion().run();
        } catch (IllegalArgumentException | ElementoNoEncontradoException e) {
            vista.mostrarError(e.getMessage());
        }
    }

    Catalogo getCatalogo() {
        return catalogo;
    }

    private void salir() {
        vista.mostrarMensaje("Hasta pronto.");
    }

    // ============================================================== consultas

    private void listarCatalogo() {
        vista.mostrarLista("Catálogo", catalogo.getContenidos());
    }

    private void verDetalle() {
        vista.mostrarTexto(catalogo.buscarContenido(pedirId("ID del contenido")).obtenerDetalles());
    }

    private void buscarPorTitulo() {
        vista.mostrarLista("Resultados", catalogo.buscarPorTitulo(vista.pedirTexto("Texto a buscar")));
    }

    private void verPersonas() {
        vista.mostrarLista("Actores", catalogo.getActores());
        vista.mostrarLista("Investigadores", catalogo.getInvestigadores());
    }

    private void mostrarResumen() {
        StringBuilder resumen = new StringBuilder("---- Resumen del catálogo ----\n");
        catalogo.contarPorTipo().forEach((tipo, cantidad) -> resumen.append(String.format("%-14s: %d%n", tipo, cantidad)));
        resumen.append("Total de contenidos : ").append(catalogo.getContenidos().size()).append("\n");
        resumen.append("Duración total (min): ").append(catalogo.getDuracionTotal()).append("\n");
        resumen.append("Mejor calificado    : ").append(catalogo.getMejorCalificado()
                .map(ContenidoAudiovisual::toString).orElse("sin calificaciones"));
        vista.mostrarTexto(resumen.toString());
    }

    // ============================================================== registro de contenido

    private void registrarContenido() {
        Map<Integer, String> nombres = new LinkedHashMap<>();
        tiposDeContenido.forEach((numero, tipo) -> nombres.put(numero, tipo.nombre()));
        TipoContenido tipo = tiposDeContenido.get(vista.elegirOpcion("Tipo de contenido", nombres));
        ContenidoAudiovisual nuevo = tipo.formulario().apply(catalogo.siguienteIdContenido());
        catalogo.agregarContenido(nuevo);
        vista.mostrarMensaje("Registrado: " + nuevo);
    }

    private Pelicula formularioPelicula(int id) {
        DatosBasicos d = pedirDatosBasicos();
        return new Pelicula(id, d.titulo(), d.duracion(), d.genero(), vista.pedirTexto("Estudio"));
    }

    private SerieDeTV formularioSerie(int id) {
        DatosBasicos d = pedirDatosBasicos();
        return new SerieDeTV(id, d.titulo(), d.duracion(), d.genero());
    }

    private Documental formularioDocumental(int id) {
        DatosBasicos d = pedirDatosBasicos();
        return new Documental(id, d.titulo(), d.duracion(), d.genero(), vista.pedirTexto("Tema"));
    }

    private VideoMusical formularioVideoMusical(int id) {
        DatosBasicos d = pedirDatosBasicos();
        return new VideoMusical(id, d.titulo(), d.duracion(), d.genero(), vista.pedirTexto("Artista"),
                vista.pedirTexto("Álbum"), vista.pedirEntero("Año de lanzamiento", 1900, ANIO_ACTUAL));
    }

    private Podcast formularioPodcast(int id) {
        DatosBasicos d = pedirDatosBasicos();
        return new Podcast(id, d.titulo(), d.duracion(), d.genero(), vista.pedirTexto("Anfitrión"),
                vista.pedirTexto("Plataforma"), vista.pedirEntero("Número de episodio", 1, 9999));
    }

    private record DatosBasicos(String titulo, int duracion, String genero) {
    }

    private DatosBasicos pedirDatosBasicos() {
        return new DatosBasicos(vista.pedirTexto("Título"), vista.pedirEntero("Duración en minutos", 1, 10000),
                vista.pedirTexto("Género"));
    }

    // ============================================================== personas y relaciones

    private void registrarActor() {
        Actor actor = new Actor(catalogo.siguienteIdActor(), vista.pedirTexto("Nombre"),
                vista.pedirTexto("Nacionalidad"), vista.pedirEntero("Año de debut", Actor.ANIO_MINIMO, ANIO_ACTUAL));
        catalogo.agregarActor(actor);
        vista.mostrarMensaje("Actor registrado: " + actor);
    }

    private void registrarInvestigador() {
        Investigador investigador = new Investigador(catalogo.siguienteIdInvestigador(), vista.pedirTexto("Nombre"),
                vista.pedirTexto("Especialidad"), vista.pedirTexto("Institución"));
        catalogo.agregarInvestigador(investigador);
        vista.mostrarMensaje("Investigador registrado: " + investigador);
    }

    private void asignarActor() {
        ConReparto contenido = catalogo.buscarContenidoComo(pedirId("ID de la película o video musical"),
                ConReparto.class, "una película ni un video musical");
        vista.mostrarLista("Actores", catalogo.getActores());
        Actor actor = catalogo.buscarActor(pedirId("ID del actor"));
        informarAsignacion(contenido.agregarActor(actor), actor.getNombre());
    }

    private void asignarInvestigador() {
        ConInvestigadores contenido = catalogo.buscarContenidoComo(pedirId("ID del documental o podcast"),
                ConInvestigadores.class, "un documental ni un podcast");
        vista.mostrarLista("Investigadores", catalogo.getInvestigadores());
        Investigador investigador = catalogo.buscarInvestigador(pedirId("ID del investigador"));
        informarAsignacion(contenido.agregarInvestigador(investigador), investigador.getNombre());
    }

    private void informarAsignacion(boolean asignado, String nombre) {
        if (asignado) {
            vista.mostrarMensaje(nombre + " fue asignado correctamente.");
        } else {
            vista.mostrarError(nombre + " ya estaba asignado a ese contenido.");
        }
    }

    private void agregarTemporada() {
        SerieDeTV serie = catalogo.buscarContenidoComo(pedirId("ID de la serie"), SerieDeTV.class, "una serie de TV");
        Temporada temporada = serie.agregarTemporada(vista.pedirEntero("Año de estreno", SerieDeTV.ANIO_MINIMO,
                ANIO_ACTUAL), vista.pedirEntero("Número de episodios", 1, 500));
        vista.mostrarMensaje("Creada " + temporada);
    }

    // ============================================================== modificaciones

    private void calificar() {
        ContenidoAudiovisual contenido = catalogo.buscarContenido(pedirId("ID del contenido"));
        contenido.calificar(vista.pedirEntero("Estrellas", ContenidoAudiovisual.MIN_ESTRELLAS,
                ContenidoAudiovisual.MAX_ESTRELLAS));
        vista.mostrarMensaje(String.format("Nuevo promedio de %s: %.1f", contenido.getTitulo(),
                contenido.getCalificacionPromedio()));
    }

    private void registrarReproducciones() {
        VideoMusical video = catalogo.buscarContenidoComo(pedirId("ID del video musical"), VideoMusical.class,
                "un video musical");
        video.registrarReproducciones(vista.pedirEntero("Reproducciones a sumar", 0, Integer.MAX_VALUE));
        vista.mostrarMensaje("Reproducciones de " + video.getTitulo() + ": " + video.getReproducciones()
                + (video.esViral() ? " (viral)" : ""));
    }

    private void editarContenido() {
        ContenidoAudiovisual contenido = catalogo.buscarContenido(pedirId("ID del contenido"));
        DatosBasicos nuevos = pedirDatosBasicos();
        contenido.setTitulo(nuevos.titulo());
        contenido.setDuracionEnMinutos(nuevos.duracion());
        contenido.setGenero(nuevos.genero());
        vista.mostrarMensaje("Actualizado: " + contenido);
    }

    private void eliminarContenido() {
        ContenidoAudiovisual eliminado = catalogo.eliminarContenido(pedirId("ID del contenido a eliminar"));
        vista.mostrarMensaje("Eliminado: " + eliminado.getTitulo()
                + ". Los actores e investigadores siguen registrados.");
    }

    private int pedirId(String etiqueta) {
        return vista.pedirEntero(etiqueta, 1, Integer.MAX_VALUE);
    }
}

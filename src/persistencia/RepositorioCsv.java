package persistencia;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import modelo.Actor;
import modelo.Catalogo;
import modelo.ConInvestigadores;
import modelo.ConReparto;
import modelo.ContenidoAudiovisual;
import modelo.Investigador;
import modelo.SerieDeTV;
import modelo.Temporada;
import modelo.excepciones.ElementoNoEncontradoException;

/**
 * Guarda y carga el catálogo completo en una carpeta con seis archivos CSV:
 * contenidos, actores, investigadores, temporadas, reparto y asesores.
 */
public class RepositorioCsv implements RepositorioCatalogo {

    static final String CONTENIDOS = "contenidos.csv";
    static final String ACTORES = "actores.csv";
    static final String INVESTIGADORES = "investigadores.csv";
    static final String TEMPORADAS = "temporadas.csv";
    static final String REPARTO = "reparto.csv";
    static final String ASESORES = "asesores.csv";

    private static final List<String> ENCABEZADO_CONTENIDOS = List.of("id", "tipo", "titulo", "duracion",
            "genero", "suma_estrellas", "num_calificaciones", "dato1", "dato2", "dato3", "dato4");
    private static final int PRIMERA_COLUMNA_PROPIA = 7;

    private final Path carpeta;
    private final Map<String, MapeadorContenido<?>> mapeadoresPorTipo = new LinkedHashMap<>();

    public RepositorioCsv(Path carpeta, List<MapeadorContenido<?>> mapeadores) {
        this.carpeta = carpeta;
        for (MapeadorContenido<?> mapeador : mapeadores) {
            mapeadoresPorTipo.put(mapeador.getTipo(), mapeador);
        }
    }

    public Path getCarpeta() {
        return carpeta;
    }

    @Override
    public boolean existenDatos() {
        return Files.exists(carpeta.resolve(CONTENIDOS));
    }

    // ================================================================ lectura

    @Override
    public Catalogo cargar() throws PersistenciaException {
        Catalogo catalogo = new Catalogo();
        cargarActores(catalogo);
        cargarInvestigadores(catalogo);
        cargarContenidos(catalogo);
        cargarTemporadas(catalogo);
        cargarReparto(catalogo);
        cargarAsesores(catalogo);
        return catalogo;
    }

    private void cargarActores(Catalogo catalogo) throws PersistenciaException {
        for (FilaCsv fila : leer(ACTORES)) {
            crearEnCatalogo(fila, () -> catalogo.agregarActor(new Actor(fila.entero(0, "id"),
                    fila.texto(1, "nombre"), fila.texto(2, "nacionalidad"), fila.entero(3, "anio_debut"))));
        }
    }

    private void cargarInvestigadores(Catalogo catalogo) throws PersistenciaException {
        for (FilaCsv fila : leer(INVESTIGADORES)) {
            crearEnCatalogo(fila, () -> catalogo.agregarInvestigador(new Investigador(fila.entero(0, "id"),
                    fila.texto(1, "nombre"), fila.texto(2, "especialidad"), fila.texto(3, "institucion"))));
        }
    }

    private void cargarContenidos(Catalogo catalogo) throws PersistenciaException {
        for (FilaCsv fila : leer(CONTENIDOS)) {
            MapeadorContenido<?> mapeador = mapeadorDeTipo(fila, fila.texto(1, "tipo"));
            DatosComunes datos = new DatosComunes(fila.entero(0, "id"), fila.texto(2, "titulo"),
                    fila.entero(3, "duracion"), fila.texto(4, "genero"));
            crearEnCatalogo(fila, () -> {
                ContenidoAudiovisual contenido = mapeador.crear(datos, fila, PRIMERA_COLUMNA_PROPIA);
                contenido.restaurarCalificaciones(fila.entero(5, "suma_estrellas"),
                        fila.entero(6, "num_calificaciones"));
                catalogo.agregarContenido(contenido);
            });
        }
    }

    private void cargarTemporadas(Catalogo catalogo) throws PersistenciaException {
        List<FilaCsv> filas = new ArrayList<>(leer(TEMPORADAS));
        for (FilaCsv fila : ordenarPorNumeroDeTemporada(filas)) {
            crearEnCatalogo(fila, () -> {
                SerieDeTV serie = catalogo.buscarContenidoComo(fila.entero(0, "id_serie"), SerieDeTV.class,
                        "una serie de TV");
                int numero = fila.entero(1, "numero");
                if (numero != serie.getNumeroTemporadas() + 1) {
                    throw fila.error("la temporada " + numero + " de la serie " + serie.getId()
                            + " no es consecutiva");
                }
                serie.agregarTemporada(fila.entero(2, "anio"), fila.entero(3, "episodios"));
            });
        }
    }

    private static List<FilaCsv> ordenarPorNumeroDeTemporada(List<FilaCsv> filas) throws FormatoCsvException {
        Map<FilaCsv, Integer> numeros = new LinkedHashMap<>();
        for (FilaCsv fila : filas) {
            numeros.put(fila, fila.entero(1, "numero"));
        }
        filas.sort(Comparator.comparing(numeros::get));
        return filas;
    }

    private void cargarReparto(Catalogo catalogo) throws PersistenciaException {
        for (FilaCsv fila : leer(REPARTO)) {
            crearEnCatalogo(fila, () -> catalogo.buscarContenidoComo(fila.entero(0, "id_contenido"),
                    ConReparto.class, "una película o video musical")
                    .agregarActor(catalogo.buscarActor(fila.entero(1, "id_actor"))));
        }
    }

    private void cargarAsesores(Catalogo catalogo) throws PersistenciaException {
        for (FilaCsv fila : leer(ASESORES)) {
            crearEnCatalogo(fila, () -> catalogo.buscarContenidoComo(fila.entero(0, "id_contenido"),
                    ConInvestigadores.class, "un documental o podcast")
                    .agregarInvestigador(catalogo.buscarInvestigador(fila.entero(1, "id_investigador"))));
        }
    }

    private List<FilaCsv> leer(String archivo) throws PersistenciaException {
        return ArchivoCsv.leer(carpeta.resolve(archivo));
    }

    private MapeadorContenido<?> mapeadorDeTipo(FilaCsv fila, String tipo) throws FormatoCsvException {
        MapeadorContenido<?> mapeador = mapeadoresPorTipo.get(tipo.toUpperCase());
        if (mapeador == null) {
            throw fila.error("tipo de contenido desconocido '" + tipo + "'");
        }
        return mapeador;
    }

    /** Acción de carga que puede fallar por formato o por reglas del modelo. */
    @FunctionalInterface
    private interface AccionDeCarga {
        void ejecutar() throws FormatoCsvException;
    }

    /** Convierte los errores del modelo (datos inválidos, IDs inexistentes) en errores con línea. */
    private static void crearEnCatalogo(FilaCsv fila, AccionDeCarga accion) throws FormatoCsvException {
        try {
            accion.ejecutar();
        } catch (IllegalArgumentException | ElementoNoEncontradoException e) {
            throw fila.error(e.getMessage());
        }
    }

    // ================================================================ escritura

    @Override
    public void guardar(Catalogo catalogo) throws PersistenciaException {
        escribir(ACTORES, List.of("id", "nombre", "nacionalidad", "anio_debut"), filasDeActores(catalogo));
        escribir(INVESTIGADORES, List.of("id", "nombre", "especialidad", "institucion"),
                filasDeInvestigadores(catalogo));
        escribir(CONTENIDOS, ENCABEZADO_CONTENIDOS, filasDeContenidos(catalogo));
        escribir(TEMPORADAS, List.of("id_serie", "numero", "anio", "episodios"), filasDeTemporadas(catalogo));
        escribir(REPARTO, List.of("id_contenido", "id_actor"), filasDeReparto(catalogo));
        escribir(ASESORES, List.of("id_contenido", "id_investigador"), filasDeAsesores(catalogo));
    }

    private void escribir(String archivo, List<String> encabezado, List<List<String>> filas)
            throws PersistenciaException {
        ArchivoCsv.escribir(carpeta.resolve(archivo), encabezado, filas);
    }

    private static List<List<String>> filasDeActores(Catalogo catalogo) {
        return catalogo.getActores().stream()
                .map(a -> List.of(texto(a.getId()), a.getNombre(), a.getNacionalidad(), texto(a.getAnioDebut())))
                .toList();
    }

    private static List<List<String>> filasDeInvestigadores(Catalogo catalogo) {
        return catalogo.getInvestigadores().stream()
                .map(i -> List.of(texto(i.getId()), i.getNombre(), i.getEspecialidad(), i.getInstitucion()))
                .toList();
    }

    private List<List<String>> filasDeContenidos(Catalogo catalogo) throws PersistenciaException {
        List<List<String>> filas = new ArrayList<>();
        for (ContenidoAudiovisual contenido : catalogo.getContenidos()) {
            MapeadorContenido<?> mapeador = mapeadorDeClase(contenido);
            List<String> fila = new ArrayList<>(List.of(texto(contenido.getId()), mapeador.getTipo(),
                    contenido.getTitulo(), texto(contenido.getDuracionEnMinutos()), contenido.getGenero(),
                    texto(contenido.getSumaEstrellas()), texto(contenido.getNumeroCalificaciones())));
            fila.addAll(columnasPropias(mapeador, contenido));
            filas.add(fila);
        }
        return filas;
    }

    private MapeadorContenido<?> mapeadorDeClase(ContenidoAudiovisual contenido) throws PersistenciaException {
        for (MapeadorContenido<?> mapeador : mapeadoresPorTipo.values()) {
            if (mapeador.getClase() == contenido.getClass()) {
                return mapeador;
            }
        }
        throw new PersistenciaException("No hay un mapeador CSV para " + contenido.getClass().getSimpleName());
    }

    private static <T extends ContenidoAudiovisual> List<String> columnasPropias(MapeadorContenido<T> mapeador,
                                                                                ContenidoAudiovisual contenido) {
        return mapeador.columnasPropias(mapeador.getClase().cast(contenido));
    }

    private static List<List<String>> filasDeTemporadas(Catalogo catalogo) {
        List<List<String>> filas = new ArrayList<>();
        for (ContenidoAudiovisual contenido : catalogo.getContenidos()) {
            if (contenido instanceof SerieDeTV serie) {
                for (Temporada t : serie.getTemporadas()) {
                    filas.add(List.of(texto(serie.getId()), texto(t.getNumero()), texto(t.getAnioEstreno()),
                            texto(t.getNumeroEpisodios())));
                }
            }
        }
        return filas;
    }

    private static List<List<String>> filasDeReparto(Catalogo catalogo) {
        List<List<String>> filas = new ArrayList<>();
        for (ContenidoAudiovisual contenido : catalogo.getContenidos()) {
            if (contenido instanceof ConReparto conReparto) {
                conReparto.getActores().forEach(a -> filas.add(List.of(texto(contenido.getId()), texto(a.getId()))));
            }
        }
        return filas;
    }

    private static List<List<String>> filasDeAsesores(Catalogo catalogo) {
        List<List<String>> filas = new ArrayList<>();
        for (ContenidoAudiovisual contenido : catalogo.getContenidos()) {
            if (contenido instanceof ConInvestigadores conInvestigadores) {
                conInvestigadores.getInvestigadores()
                        .forEach(i -> filas.add(List.of(texto(contenido.getId()), texto(i.getId()))));
            }
        }
        return filas;
    }

    private static String texto(long numero) {
        return String.valueOf(numero);
    }
}

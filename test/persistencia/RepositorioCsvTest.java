package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import modelo.Actor;
import modelo.Catalogo;
import modelo.Documental;
import modelo.Investigador;
import modelo.Pelicula;
import modelo.Podcast;
import modelo.SerieDeTV;
import modelo.VideoMusical;

class RepositorioCsvTest {

    @TempDir
    Path carpeta;

    private RepositorioCsv repositorio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioCsv(carpeta, Mapeadores.porDefecto());
    }

    private static Catalogo catalogoDeEjemplo() {
        Catalogo catalogo = new Catalogo();
        Actor zoe = new Actor(1, "Zoe Saldaña", "Estados Unidos", 2000);
        Actor juan = new Actor(2, "Juan Pérez", "Ecuador", 2015);
        Investigador sagan = new Investigador(1, "Carl Sagan", "Astronomía", "Cornell");
        catalogo.agregarActor(zoe);
        catalogo.agregarActor(juan);
        catalogo.agregarInvestigador(sagan);

        Pelicula avatar = new Pelicula(1, "Avatar", 162, "Ciencia ficción", "Fox, Lightstorm");
        avatar.agregarActor(zoe);
        avatar.calificar(5);
        avatar.calificar(4);
        SerieDeTV got = new SerieDeTV(2, "Game of Thrones", 60, "Fantasía");
        got.agregarTemporada(2011, 10);
        got.agregarTemporada(2012, 8);
        Documental cosmos = new Documental(3, "Cosmos", 45, "Ciencia", "Astronomía");
        cosmos.agregarInvestigador(sagan);
        VideoMusical video = new VideoMusical(4, "Vivir mi vida", 5, "Salsa", "Marc Anthony", "3.0", 2013);
        video.registrarReproducciones(1_500_000);
        video.agregarActor(juan);
        Podcast podcast = new Podcast(5, "El universo", 30, "Ciencia", "Andy Ortiz", "Spotify", 1);
        podcast.agregarInvestigador(sagan);

        catalogo.agregarContenido(avatar);
        catalogo.agregarContenido(got);
        catalogo.agregarContenido(cosmos);
        catalogo.agregarContenido(video);
        catalogo.agregarContenido(podcast);
        return catalogo;
    }

    @Test
    @DisplayName("Sin archivos no hay datos y cargar devuelve un catálogo vacío")
    void carpetaVacia() throws PersistenciaException {
        assertFalse(repositorio.existenDatos());
        assertTrue(repositorio.cargar().estaVacio());
    }

    @Test
    @DisplayName("Guardar crea los seis archivos CSV")
    void guardarCreaLosArchivos() throws PersistenciaException {
        repositorio.guardar(catalogoDeEjemplo());
        assertTrue(repositorio.existenDatos());
        for (String archivo : new String[] {RepositorioCsv.CONTENIDOS, RepositorioCsv.ACTORES,
                RepositorioCsv.INVESTIGADORES, RepositorioCsv.TEMPORADAS, RepositorioCsv.REPARTO,
                RepositorioCsv.ASESORES}) {
            assertTrue(Files.exists(carpeta.resolve(archivo)), archivo);
        }
        assertEquals(carpeta, repositorio.getCarpeta());
    }

    @Test
    @DisplayName("Lo que se guarda se recupera igual al cargar")
    void guardarYCargarConservaTodo() throws PersistenciaException {
        repositorio.guardar(catalogoDeEjemplo());
        Catalogo cargado = repositorio.cargar();

        assertEquals(5, cargado.getContenidos().size());
        assertEquals(2, cargado.getActores().size());
        assertEquals(1, cargado.getInvestigadores().size());

        Pelicula avatar = assertInstanceOf(Pelicula.class, cargado.buscarContenido(1));
        assertEquals("Fox, Lightstorm", avatar.getEstudio());
        assertEquals(4.5, avatar.getCalificacionPromedio(), 0.001);
        assertEquals("Zoe Saldaña", avatar.getActores().get(0).getNombre());

        SerieDeTV got = assertInstanceOf(SerieDeTV.class, cargado.buscarContenido(2));
        assertEquals(2, got.getNumeroTemporadas());
        assertEquals(18, got.getTotalEpisodios());

        Documental cosmos = assertInstanceOf(Documental.class, cargado.buscarContenido(3));
        assertEquals("Carl Sagan", cosmos.getInvestigadores().get(0).getNombre());

        VideoMusical video = assertInstanceOf(VideoMusical.class, cargado.buscarContenido(4));
        assertTrue(video.esViral());
        assertEquals(1, video.getActores().size());

        Podcast podcast = assertInstanceOf(Podcast.class, cargado.buscarContenido(5));
        assertFalse(podcast.esMonologo());
    }

    @Test
    void volverAGuardarReemplazaLosDatos() throws PersistenciaException {
        repositorio.guardar(catalogoDeEjemplo());
        Catalogo cargado = repositorio.cargar();
        cargado.eliminarContenido(1);
        repositorio.guardar(cargado);
        assertEquals(4, repositorio.cargar().getContenidos().size());
    }

    private void escribir(String archivo, String... lineas) throws IOException {
        Files.write(carpeta.resolve(archivo), java.util.List.of(lineas), StandardCharsets.UTF_8);
    }

    private static final String ENCABEZADO =
            "id,tipo,titulo,duracion,genero,suma_estrellas,num_calificaciones,dato1,dato2,dato3,dato4";

    @Test
    @DisplayName("Un número inválido indica el archivo y la línea")
    void numeroInvalidoIndicaLaLinea() throws IOException {
        escribir(RepositorioCsv.CONTENIDOS, ENCABEZADO,
                "1,PELICULA,Avatar,162,Ciencia ficción,0,0,Fox",
                "2,PELICULA,Titanic,abc,Drama,0,0,Paramount");
        FormatoCsvException error = assertThrows(FormatoCsvException.class, () -> repositorio.cargar());
        assertEquals(RepositorioCsv.CONTENIDOS, error.getArchivo());
        assertEquals(3, error.getLinea());
        assertTrue(error.getMessage().contains("abc"));
    }

    @Test
    void tipoDesconocido() throws IOException {
        escribir(RepositorioCsv.CONTENIDOS, ENCABEZADO, "1,NOVELA,Algo,10,Drama,0,0");
        FormatoCsvException error = assertThrows(FormatoCsvException.class, () -> repositorio.cargar());
        assertTrue(error.getMessage().contains("desconocido"));
    }

    @Test
    void faltaUnaColumnaObligatoria() throws IOException {
        escribir(RepositorioCsv.CONTENIDOS, ENCABEZADO, "1,DOCUMENTAL,Cosmos,45,Ciencia,0,0");
        FormatoCsvException error = assertThrows(FormatoCsvException.class, () -> repositorio.cargar());
        assertTrue(error.getMessage().contains("tema"));
    }

    @Test
    void datosQueRompenLasReglasDelModelo() throws IOException {
        escribir(RepositorioCsv.CONTENIDOS, ENCABEZADO, "1,PELICULA,Avatar,-5,Ciencia ficción,0,0,Fox");
        FormatoCsvException error = assertThrows(FormatoCsvException.class, () -> repositorio.cargar());
        assertTrue(error.getMessage().contains("duración"));
    }

    @Test
    void relacionHaciaUnIdInexistente() throws IOException {
        escribir(RepositorioCsv.CONTENIDOS, ENCABEZADO, "1,PELICULA,Avatar,162,Ciencia ficción,0,0,Fox");
        escribir(RepositorioCsv.REPARTO, "id_contenido,id_actor", "1,99");
        FormatoCsvException error = assertThrows(FormatoCsvException.class, () -> repositorio.cargar());
        assertEquals(RepositorioCsv.REPARTO, error.getArchivo());
        assertTrue(error.getMessage().contains("99"));
    }

    @Test
    void relacionConUnTipoQueNoLaAdmite() throws IOException {
        escribir(RepositorioCsv.CONTENIDOS, ENCABEZADO, "1,SERIE,GoT,60,Fantasía,0,0");
        escribir(RepositorioCsv.INVESTIGADORES, "id,nombre,especialidad,institucion", "1,Carl Sagan,Astronomía,Cornell");
        escribir(RepositorioCsv.ASESORES, "id_contenido,id_investigador", "1,1");
        assertThrows(FormatoCsvException.class, () -> repositorio.cargar());
    }

    @Test
    void temporadasQueNoSonConsecutivas() throws IOException {
        escribir(RepositorioCsv.CONTENIDOS, ENCABEZADO, "1,SERIE,GoT,60,Fantasía,0,0");
        escribir(RepositorioCsv.TEMPORADAS, "id_serie,numero,anio,episodios", "1,1,2011,10", "1,3,2013,10");
        FormatoCsvException error = assertThrows(FormatoCsvException.class, () -> repositorio.cargar());
        assertTrue(error.getMessage().contains("consecutiva"));
    }

    @Test
    void temporadasDesordenadasSeCarganEnOrden() throws IOException, PersistenciaException {
        escribir(RepositorioCsv.CONTENIDOS, ENCABEZADO, "1,SERIE,GoT,60,Fantasía,0,0");
        escribir(RepositorioCsv.TEMPORADAS, "id_serie,numero,anio,episodios", "1,2,2012,8", "1,1,2011,10", "");
        SerieDeTV serie = (SerieDeTV) repositorio.cargar().buscarContenido(1);
        assertEquals(2011, serie.getTemporadas().get(0).getAnioEstreno());
    }

    @Test
    void comillasSinCerrarEnElArchivo() throws IOException {
        escribir(RepositorioCsv.ACTORES, "id,nombre,nacionalidad,anio_debut", "1,\"Zoe,EEUU,2000");
        FormatoCsvException error = assertThrows(FormatoCsvException.class, () -> repositorio.cargar());
        assertEquals(2, error.getLinea());
    }

    @Test
    void noSePuedeGuardarSiLaCarpetaEsUnArchivo() throws IOException {
        Path archivo = Files.createFile(carpeta.resolve("no-es-carpeta"));
        RepositorioCsv invalido = new RepositorioCsv(archivo, Mapeadores.porDefecto());
        assertThrows(PersistenciaException.class, () -> invalido.guardar(catalogoDeEjemplo()));
    }

    @Test
    void contenidoSinMapeadorNoSePuedeGuardar() {
        RepositorioCsv sinMapeadores = new RepositorioCsv(carpeta, java.util.List.of());
        PersistenciaException error = assertThrows(PersistenciaException.class,
                () -> sinMapeadores.guardar(catalogoDeEjemplo()));
        assertTrue(error.getMessage().contains("mapeador"));
    }
}

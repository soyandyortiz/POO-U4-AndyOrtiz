package persistencia;

import java.util.List;

import modelo.Documental;
import modelo.Pelicula;
import modelo.Podcast;
import modelo.SerieDeTV;
import modelo.VideoMusical;

/** Mapeadores CSV de los cinco tipos de contenido del sistema. */
public final class Mapeadores {

    private Mapeadores() {
    }

    public static List<MapeadorContenido<?>> porDefecto() {
        return List.of(new MapeadorPelicula(), new MapeadorSerie(), new MapeadorDocumental(),
                new MapeadorVideoMusical(), new MapeadorPodcast());
    }

    static final class MapeadorPelicula implements MapeadorContenido<Pelicula> {
        @Override
        public String getTipo() {
            return "PELICULA";
        }

        @Override
        public Class<Pelicula> getClase() {
            return Pelicula.class;
        }

        @Override
        public List<String> columnasPropias(Pelicula p) {
            return List.of(p.getEstudio());
        }

        @Override
        public Pelicula crear(DatosComunes d, FilaCsv fila, int col) throws FormatoCsvException {
            return new Pelicula(d.id(), d.titulo(), d.duracion(), d.genero(), fila.texto(col, "estudio"));
        }
    }

    static final class MapeadorSerie implements MapeadorContenido<SerieDeTV> {
        @Override
        public String getTipo() {
            return "SERIE";
        }

        @Override
        public Class<SerieDeTV> getClase() {
            return SerieDeTV.class;
        }

        @Override
        public List<String> columnasPropias(SerieDeTV s) {
            return List.of(); // sus temporadas van en temporadas.csv
        }

        @Override
        public SerieDeTV crear(DatosComunes d, FilaCsv fila, int col) {
            return new SerieDeTV(d.id(), d.titulo(), d.duracion(), d.genero());
        }
    }

    static final class MapeadorDocumental implements MapeadorContenido<Documental> {
        @Override
        public String getTipo() {
            return "DOCUMENTAL";
        }

        @Override
        public Class<Documental> getClase() {
            return Documental.class;
        }

        @Override
        public List<String> columnasPropias(Documental doc) {
            return List.of(doc.getTema());
        }

        @Override
        public Documental crear(DatosComunes d, FilaCsv fila, int col) throws FormatoCsvException {
            return new Documental(d.id(), d.titulo(), d.duracion(), d.genero(), fila.texto(col, "tema"));
        }
    }

    static final class MapeadorVideoMusical implements MapeadorContenido<VideoMusical> {
        @Override
        public String getTipo() {
            return "VIDEO_MUSICAL";
        }

        @Override
        public Class<VideoMusical> getClase() {
            return VideoMusical.class;
        }

        @Override
        public List<String> columnasPropias(VideoMusical v) {
            return List.of(v.getArtista(), v.getAlbum(), String.valueOf(v.getAnioLanzamiento()),
                    String.valueOf(v.getReproducciones()));
        }

        @Override
        public VideoMusical crear(DatosComunes d, FilaCsv fila, int col) throws FormatoCsvException {
            VideoMusical video = new VideoMusical(d.id(), d.titulo(), d.duracion(), d.genero(),
                    fila.texto(col, "artista"), fila.texto(col + 1, "álbum"), fila.entero(col + 2, "año"));
            video.registrarReproducciones(fila.enteroLargo(col + 3, "reproducciones"));
            return video;
        }
    }

    static final class MapeadorPodcast implements MapeadorContenido<Podcast> {
        @Override
        public String getTipo() {
            return "PODCAST";
        }

        @Override
        public Class<Podcast> getClase() {
            return Podcast.class;
        }

        @Override
        public List<String> columnasPropias(Podcast p) {
            return List.of(p.getAnfitrion(), p.getPlataforma(), String.valueOf(p.getNumeroEpisodio()));
        }

        @Override
        public Podcast crear(DatosComunes d, FilaCsv fila, int col) throws FormatoCsvException {
            return new Podcast(d.id(), d.titulo(), d.duracion(), d.genero(),
                    fila.texto(col, "anfitrión"), fila.texto(col + 1, "plataforma"), fila.entero(col + 2, "episodio"));
        }
    }
}

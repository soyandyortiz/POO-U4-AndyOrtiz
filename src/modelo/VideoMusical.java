package modelo;

import java.util.List;

public class VideoMusical extends ContenidoAudiovisual implements ConReparto {

    public static final long UMBRAL_VIRAL = 1_000_000;

    private final String artista;
    private final String album;
    private final int anioLanzamiento;
    private long reproducciones;
    private final ColeccionUnica<Actor> participantes = new ColeccionUnica<>();

    public VideoMusical(int id, String titulo, int duracionEnMinutos, String genero,
                        String artista, String album, int anioLanzamiento) {
        super(id, titulo, duracionEnMinutos, genero);
        this.artista = Validacion.textoObligatorio(artista, "artista");
        this.album = Validacion.textoObligatorio(album, "álbum");
        this.anioLanzamiento = Validacion.enRango(anioLanzamiento, 1900, 2100, "año de lanzamiento");
    }

    public String getArtista() {
        return artista;
    }

    public String getAlbum() {
        return album;
    }

    public int getAnioLanzamiento() {
        return anioLanzamiento;
    }

    public long getReproducciones() {
        return reproducciones;
    }

    public void registrarReproducciones(long cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("Las reproducciones no pueden ser negativas");
        }
        reproducciones += cantidad;
    }

    public boolean esViral() {
        return reproducciones >= UMBRAL_VIRAL;
    }

    @Override
    public boolean agregarActor(Actor actor) {
        return participantes.agregar(actor);
    }

    @Override
    public boolean quitarActor(Actor actor) {
        return participantes.quitar(actor);
    }

    @Override
    public List<Actor> getActores() {
        return participantes.comoLista();
    }

    @Override
    public String getTipo() {
        return "Video musical";
    }

    @Override
    protected String detallesEspecificos() {
        return TextoDetalle.linea("Artista", artista + " | Álbum: " + album + " (" + anioLanzamiento + ")")
                + TextoDetalle.linea("Reproducciones", reproducciones + (esViral() ? " [VIRAL]" : ""))
                + TextoDetalle.lista("Participantes", getActores());
    }
}

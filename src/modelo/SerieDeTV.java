package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SerieDeTV extends ContenidoAudiovisual {

    public static final int ANIO_MINIMO = 1900;
    public static final int ANIO_MAXIMO = 2100;

    private final List<Temporada> temporadas = new ArrayList<>();

    public SerieDeTV(int id, String titulo, int duracionEnMinutos, String genero) {
        super(id, titulo, duracionEnMinutos, genero);
    }

    /** La serie crea la temporada y le asigna el siguiente número (composición). */
    public Temporada agregarTemporada(int anioEstreno, int numeroEpisodios) {
        Validacion.enRango(anioEstreno, ANIO_MINIMO, ANIO_MAXIMO, "año de estreno");
        Validacion.positivo(numeroEpisodios, "número de episodios");
        Temporada temporada = new Temporada(temporadas.size() + 1, anioEstreno, numeroEpisodios);
        temporadas.add(temporada);
        return temporada;
    }

    public boolean eliminarUltimaTemporada() {
        if (temporadas.isEmpty()) {
            return false;
        }
        temporadas.remove(temporadas.size() - 1);
        return true;
    }

    public List<Temporada> getTemporadas() {
        return Collections.unmodifiableList(temporadas);
    }

    public int getNumeroTemporadas() {
        return temporadas.size();
    }

    public int getTotalEpisodios() {
        return temporadas.stream().mapToInt(Temporada::getNumeroEpisodios).sum();
    }

    @Override
    public String getTipo() {
        return "Serie de TV";
    }

    @Override
    protected String detallesEspecificos() {
        return TextoDetalle.linea("Episodios totales", getTotalEpisodios())
                + TextoDetalle.lista("Temporadas", temporadas);
    }
}

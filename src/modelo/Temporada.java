package modelo;

/**
 * Temporada de una serie. Composición: solo SerieDeTV puede crearla,
 * por eso el constructor no es público.
 */
public class Temporada {

    private final int numero;
    private final int anioEstreno;
    private final int numeroEpisodios;

    Temporada(int numero, int anioEstreno, int numeroEpisodios) {
        this.numero = numero;
        this.anioEstreno = anioEstreno;
        this.numeroEpisodios = numeroEpisodios;
    }

    public int getNumero() {
        return numero;
    }

    public int getAnioEstreno() {
        return anioEstreno;
    }

    public int getNumeroEpisodios() {
        return numeroEpisodios;
    }

    @Override
    public String toString() {
        return "Temporada " + numero + " (" + anioEstreno + ") - " + numeroEpisodios + " episodios";
    }
}

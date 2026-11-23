package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lista que no admite elementos nulos ni repetidos.
 * Reemplaza la lógica de "agregar sin duplicados" que estaba copiada en
 * Pelicula, VideoMusical, Documental y Podcast.
 */
public final class ColeccionUnica<T> {

    private final List<T> elementos = new ArrayList<>();

    public boolean agregar(T elemento) {
        Validacion.noNulo(elemento, "elemento");
        if (elementos.contains(elemento)) {
            return false;
        }
        return elementos.add(elemento);
    }

    public boolean quitar(T elemento) {
        return elementos.remove(elemento);
    }

    public boolean contiene(T elemento) {
        return elementos.contains(elemento);
    }

    public int tamanio() {
        return elementos.size();
    }

    public List<T> comoLista() {
        return Collections.unmodifiableList(elementos);
    }
}

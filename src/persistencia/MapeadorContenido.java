package persistencia;

import java.util.List;

import modelo.ContenidoAudiovisual;

/**
 * Sabe convertir un tipo de contenido a sus columnas propias del CSV y al revés.
 * Para guardar un tipo nuevo basta con crear otro mapeador, sin tocar RepositorioCsv (OCP).
 */
public interface MapeadorContenido<T extends ContenidoAudiovisual> {

    String getTipo();

    Class<T> getClase();

    List<String> columnasPropias(T contenido);

    T crear(DatosComunes datos, FilaCsv fila, int primeraColumnaPropia) throws FormatoCsvException;
}

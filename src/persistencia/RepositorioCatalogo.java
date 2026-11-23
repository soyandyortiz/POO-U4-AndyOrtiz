package persistencia;

import modelo.Catalogo;

/**
 * Abstracción de dónde se guardan los datos. El controlador depende de esta
 * interfaz y no de los archivos CSV directamente (DIP).
 */
public interface RepositorioCatalogo {

    boolean existenDatos();

    Catalogo cargar() throws PersistenciaException;

    void guardar(Catalogo catalogo) throws PersistenciaException;
}

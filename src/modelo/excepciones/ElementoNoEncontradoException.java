package modelo.excepciones;

/** Se lanza cuando se busca en el catálogo un ID que no existe. */
public class ElementoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ElementoNoEncontradoException(String tipoElemento, int id) {
        super("No existe " + tipoElemento + " con ID " + id);
    }
}

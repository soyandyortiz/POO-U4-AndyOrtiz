package persistencia;

/** Una línea de un archivo CSV no tiene el formato esperado. Indica archivo y número de línea. */
public class FormatoCsvException extends PersistenciaException {

    private static final long serialVersionUID = 1L;

    private final String archivo;
    private final int linea;

    public FormatoCsvException(String archivo, int linea, String detalle) {
        super(archivo + ", línea " + linea + ": " + detalle);
        this.archivo = archivo;
        this.linea = linea;
    }

    public String getArchivo() {
        return archivo;
    }

    public int getLinea() {
        return linea;
    }
}

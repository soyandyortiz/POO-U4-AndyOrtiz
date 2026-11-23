package persistencia;

import java.util.List;

/**
 * Una fila leída de un archivo CSV. Convierte cada campo al tipo necesario
 * y, si no puede, lanza FormatoCsvException indicando archivo, línea y columna.
 */
public final class FilaCsv {

    private final String archivo;
    private final int numeroLinea;
    private final List<String> campos;

    public FilaCsv(String archivo, int numeroLinea, List<String> campos) {
        this.archivo = archivo;
        this.numeroLinea = numeroLinea;
        this.campos = List.copyOf(campos);
    }

    public String texto(int columna, String nombre) throws FormatoCsvException {
        if (columna >= campos.size()) {
            throw error("falta la columna '" + nombre + "'");
        }
        String valor = campos.get(columna).trim();
        if (valor.isEmpty()) {
            throw error("la columna '" + nombre + "' está vacía");
        }
        return valor;
    }

    public int entero(int columna, String nombre) throws FormatoCsvException {
        String valor = texto(columna, nombre);
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw error("'" + valor + "' no es un número válido en la columna '" + nombre + "'");
        }
    }

    public long enteroLargo(int columna, String nombre) throws FormatoCsvException {
        String valor = texto(columna, nombre);
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            throw error("'" + valor + "' no es un número válido en la columna '" + nombre + "'");
        }
    }

    public FormatoCsvException error(String detalle) {
        return new FormatoCsvException(archivo, numeroLinea, detalle);
    }
}

package modelo;

/**
 * Reglas de validación que se repetían en varias clases del modelo.
 * Se concentran aquí para no duplicarlas (DRY).
 */
public final class Validacion {

    private Validacion() {
    }

    public static String textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede estar vacío");
        }
        return valor.trim();
    }

    public static int positivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser mayor a 0");
        }
        return valor;
    }

    public static int enRango(int valor, int minimo, int maximo, String campo) {
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException(
                    "El campo " + campo + " debe estar entre " + minimo + " y " + maximo);
        }
        return valor;
    }

    public static <T> T noNulo(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }
}

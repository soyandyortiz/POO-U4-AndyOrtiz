package modelo;

import java.util.List;

/** Arma las secciones de texto que usan los detalles de cada contenido. */
final class TextoDetalle {

    private TextoDetalle() {
    }

    static String linea(String etiqueta, Object valor) {
        return etiqueta + ": " + valor + "\n";
    }

    static String lista(String titulo, List<?> elementos) {
        StringBuilder texto = new StringBuilder(titulo + " (" + elementos.size() + "):\n");
        for (Object elemento : elementos) {
            texto.append("  - ").append(elemento).append("\n");
        }
        return texto.toString();
    }
}

package persistencia;

import java.util.ArrayList;
import java.util.List;

/**
 * Convierte una fila de texto CSV en campos y viceversa.
 * Separador: coma. Los campos que tienen comas o comillas se escriben entre comillas
 * y las comillas internas se duplican (formato estándar RFC 4180).
 */
public final class Csv {

    public static final char SEPARADOR = ',';
    private static final char COMILLA = '"';

    private Csv() {
    }

    public static String unirFila(List<String> campos) {
        List<String> escapados = new ArrayList<>();
        for (String campo : campos) {
            escapados.add(escapar(campo == null ? "" : campo));
        }
        return String.join(String.valueOf(SEPARADOR), escapados);
    }

    private static String escapar(String campo) {
        boolean necesitaComillas = campo.indexOf(SEPARADOR) >= 0 || campo.indexOf(COMILLA) >= 0
                || campo.startsWith(" ") || campo.endsWith(" ");
        if (!necesitaComillas) {
            return campo;
        }
        return COMILLA + campo.replace("\"", "\"\"") + COMILLA;
    }

    public static List<String> separarFila(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean dentroDeComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (dentroDeComillas) {
                if (c == COMILLA && i + 1 < linea.length() && linea.charAt(i + 1) == COMILLA) {
                    actual.append(COMILLA);
                    i++;
                } else if (c == COMILLA) {
                    dentroDeComillas = false;
                } else {
                    actual.append(c);
                }
            } else if (c == COMILLA) {
                dentroDeComillas = true;
            } else if (c == SEPARADOR) {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (dentroDeComillas) {
            throw new IllegalArgumentException("Hay comillas sin cerrar");
        }
        campos.add(actual.toString());
        return campos;
    }
}

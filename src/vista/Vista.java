package vista;

import java.util.List;
import java.util.Map;

/**
 * Lo que el controlador necesita de una vista. Al depender de esta interfaz,
 * el controlador se puede probar con una vista simulada (DIP).
 */
public interface Vista {

    int elegirOpcion(String titulo, Map<Integer, String> opciones);

    String pedirTexto(String etiqueta);

    int pedirEntero(String etiqueta, int minimo, int maximo);

    boolean confirmar(String pregunta);

    void mostrarTexto(String texto);

    void mostrarLista(String titulo, List<?> elementos);

    void mostrarMensaje(String mensaje);

    void mostrarError(String mensaje);
}

package controlador;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;

import vista.Vista;

/** Vista falsa para las pruebas: responde con valores preparados y guarda lo que se muestra. */
class VistaSimulada implements Vista {

    private final Deque<String> respuestas = new ArrayDeque<>();
    final List<String> mensajes = new ArrayList<>();
    final List<String> errores = new ArrayList<>();
    final List<String> textos = new ArrayList<>();

    VistaSimulada responder(Object... valores) {
        for (Object valor : valores) {
            respuestas.add(String.valueOf(valor));
        }
        return this;
    }

    private String siguiente() {
        if (respuestas.isEmpty()) {
            throw new IllegalStateException("La prueba no preparó suficientes respuestas");
        }
        return respuestas.poll();
    }

    @Override
    public int elegirOpcion(String titulo, Map<Integer, String> opciones) {
        return Integer.parseInt(siguiente());
    }

    @Override
    public String pedirTexto(String etiqueta) {
        return siguiente();
    }

    @Override
    public int pedirEntero(String etiqueta, int minimo, int maximo) {
        return Integer.parseInt(siguiente());
    }

    @Override
    public boolean confirmar(String pregunta) {
        return siguiente().equalsIgnoreCase("s");
    }

    @Override
    public void mostrarTexto(String texto) {
        textos.add(texto);
    }

    @Override
    public void mostrarLista(String titulo, List<?> elementos) {
        textos.add(titulo + ": " + elementos);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        mensajes.add(mensaje);
    }

    @Override
    public void mostrarError(String mensaje) {
        errores.add(mensaje);
    }
}

package vista;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Interfaz de usuario por consola. Solo lee y muestra; no contiene reglas del negocio. */
public class VistaConsola implements Vista {

    private static final String LINEA = "=".repeat(60);

    private final Scanner entrada;
    private final PrintStream salida;

    public VistaConsola(Scanner entrada, PrintStream salida) {
        this.entrada = entrada;
        this.salida = salida;
    }

    @Override
    public int elegirOpcion(String titulo, Map<Integer, String> opciones) {
        salida.println();
        salida.println(LINEA);
        salida.println(" " + titulo);
        salida.println(LINEA);
        opciones.forEach((numero, texto) -> salida.printf(" %2d. %s%n", numero, texto));
        salida.println(LINEA);
        while (true) {
            int opcion = leerEntero("Seleccione una opción: ");
            if (opciones.containsKey(opcion)) {
                return opcion;
            }
            salida.println("   La opción " + opcion + " no existe.");
        }
    }

    @Override
    public String pedirTexto(String etiqueta) {
        while (true) {
            String texto = leerLinea(etiqueta + ": ");
            if (!texto.isEmpty()) {
                return texto;
            }
            salida.println("   Este campo es obligatorio.");
        }
    }

    @Override
    public int pedirEntero(String etiqueta, int minimo, int maximo) {
        while (true) {
            int valor = leerEntero(etiqueta + " (" + minimo + "-" + maximo + "): ");
            if (valor >= minimo && valor <= maximo) {
                return valor;
            }
            salida.println("   Debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    @Override
    public boolean confirmar(String pregunta) {
        String respuesta = leerLinea(pregunta + " (s/n): ");
        return respuesta.equalsIgnoreCase("s") || respuesta.equalsIgnoreCase("si");
    }

    @Override
    public void mostrarTexto(String texto) {
        salida.println(texto);
    }

    @Override
    public void mostrarLista(String titulo, List<?> elementos) {
        salida.println("---- " + titulo + " (" + elementos.size() + ") ----");
        if (elementos.isEmpty()) {
            salida.println("   (sin resultados)");
        }
        elementos.forEach(elemento -> salida.println(" " + elemento));
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        salida.println(">> " + mensaje);
    }

    @Override
    public void mostrarError(String mensaje) {
        salida.println("[ERROR] " + mensaje);
    }

    private int leerEntero(String mensaje) {
        while (true) {
            String texto = leerLinea(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                salida.println("   Ingrese un número entero.");
            }
        }
    }

    private String leerLinea(String mensaje) {
        salida.print(mensaje);
        if (!entrada.hasNextLine()) {
            throw new IllegalStateException("Se terminó la entrada de datos");
        }
        return entrada.nextLine().trim();
    }
}

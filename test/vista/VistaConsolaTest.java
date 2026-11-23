package vista;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

class VistaConsolaTest {

    private final ByteArrayOutputStream salida = new ByteArrayOutputStream();

    private VistaConsola vistaConEntrada(String... lineas) {
        String texto = String.join("\n", lineas) + "\n";
        Scanner entrada = new Scanner(new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
        return new VistaConsola(entrada, new PrintStream(salida, true, StandardCharsets.UTF_8));
    }

    private String loMostrado() {
        return salida.toString(StandardCharsets.UTF_8);
    }

    @Test
    void vuelveAPedirUnNumeroSiSeEscribeTexto() {
        VistaConsola vista = vistaConEntrada("abc", "7", "3");
        assertEquals(3, vista.pedirEntero("Estrellas", 1, 5));
        assertTrue(loMostrado().contains("Ingrese un número entero"));
        assertTrue(loMostrado().contains("Debe estar entre 1 y 5"));
    }

    @Test
    void soloAceptaOpcionesDelMenu() {
        VistaConsola vista = vistaConEntrada("9", "2");
        assertEquals(2, vista.elegirOpcion("Menú", Map.of(1, "Uno", 2, "Dos")));
        assertTrue(loMostrado().contains("La opción 9 no existe"));
    }

    @Test
    void noAceptaTextoVacio() {
        VistaConsola vista = vistaConEntrada("", "  Avatar ");
        assertEquals("Avatar", vista.pedirTexto("Título"));
        assertTrue(loMostrado().contains("obligatorio"));
    }

    @Test
    void confirmaConSoS() {
        assertTrue(vistaConEntrada("s").confirmar("¿Guardar?"));
        assertTrue(vistaConEntrada("SI").confirmar("¿Guardar?"));
        assertFalse(vistaConEntrada("n").confirmar("¿Guardar?"));
    }

    @Test
    void muestraListasMensajesYErrores() {
        VistaConsola vista = vistaConEntrada();
        vista.mostrarLista("Actores", List.of());
        vista.mostrarLista("Números", List.of(1, 2));
        vista.mostrarMensaje("listo");
        vista.mostrarError("falló");
        vista.mostrarTexto("texto libre");
        String texto = loMostrado();
        assertTrue(texto.contains("(sin resultados)"));
        assertTrue(texto.contains(">> listo"));
        assertTrue(texto.contains("[ERROR] falló"));
        assertTrue(texto.contains("texto libre"));
    }

    @Test
    void avisaSiSeTerminaLaEntrada() {
        VistaConsola vista = vistaConEntrada();
        assertThrows(IllegalStateException.class, () -> vista.pedirTexto("Nombre"));
    }
}

package app;

import java.util.Scanner;

import controlador.CatalogoController;
import vista.Vista;
import vista.VistaConsola;

/** Arma las dependencias del sistema y lo pone en marcha. */
public class Main {

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            Vista vista = new VistaConsola(entrada, System.out);
            new CatalogoController(vista).iniciar();
        }
    }
}

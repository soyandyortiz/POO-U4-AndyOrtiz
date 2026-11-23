package app;

import java.nio.file.Path;
import java.util.Scanner;

import controlador.CatalogoController;
import persistencia.Mapeadores;
import persistencia.RepositorioCatalogo;
import persistencia.RepositorioCsv;
import vista.Vista;
import vista.VistaConsola;

/** Arma las dependencias del sistema y lo pone en marcha. */
public class Main {

    private static final Path CARPETA_DATOS = Path.of("datos");

    public static void main(String[] args) {
        try (Scanner entrada = new Scanner(System.in)) {
            Vista vista = new VistaConsola(entrada, System.out);
            RepositorioCatalogo repositorio = new RepositorioCsv(CARPETA_DATOS, Mapeadores.porDefecto());
            new CatalogoController(vista, repositorio).iniciar();
        }
    }
}

package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Lee y escribe archivos CSV completos (con fila de encabezado) en UTF-8. */
public final class ArchivoCsv {

    private ArchivoCsv() {
    }

    /** Devuelve las filas sin el encabezado. Si el archivo no existe devuelve una lista vacía. */
    public static List<FilaCsv> leer(Path archivo) throws PersistenciaException {
        if (!Files.exists(archivo)) {
            return List.of();
        }
        String nombre = archivo.getFileName().toString();
        List<FilaCsv> filas = new ArrayList<>();
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea = lector.readLine(); // encabezado
            int numeroLinea = 1;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (!linea.isBlank()) {
                    filas.add(new FilaCsv(nombre, numeroLinea, separar(nombre, numeroLinea, linea)));
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer " + nombre + ": " + e.getMessage(), e);
        }
        return filas;
    }

    private static List<String> separar(String nombre, int numeroLinea, String linea) throws FormatoCsvException {
        try {
            return Csv.separarFila(linea);
        } catch (IllegalArgumentException e) {
            throw new FormatoCsvException(nombre, numeroLinea, e.getMessage());
        }
    }

    /**
     * Escribe primero en un archivo temporal y luego lo reemplaza, para no dejar
     * el archivo original a medias si ocurre un error mientras se guarda.
     */
    public static void escribir(Path archivo, List<String> encabezado, List<List<String>> filas)
            throws PersistenciaException {
        Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
        try {
            Files.createDirectories(archivo.toAbsolutePath().getParent());
            try (BufferedWriter escritor = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
                escribirLinea(escritor, encabezado);
                for (List<String> fila : filas) {
                    escribirLinea(escritor, fila);
                }
            }
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo guardar " + archivo.getFileName() + ": " + e.getMessage(), e);
        }
    }

    private static void escribirLinea(BufferedWriter escritor, List<String> campos) throws IOException {
        escritor.write(Csv.unirFila(campos));
        escritor.newLine();
    }
}

package persistencia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class CsvTest {

    @Test
    void separaCamposSimples() {
        assertEquals(List.of("1", "Avatar", "162"), Csv.separarFila("1,Avatar,162"));
    }

    @Test
    void respetaComasDentroDeComillas() {
        assertEquals(List.of("6", "Warner Bros, Paramount"), Csv.separarFila("6,\"Warner Bros, Paramount\""));
    }

    @Test
    void convierteComillasDobles() {
        assertEquals(List.of("El \"mejor\" show"), Csv.separarFila("\"El \"\"mejor\"\" show\""));
    }

    @Test
    void conservaCamposVacios() {
        assertEquals(List.of("2", "", ""), Csv.separarFila("2,,"));
    }

    @Test
    void escribeYLeeLosMismosCampos() {
        List<String> original = List.of("1", "Película, parte 2", "Dice \"hola\"", " espacios ", "");
        assertEquals(original, Csv.separarFila(Csv.unirFila(original)));
    }

    @Test
    void escribeNulosComoCamposVacios() {
        assertEquals("a,,b", Csv.unirFila(java.util.Arrays.asList("a", null, "b")));
    }

    @Test
    void rechazaComillasSinCerrar() {
        assertThrows(IllegalArgumentException.class, () -> Csv.separarFila("1,\"sin cerrar"));
    }
}

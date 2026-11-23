package modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PersonasYColeccionTest {

    @Test
    void dosActoresConElMismoIdSonIguales() {
        Actor a = new Actor(1, "Zoe Saldaña", "Estados Unidos", 2000);
        Actor b = new Actor(1, "Otro nombre", "Ecuador", 2010);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new Actor(2, "Zoe Saldaña", "Estados Unidos", 2000));
    }

    @Test
    void validaElAnioDeDebutDelActor() {
        assertThrows(IllegalArgumentException.class, () -> new Actor(1, "Nombre", "País", 1500));
        Actor actor = new Actor(1, "Nombre", "País", 1990);
        assertEquals("Nombre", actor.getNombre());
        assertEquals("País", actor.getNacionalidad());
        assertEquals(1990, actor.getAnioDebut());
    }

    @Test
    void investigadorGuardaSusDatos() {
        Investigador i = new Investigador(2, "María Torres", "Astrofísica", "EPN");
        assertEquals("María Torres", i.getNombre());
        assertEquals("Astrofísica", i.getEspecialidad());
        assertEquals("EPN", i.getInstitucion());
        assertTrue(i.toString().contains("[2]"));
        assertEquals(i, new Investigador(2, "Otro", "X", "Y"));
        assertThrows(IllegalArgumentException.class, () -> new Investigador(3, "Nombre", "", "EPN"));
    }

    @Test
    void coleccionUnicaNoAceptaNulosNiRepetidos() {
        ColeccionUnica<String> coleccion = new ColeccionUnica<>();
        assertTrue(coleccion.agregar("a"));
        assertFalse(coleccion.agregar("a"));
        assertThrows(IllegalArgumentException.class, () -> coleccion.agregar(null));
        assertTrue(coleccion.contiene("a"));
        assertEquals(1, coleccion.tamanio());
        assertTrue(coleccion.quitar("a"));
        assertEquals(0, coleccion.comoLista().size());
    }
}

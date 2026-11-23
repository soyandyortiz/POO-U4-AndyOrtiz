package modelo;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import modelo.excepciones.ElementoNoEncontradoException;

/**
 * Guarda los contenidos, actores e investigadores y responde consultas sobre ellos.
 * Antes esta lógica estaba mezclada en el controlador.
 */
public class Catalogo {

    private final Map<Integer, ContenidoAudiovisual> contenidos = new LinkedHashMap<>();
    private final Map<Integer, Actor> actores = new LinkedHashMap<>();
    private final Map<Integer, Investigador> investigadores = new LinkedHashMap<>();

    // ---------------------------------------------------------- altas

    public void agregarContenido(ContenidoAudiovisual contenido) {
        agregarSinRepetirId(contenidos, contenido.getId(), contenido, "contenido");
    }

    public void agregarActor(Actor actor) {
        agregarSinRepetirId(actores, actor.getId(), actor, "actor");
    }

    public void agregarInvestigador(Investigador investigador) {
        agregarSinRepetirId(investigadores, investigador.getId(), investigador, "investigador");
    }

    private static <T> void agregarSinRepetirId(Map<Integer, T> destino, int id, T elemento, String tipo) {
        Validacion.noNulo(elemento, tipo);
        if (destino.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un " + tipo + " con ID " + id);
        }
        destino.put(id, elemento);
    }

    public int siguienteIdContenido() {
        return siguienteId(contenidos.keySet());
    }

    public int siguienteIdActor() {
        return siguienteId(actores.keySet());
    }

    public int siguienteIdInvestigador() {
        return siguienteId(investigadores.keySet());
    }

    private static int siguienteId(Collection<Integer> idsUsados) {
        return idsUsados.stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    // ---------------------------------------------------------- búsquedas

    public ContenidoAudiovisual buscarContenido(int id) {
        return buscar(contenidos, id, "contenido");
    }

    public Actor buscarActor(int id) {
        return buscar(actores, id, "actor");
    }

    public Investigador buscarInvestigador(int id) {
        return buscar(investigadores, id, "investigador");
    }

    /** Busca un contenido y comprueba que sea del tipo pedido (por ejemplo, ConReparto). */
    public <T> T buscarContenidoComo(int id, Class<T> tipo, String descripcionTipo) {
        ContenidoAudiovisual contenido = buscarContenido(id);
        if (!tipo.isInstance(contenido)) {
            throw new IllegalArgumentException("El contenido " + id + " (" + contenido.getTipo()
                    + ") no es " + descripcionTipo);
        }
        return tipo.cast(contenido);
    }

    private static <T> T buscar(Map<Integer, T> origen, int id, String tipo) {
        T elemento = origen.get(id);
        if (elemento == null) {
            throw new ElementoNoEncontradoException(tipo, id);
        }
        return elemento;
    }

    public List<ContenidoAudiovisual> buscarPorTitulo(String texto) {
        String buscado = texto == null ? "" : texto.trim().toLowerCase();
        return contenidos.values().stream()
                .filter(c -> c.getTitulo().toLowerCase().contains(buscado))
                .toList();
    }

    // ---------------------------------------------------------- bajas

    /** Al eliminar un contenido, sus actores e investigadores siguen registrados. */
    public ContenidoAudiovisual eliminarContenido(int id) {
        ContenidoAudiovisual eliminado = buscarContenido(id);
        contenidos.remove(id);
        return eliminado;
    }

    // ---------------------------------------------------------- consultas

    public List<ContenidoAudiovisual> getContenidos() {
        return List.copyOf(contenidos.values());
    }

    public List<Actor> getActores() {
        return List.copyOf(actores.values());
    }

    public List<Investigador> getInvestigadores() {
        return List.copyOf(investigadores.values());
    }

    public boolean estaVacio() {
        return contenidos.isEmpty() && actores.isEmpty() && investigadores.isEmpty();
    }

    public Map<String, Integer> contarPorTipo() {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        for (ContenidoAudiovisual contenido : contenidos.values()) {
            conteo.merge(contenido.getTipo(), 1, Integer::sum);
        }
        return conteo;
    }

    public int getDuracionTotal() {
        return contenidos.values().stream().mapToInt(ContenidoAudiovisual::getDuracionEnMinutos).sum();
    }

    public Optional<ContenidoAudiovisual> getMejorCalificado() {
        return contenidos.values().stream()
                .filter(c -> c.getNumeroCalificaciones() > 0)
                .max(Comparator.comparingDouble(ContenidoAudiovisual::getCalificacionPromedio));
    }
}

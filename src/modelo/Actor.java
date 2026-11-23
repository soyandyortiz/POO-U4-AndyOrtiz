package modelo;

/** Actor o actriz registrado en el catálogo. */
public class Actor {

    public static final int ANIO_MINIMO = 1880;
    public static final int ANIO_MAXIMO = 2100;

    private final int id;
    private final String nombre;
    private final String nacionalidad;
    private final int anioDebut;

    public Actor(int id, String nombre, String nacionalidad, int anioDebut) {
        this.id = Validacion.positivo(id, "id");
        this.nombre = Validacion.textoObligatorio(nombre, "nombre");
        this.nacionalidad = Validacion.textoObligatorio(nacionalidad, "nacionalidad");
        this.anioDebut = Validacion.enRango(anioDebut, ANIO_MINIMO, ANIO_MAXIMO, "año de debut");
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public int getAnioDebut() {
        return anioDebut;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof Actor actor && actor.id == id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " (" + nacionalidad + ", debut " + anioDebut + ")";
    }
}

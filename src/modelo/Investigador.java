package modelo;

/** Investigador o experto que asesora documentales o participa en podcasts. */
public class Investigador {

    private final int id;
    private final String nombre;
    private final String especialidad;
    private final String institucion;

    public Investigador(int id, String nombre, String especialidad, String institucion) {
        this.id = Validacion.positivo(id, "id");
        this.nombre = Validacion.textoObligatorio(nombre, "nombre");
        this.especialidad = Validacion.textoObligatorio(especialidad, "especialidad");
        this.institucion = Validacion.textoObligatorio(institucion, "institución");
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getInstitucion() {
        return institucion;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof Investigador investigador && investigador.id == id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nombre + " - " + especialidad + " (" + institucion + ")";
    }
}

package modelo;

/**
 * Superclase de todo el contenido del catálogo. Guarda los datos comunes
 * y define cómo se arman los detalles; cada subclase solo aporta su parte.
 */
public abstract class ContenidoAudiovisual implements Calificable {

    private final int id;
    private String titulo;
    private int duracionEnMinutos;
    private String genero;
    private int sumaEstrellas;
    private int numeroCalificaciones;

    protected ContenidoAudiovisual(int id, String titulo, int duracionEnMinutos, String genero) {
        this.id = Validacion.positivo(id, "id");
        this.titulo = Validacion.textoObligatorio(titulo, "título");
        this.duracionEnMinutos = Validacion.positivo(duracionEnMinutos, "duración");
        this.genero = Validacion.textoObligatorio(genero, "género");
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = Validacion.textoObligatorio(titulo, "título");
    }

    public int getDuracionEnMinutos() {
        return duracionEnMinutos;
    }

    public void setDuracionEnMinutos(int duracionEnMinutos) {
        this.duracionEnMinutos = Validacion.positivo(duracionEnMinutos, "duración");
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = Validacion.textoObligatorio(genero, "género");
    }

    @Override
    public void calificar(int estrellas) {
        Validacion.enRango(estrellas, MIN_ESTRELLAS, MAX_ESTRELLAS, "estrellas");
        sumaEstrellas += estrellas;
        numeroCalificaciones++;
    }

    /** Recupera las calificaciones guardadas en un archivo. */
    public void restaurarCalificaciones(int sumaEstrellas, int numeroCalificaciones) {
        if (numeroCalificaciones < 0
                || sumaEstrellas < numeroCalificaciones * MIN_ESTRELLAS
                || sumaEstrellas > numeroCalificaciones * MAX_ESTRELLAS) {
            throw new IllegalArgumentException("Calificaciones guardadas inválidas: suma " + sumaEstrellas
                    + " para " + numeroCalificaciones + " votos");
        }
        this.sumaEstrellas = sumaEstrellas;
        this.numeroCalificaciones = numeroCalificaciones;
    }

    @Override
    public double getCalificacionPromedio() {
        return numeroCalificaciones == 0 ? 0 : (double) sumaEstrellas / numeroCalificaciones;
    }

    @Override
    public int getNumeroCalificaciones() {
        return numeroCalificaciones;
    }

    public int getSumaEstrellas() {
        return sumaEstrellas;
    }

    public abstract String getTipo();

    protected abstract String detallesEspecificos();

    public String obtenerDetalles() {
        return "Detalles (" + getTipo() + "):\n"
                + TextoDetalle.linea("ID", id)
                + TextoDetalle.linea("Título", titulo)
                + TextoDetalle.linea("Duración en minutos", duracionEnMinutos)
                + TextoDetalle.linea("Género", genero)
                + TextoDetalle.linea("Calificación", String.format("%.1f/5 (%d votos)",
                        getCalificacionPromedio(), numeroCalificaciones))
                + detallesEspecificos();
    }

    @Override
    public String toString() {
        return String.format("[%d] %-13s | %s (%d min) | %.1f/5",
                id, getTipo(), titulo, duracionEnMinutos, getCalificacionPromedio());
    }
}

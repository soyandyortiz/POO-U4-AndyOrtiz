package modelo;

import java.util.List;

public class Pelicula extends ContenidoAudiovisual implements ConReparto {

    private String estudio;
    private final ColeccionUnica<Actor> reparto = new ColeccionUnica<>();

    public Pelicula(int id, String titulo, int duracionEnMinutos, String genero, String estudio) {
        super(id, titulo, duracionEnMinutos, genero);
        this.estudio = Validacion.textoObligatorio(estudio, "estudio");
    }

    public String getEstudio() {
        return estudio;
    }

    public void setEstudio(String estudio) {
        this.estudio = Validacion.textoObligatorio(estudio, "estudio");
    }

    @Override
    public boolean agregarActor(Actor actor) {
        return reparto.agregar(actor);
    }

    @Override
    public boolean quitarActor(Actor actor) {
        return reparto.quitar(actor);
    }

    @Override
    public List<Actor> getActores() {
        return reparto.comoLista();
    }

    @Override
    public String getTipo() {
        return "Película";
    }

    @Override
    protected String detallesEspecificos() {
        return TextoDetalle.linea("Estudio", estudio) + TextoDetalle.lista("Reparto", getActores());
    }
}

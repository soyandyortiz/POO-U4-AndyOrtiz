package modelo;

import java.util.List;

public class Documental extends ContenidoAudiovisual implements ConInvestigadores {

    private String tema;
    private final ColeccionUnica<Investigador> asesores = new ColeccionUnica<>();

    public Documental(int id, String titulo, int duracionEnMinutos, String genero, String tema) {
        super(id, titulo, duracionEnMinutos, genero);
        this.tema = Validacion.textoObligatorio(tema, "tema");
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = Validacion.textoObligatorio(tema, "tema");
    }

    @Override
    public boolean agregarInvestigador(Investigador investigador) {
        return asesores.agregar(investigador);
    }

    @Override
    public boolean quitarInvestigador(Investigador investigador) {
        return asesores.quitar(investigador);
    }

    @Override
    public List<Investigador> getInvestigadores() {
        return asesores.comoLista();
    }

    @Override
    public String getTipo() {
        return "Documental";
    }

    @Override
    protected String detallesEspecificos() {
        return TextoDetalle.linea("Tema", tema) + TextoDetalle.lista("Investigadores asesores", getInvestigadores());
    }
}

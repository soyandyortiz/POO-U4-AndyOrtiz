package modelo;

import java.util.List;

public class Podcast extends ContenidoAudiovisual implements ConInvestigadores {

    private final String anfitrion;
    private final String plataforma;
    private final int numeroEpisodio;
    private final ColeccionUnica<Investigador> invitados = new ColeccionUnica<>();

    public Podcast(int id, String titulo, int duracionEnMinutos, String genero,
                   String anfitrion, String plataforma, int numeroEpisodio) {
        super(id, titulo, duracionEnMinutos, genero);
        this.anfitrion = Validacion.textoObligatorio(anfitrion, "anfitrión");
        this.plataforma = Validacion.textoObligatorio(plataforma, "plataforma");
        this.numeroEpisodio = Validacion.positivo(numeroEpisodio, "número de episodio");
    }

    public String getAnfitrion() {
        return anfitrion;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public int getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public boolean esMonologo() {
        return invitados.tamanio() == 0;
    }

    @Override
    public boolean agregarInvestigador(Investigador investigador) {
        return invitados.agregar(investigador);
    }

    @Override
    public boolean quitarInvestigador(Investigador investigador) {
        return invitados.quitar(investigador);
    }

    @Override
    public List<Investigador> getInvestigadores() {
        return invitados.comoLista();
    }

    @Override
    public String getTipo() {
        return "Podcast";
    }

    @Override
    protected String detallesEspecificos() {
        String encabezado = TextoDetalle.linea("Episodio", "#" + numeroEpisodio + " | Anfitrión: " + anfitrion
                + " | Plataforma: " + plataforma);
        return esMonologo()
                ? encabezado + "Formato: monólogo\n"
                : encabezado + TextoDetalle.lista("Invitados", getInvestigadores());
    }
}

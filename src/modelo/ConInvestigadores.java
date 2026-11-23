package modelo;

import java.util.List;

/** Contenido asesorado o acompañado por investigadores (Documental, Podcast). */
public interface ConInvestigadores {

    boolean agregarInvestigador(Investigador investigador);

    boolean quitarInvestigador(Investigador investigador);

    List<Investigador> getInvestigadores();
}

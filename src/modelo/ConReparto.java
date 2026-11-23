package modelo;

import java.util.List;

/** Contenido en el que participan actores (Pelicula, VideoMusical). */
public interface ConReparto {

    boolean agregarActor(Actor actor);

    boolean quitarActor(Actor actor);

    List<Actor> getActores();
}

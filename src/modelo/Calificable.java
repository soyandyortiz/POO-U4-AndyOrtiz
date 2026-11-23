package modelo;

/** Contenido que el público puede calificar de 1 a 5 estrellas. */
public interface Calificable {

    int MIN_ESTRELLAS = 1;
    int MAX_ESTRELLAS = 5;

    void calificar(int estrellas);

    double getCalificacionPromedio();

    int getNumeroCalificaciones();
}

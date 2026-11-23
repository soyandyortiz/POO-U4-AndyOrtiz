package controlador;

import modelo.Catalogo;
import persistencia.PersistenciaException;
import persistencia.RepositorioCatalogo;

/** Repositorio falso: permite probar el controlador sin tocar archivos (gracias a DIP). */
class RepositorioEnMemoria implements RepositorioCatalogo {

    Catalogo guardado;
    int vecesGuardado;
    boolean fallar;

    RepositorioEnMemoria(Catalogo inicial) {
        this.guardado = inicial;
    }

    @Override
    public boolean existenDatos() {
        return guardado != null;
    }

    @Override
    public Catalogo cargar() throws PersistenciaException {
        if (fallar) {
            throw new PersistenciaException("archivo dañado");
        }
        return guardado;
    }

    @Override
    public void guardar(Catalogo catalogo) throws PersistenciaException {
        if (fallar) {
            throw new PersistenciaException("disco lleno");
        }
        guardado = catalogo;
        vecesGuardado++;
    }
}

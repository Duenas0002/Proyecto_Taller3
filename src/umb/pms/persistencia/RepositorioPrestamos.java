package umb.pms.persistencia;

import umb.pms.modelo.Prestamo;
import umb.pms.excepciones.PrestamoException;
import java.util.List;

/**
 * Contrato que define las operaciones de persistencia de prestamos (DIP / ISP).
 */
public interface RepositorioPrestamos {
    void guardarTodos(List<Prestamo> prestamos) throws PrestamoException;
    List<Prestamo> cargarTodos() throws PrestamoException;
}

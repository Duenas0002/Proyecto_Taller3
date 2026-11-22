package umb.pms.patrones.factory;

import umb.pms.modelo.Prestamo;
import umb.pms.excepciones.PrestamoException;
import java.time.LocalDate;

/**
 * Patrón Creacional Factory Method para centralizar la instanciación de Préstamos con validación de datos.
 */
public class FabricaPrestamos {
    public static Prestamo crearPrestamo(String codigoEst, String nombreEst, String equipoId, 
                                         String nombreEquipo, int dias) throws PrestamoException {
        if (codigoEst == null || codigoEst.trim().isEmpty()) {
            throw new PrestamoException("El código del estudiante es obligatorio.");
        }
        if (nombreEst == null || nombreEst.trim().isEmpty()) {
            throw new PrestamoException("El nombre del estudiante es obligatorio.");
        }
        if (equipoId == null || equipoId.trim().isEmpty()) {
            throw new PrestamoException("El identificador del equipo es obligatorio.");
        }
        if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
            throw new PrestamoException("El nombre del equipo es obligatorio.");
        }
        try {
            return new Prestamo(codigoEst.trim(), nombreEst.trim(), equipoId.trim(), 
                                nombreEquipo.trim(), LocalDate.now(), dias);
        } catch (IllegalArgumentException e) {
            throw new PrestamoException("Error en parámetros de creación: " + e.getMessage(), e);
        }
    }
}

package umb.pms.patrones.strategy;

import umb.pms.modelo.Prestamo;
import java.time.LocalDate;
import java.util.List;

public interface EstrategiaFiltroPrestamos {
    List<Prestamo> filtrar(List<Prestamo> prestamos, LocalDate fechaReferencia);
    String getNombre();
}

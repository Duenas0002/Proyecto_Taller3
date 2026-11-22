package umb.pms.patrones.strategy;

import umb.pms.modelo.Prestamo;
import java.time.LocalDate;
import java.util.List;

public class FiltroTodos implements EstrategiaFiltroPrestamos {
    @Override
    public List<Prestamo> filtrar(List<Prestamo> prestamos, LocalDate fechaReferencia) {
        return prestamos;
    }

    @Override
    public String getNombre() {
        return "Todos los Registros";
    }
}
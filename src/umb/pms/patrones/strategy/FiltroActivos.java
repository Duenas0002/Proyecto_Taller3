package umb.pms.patrones.strategy;

import umb.pms.modelo.Prestamo;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroActivos implements EstrategiaFiltroPrestamos {
    @Override
    public List<Prestamo> filtrar(List<Prestamo> prestamos, LocalDate fechaReferencia) {
        return prestamos.stream().filter(p -> !p.isDevuelto()).collect(Collectors.toList());
    }

    @Override
    public String getNombre() {
        return "Solo Activos";
    }
}
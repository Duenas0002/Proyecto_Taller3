package umb.pms.patrones.strategy;

import umb.pms.modelo.EstadoPrestamo;
import umb.pms.modelo.Prestamo;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroPorVencer implements EstrategiaFiltroPrestamos {
    @Override
    public List<Prestamo> filtrar(List<Prestamo> prestamos, LocalDate fechaReferencia) {
        return prestamos.stream()
                .filter(p -> p.calcularEstado(fechaReferencia) == EstadoPrestamo.POR_VENCER)
                .collect(Collectors.toList());
    }

    @Override
    public String getNombre() {
        return "Por Vencer (<= 2 días)";
    }
}
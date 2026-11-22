package umb.pms.patrones.strategy;

import umb.pms.modelo.EstadoPrestamo;
import umb.pms.modelo.Prestamo;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class FiltroVencidos implements EstrategiaFiltroPrestamos {
    @Override
    public List<Prestamo> filtrar(List<Prestamo> prestamos, LocalDate fechaReferencia) {
        return prestamos.stream()
                .filter(p -> p.calcularEstado(fechaReferencia) == EstadoPrestamo.VENCIDO)
                .collect(Collectors.toList());
    }

    @Override
    public String getNombre() {
        return "Solo Vencidos";
    }
}

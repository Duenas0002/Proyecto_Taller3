package umb.pms.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

/**
 * Clase que representa la entidad de dominio de un Préstamo de Equipo.
 * Incorpora reglas avanzadas de cálculo de vencimiento y trazabilidad de devolución.
 */
public class Prestamo implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DIAS_MAX_PRESTAMO = 30;
    public static final int DIAS_MAX_EXTENSION = 15;
    public static final int DIAS_ALERTA_POR_VENCER = 2;

    private final String id;
    private final String codigoEstudiante;
    private final String nombreEstudiante;
    private final String equipoId;
    private final String nombreEquipo;
    private final LocalDate fechaPrestamo;
    private LocalDate fechaLimite;
    private LocalDate fechaRealDevolucion;
    private boolean devuelto;

    public Prestamo(String codigoEstudiante, String nombreEstudiante, String equipoId, 
                    String nombreEquipo, LocalDate fechaPrestamo, int diasSolicitados) {
        if (diasSolicitados <= 0 || diasSolicitados > DIAS_MAX_PRESTAMO) {
            throw new IllegalArgumentException("Los días de préstamo deben estar entre 1 y " + DIAS_MAX_PRESTAMO);
        }
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.codigoEstudiante = Objects.requireNonNull(codigoEstudiante, "El código no puede ser nulo");
        this.nombreEstudiante = Objects.requireNonNull(nombreEstudiante, "El nombre no puede ser nulo");
        this.equipoId = Objects.requireNonNull(equipoId, "El ID del equipo no puede ser nulo");
        this.nombreEquipo = Objects.requireNonNull(nombreEquipo, "El nombre del equipo no puede ser nulo");
        this.fechaPrestamo = Objects.requireNonNull(fechaPrestamo, "La fecha de préstamo no puede ser nula");
        this.fechaLimite = this.fechaPrestamo.plusDays(diasSolicitados);
        this.devuelto = false;
        this.fechaRealDevolucion = null;
    }

    public void extenderPlazo(int diasAdicionales) {
        if (this.devuelto) {
            throw new IllegalStateException("No se puede extender un préstamo que ya ha sido devuelto.");
        }
        if (diasAdicionales <= 0 || diasAdicionales > DIAS_MAX_EXTENSION) {
            throw new IllegalArgumentException("La extensión debe estar comprendida entre 1 y " + DIAS_MAX_EXTENSION + " días.");
        }
        this.fechaLimite = this.fechaLimite.plusDays(diasAdicionales);
    }

    public void registrarDevolucion(LocalDate fechaDevolucion) {
        if (this.devuelto) {
            throw new IllegalStateException("El equipo ya se encuentra registrado como devuelto.");
        }
        this.devuelto = true;
        this.fechaRealDevolucion = Objects.requireNonNull(fechaDevolucion, "La fecha de devolución no puede ser nula");
    }

    public EstadoPrestamo calcularEstado(LocalDate fechaReferencia) {
        if (devuelto) {
            return EstadoPrestamo.DEVUELTO;
        }
        if (fechaReferencia.isAfter(fechaLimite)) {
            return EstadoPrestamo.VENCIDO;
        }
        long diasRestantes = ChronoUnit.DAYS.between(fechaReferencia, fechaLimite);
        if (diasRestantes <= DIAS_ALERTA_POR_VENCER) {
            return EstadoPrestamo.POR_VENCER;
        }
        return EstadoPrestamo.AL_DIA;
    }

    public long obtenerDiasRestantes(LocalDate fechaReferencia) {
        if (devuelto) {
            return 0;
        }
        return ChronoUnit.DAYS.between(fechaReferencia, fechaLimite);
    }

    // Getters
    public String getId() { return id; }
    public String getCodigoEstudiante() { return codigoEstudiante; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public String getEquipoId() { return equipoId; }
    public String getNombreEquipo() { return nombreEquipo; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaLimite() { return fechaLimite; }
    public LocalDate getFechaRealDevolucion() { return fechaRealDevolucion; }
    public boolean isDevuelto() { return devuelto; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prestamo prestamo = (Prestamo) o;
        return Objects.equals(id, prestamo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

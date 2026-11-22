package umb.pms.logica;

import umb.pms.modelo.EstadoPrestamo;
import umb.pms.modelo.Prestamo;
import umb.pms.excepciones.PrestamoException;
import umb.pms.persistencia.RepositorioPrestamos;
import umb.pms.patrones.observer.ObservadorPrestamos;
import umb.pms.patrones.observer.EventoPrestamo;
import umb.pms.patrones.factory.FabricaPrestamos;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador de Negocio central del sistema. Implementa DIP, Observer y coordinación GoF.
 */
public class GestorPrestamos {
    private final RepositorioPrestamos repositorio;
    private final List<Prestamo> prestamos;
    private final List<ObservadorPrestamos> observadores;

    public GestorPrestamos(RepositorioPrestamos repositorio) {
        this.repositorio = Objects.requireNonNull(repositorio, "El repositorio no puede ser nulo");
        this.observadores = new ArrayList<>();
        this.prestamos = new ArrayList<>();
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        try {
            this.prestamos.addAll(repositorio.cargarTodos());
        } catch (PrestamoException e) {
            notificarObservadores(EventoPrestamo.ERROR_PERSISTENCIA, "Error al cargar datos: " + e.getMessage());
        }
    }

    public synchronized void registrarObservador(ObservadorPrestamos obs) {
        if (obs != null && !observadores.contains(obs)) {
            observadores.add(obs);
        }
    }

    private void notificarObservadores(EventoPrestamo evento, String mensaje) {
        for (ObservadorPrestamos obs : observadores) {
            obs.alCambiarEstadoPrestamos(evento, mensaje);
        }
    }

    public synchronized void registrarPrestamo(String codigoEst, String nombreEst, String equipoId, 
                                              String nombreEquipo, int dias) throws PrestamoException {
        boolean equipoOcupado = prestamos.stream()
                .anyMatch(p -> p.getEquipoId().equalsIgnoreCase(equipoId.trim()) && !p.isDevuelto());

        if (equipoOcupado) {
            throw new PrestamoException("El equipo con ID [" + equipoId + "] ya posee un préstamo activo.");
        }

        Prestamo nuevo = FabricaPrestamos.crearPrestamo(codigoEst, nombreEst, equipoId, nombreEquipo, dias);
        prestamos.add(nuevo);
        guardarYNotificar("Préstamo registrado exitosamente para ID: " + nuevo.getId());
    }

    public synchronized void extenderPrestamo(String idPrestamo, int diasExtens) throws PrestamoException {
        Prestamo p = buscarPorId(idPrestamo);
        try {
            p.extenderPlazo(diasExtens);
            guardarYNotificar("Plazo extendido exitosamente en " + diasExtens + " días.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new PrestamoException(e.getMessage(), e);
        }
    }

    public synchronized void registrarDevolucion(String idPrestamo) throws PrestamoException {
        Prestamo p = buscarPorId(idPrestamo);
        try {
            p.registrarDevolucion(LocalDate.now());
            guardarYNotificar("Devolución registrada exitosamente.");
        } catch (IllegalStateException e) {
            throw new PrestamoException(e.getMessage(), e);
        }
    }

    private Prestamo buscarPorId(String id) throws PrestamoException {
        return prestamos.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElseThrow(() -> new PrestamoException("No se encontró el préstamo con ID: " + id));
    }

    private void guardarYNotificar(String mensajeExito) throws PrestamoException {
        try {
            repositorio.guardarTodos(prestamos);
            notificarObservadores(EventoPrestamo.DATOS_ACTUALIZADOS, mensajeExito);
        } catch (PrestamoException e) {
            notificarObservadores(EventoPrestamo.ERROR_PERSISTENCIA, "Fallo al guardar en disco: " + e.getMessage());
            throw e;
        }
    }

    public List<Prestamo> getTodosLosPrestamos() {
        return Collections.unmodifiableList(new ArrayList<>(prestamos));
    }

    public long contarPorEstado(EstadoPrestamo estado) {
        LocalDate hoy = LocalDate.now();
        return prestamos.stream()
                .filter(p -> p.calcularEstado(hoy) == estado)
                .count();
    }
}
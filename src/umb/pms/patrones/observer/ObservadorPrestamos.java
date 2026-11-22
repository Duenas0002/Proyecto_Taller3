package umb.pms.patrones.observer;

public interface ObservadorPrestamos {
    void alCambiarEstadoPrestamos(EventoPrestamo evento, String mensaje);
}

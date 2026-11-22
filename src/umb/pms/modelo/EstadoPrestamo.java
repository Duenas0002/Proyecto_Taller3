package umb.pms.modelo;

/**
 * Enumeración que define los estados posibles de un préstamo dentro del ciclo de vida del sistema.
 */
public enum EstadoPrestamo {
    AL_DIA("Al Día"),
    POR_VENCER("Por Vencer"),
    VENCIDO("Vencido"),
    DEVUELTO("Devuelto");

    private final String etiqueta;

    EstadoPrestamo(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}

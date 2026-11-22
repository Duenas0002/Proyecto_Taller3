package umb.pms.excepciones;

public class PrestamoException extends Exception {
    public PrestamoException(String mensaje) {
        super(mensaje);
    }

    public PrestamoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

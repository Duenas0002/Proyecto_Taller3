import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

public class Prestamo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String nombreEstudiante;
    private String equipo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    public Prestamo(String nombreEstudiante, String equipo, int diasPrestamo) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.nombreEstudiante = nombreEstudiante;
        this.equipo = equipo;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucion = LocalDate.now().plusDays(diasPrestamo);
    }


    public void extenderTiempo(int diasExtra) {
        this.fechaDevolucion = this.fechaDevolucion.plusDays(diasExtra);
    }

    // Getters
    public String getId() { return id; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public String getEquipo() { return equipo; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
}
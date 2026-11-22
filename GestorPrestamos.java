import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorPrestamos {
    private List<Prestamo> listaPrestamos;
    private final String ARCHIVO_DATOS = "prestamos.dat";

    public GestorPrestamos() {
        listaPrestamos = new ArrayList<>();
        cargarDatos();
    }

    // CREATE
    public void registrarPrestamo(Prestamo prestamo) {
        listaPrestamos.add(prestamo);
        guardarDatos();
    }

    // READ
    public List<Prestamo> obtenerTodos() {
        return listaPrestamos;
    }

    // UPDATE 
    public boolean extenderPrestamo(String id, int diasExtra) {
        for (Prestamo p : listaPrestamos) {
            if (p.getId().equals(id)) {
                p.extenderTiempo(diasExtra);
                guardarDatos();
                return true;
            }
        }
        return false;
    }

    // DELETE 
    public boolean eliminarPrestamo(String id) {
        boolean eliminado = listaPrestamos.removeIf(p -> p.getId().equals(id));
        if (eliminado) {
            guardarDatos();
        }
        return eliminado;
    }

    // Guardar
    private void guardarDatos() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DATOS))) {
            out.writeObject(listaPrestamos);
        } catch (IOException e) {
            System.err.println("Error al guardar los datos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarDatos() {
        File archivo = new File(ARCHIVO_DATOS);
        if (archivo.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(archivo))) {
                listaPrestamos = (List<Prestamo>) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error al cargar los datos: " + e.getMessage());
            }
        }
    }
}
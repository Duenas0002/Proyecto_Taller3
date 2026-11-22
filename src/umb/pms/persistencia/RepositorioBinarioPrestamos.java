package umb.pms.persistencia;

import umb.pms.modelo.Prestamo;
import umb.pms.excepciones.PrestamoException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación binaria de la persistencia con tolerancia a fallos mediante respaldo .bak.
 */
public class RepositorioBinarioPrestamos implements RepositorioPrestamos {
    private final File archivoPrincipal;
    private final File archivoRespaldo;

    public RepositorioBinarioPrestamos(String rutaArchivo) {
        this.archivoPrincipal = new File(rutaArchivo);
        this.archivoRespaldo = new File(rutaArchivo + ".bak");
    }

    @Override
    public synchronized void guardarTodos(List<Prestamo> prestamos) throws PrestamoException {
        if (archivoPrincipal.exists()) {
            try {
                Files.copy(archivoPrincipal.toPath(), archivoRespaldo.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("Advertencia: No se pudo actualizar el archivo de respaldo .bak: " + e.getMessage());
            }
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivoPrincipal))) {
            oos.writeObject(new ArrayList<>(prestamos));
        } catch (IOException e) {
            throw new PrestamoException("Error crítico al guardar los datos en disco.", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public synchronized List<Prestamo> cargarTodos() throws PrestamoException {
        if (!archivoPrincipal.exists()) {
            if (archivoRespaldo.exists()) {
                return cargarDesdeArchivo(archivoRespaldo);
            }
            return new ArrayList<>();
        }

        try {
            return cargarDesdeArchivo(archivoPrincipal);
        } catch (PrestamoException e) {
            System.err.println("Archivo principal corrupto. Intentando recuperar desde archivo de respaldo .bak...");
            if (archivoRespaldo.exists()) {
                List<Prestamo> recuperados = cargarDesdeArchivo(archivoRespaldo);
                guardarTodos(recuperados);
                return recuperados;
            }
            throw new PrestamoException("No se pudo cargar la información y no existe un respaldo válido.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Prestamo> cargarDesdeArchivo(File archivo) throws PrestamoException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Prestamo>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new PrestamoException("Error de lectura en el archivo: " + archivo.getName(), e);
        }
    }
}

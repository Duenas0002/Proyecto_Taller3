package umb.pms.utilidades;

import umb.pms.modelo.Prestamo;
import java.io.File;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

/**
 * Utilidad encargada de exportar el estado actual de préstamos a formato CSV.
 */
public class ExportadorCSV {
    public static void exportar(File archivoDestino, List<Prestamo> prestamos) throws Exception {
        try (PrintWriter writer = new PrintWriter(archivoDestino)) {
            writer.println("ID,CodigoEstudiante,NombreEstudiante,EquipoID,NombreEquipo,FechaPrestamo,FechaLimite,Devuelto,FechaRealDevolucion,Estado");
            LocalDate hoy = LocalDate.now();
            for (Prestamo p : prestamos) {
                StringBuilder sb = new StringBuilder();
                sb.append(p.getId()).append(",");
                sb.append(escapar(p.getCodigoEstudiante())).append(",");
                sb.append(escapar(p.getNombreEstudiante())).append(",");
                sb.append(escapar(p.getEquipoId())).append(",");
                sb.append(escapar(p.getNombreEquipo())).append(",");
                sb.append(p.getFechaPrestamo()).append(",");
                sb.append(p.getFechaLimite()).append(",");
                sb.append(p.isDevuelto()).append(",");
                sb.append(p.getFechaRealDevolucion() != null ? p.getFechaRealDevolucion() : "N/A").append(",");
                sb.append(p.calcularEstado(hoy).getEtiqueta());
                writer.println(sb.toString());
            }
        }
    }

    private static String escapar(String texto) {
        if (texto.contains(",") || texto.contains("\"")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }
}

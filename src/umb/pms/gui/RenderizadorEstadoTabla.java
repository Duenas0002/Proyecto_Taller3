package umb.pms.gui;

import umb.pms.modelo.EstadoPrestamo;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Renderizador personalizado para colorear filas según el semáforo de estado (Clean Code + UX).
 */
public class RenderizadorEstadoTabla extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                   boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            String estadoStr = (String) table.getValueAt(row, 7); // Columna de Estado
            if (EstadoPrestamo.VENCIDO.getEtiqueta().equals(estadoStr)) {
                c.setBackground(new Color(254, 226, 226)); // Rojo claro
                c.setForeground(new Color(153, 27, 27));
            } else if (EstadoPrestamo.POR_VENCER.getEtiqueta().equals(estadoStr)) {
                c.setBackground(new Color(254, 243, 199)); // Amarillo claro
                c.setForeground(new Color(146, 64, 14));
            } else if (EstadoPrestamo.DEVUELTO.getEtiqueta().equals(estadoStr)) {
                c.setBackground(new Color(241, 245, 249)); // Gris claro
                c.setForeground(new Color(71, 85, 105));
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(new Color(30, 41, 59));
            }
        }
        return c;
    }
}

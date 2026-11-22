package umb.pms;

import umb.pms.gui.VentanaPrestamos;
import umb.pms.logica.GestorPrestamos;
import umb.pms.persistencia.RepositorioBinarioPrestamos;
import umb.pms.persistencia.RepositorioPrestamos;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RepositorioPrestamos repo = new RepositorioBinarioPrestamos("prestamos.dat");
            GestorPrestamos gestor = new GestorPrestamos(repo);
            VentanaPrestamos ventana = new VentanaPrestamos(gestor);
            ventana.setVisible(true);
        });
    }
}
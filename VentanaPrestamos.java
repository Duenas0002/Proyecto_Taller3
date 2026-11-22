import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaPrestamos extends JFrame {
    private GestorPrestamos gestor;
    
    private JTextField txtEstudiante;
    private JTextField txtEquipo;
    private JSpinner spinDias;
    private JTable tabla;
    private DefaultTableModel modeloTabla;

    public VentanaPrestamos() {
        gestor = new GestorPrestamos();
        configurarVentana();
        inicializarComponentes();
        actualizarTabla();
    }

    private void configurarVentana() {
        setTitle("Gestión de Préstamos - Laboratorio");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes() {
        // La vaina esa pa meter datos
        JPanel panelFormulario = new JPanel(new FlowLayout());
        
        panelFormulario.add(new JLabel("Estudiante:"));
        txtEstudiante = new JTextField(15);
        panelFormulario.add(txtEstudiante);
        
        panelFormulario.add(new JLabel("Equipo:"));
        txtEquipo = new JTextField(15);
        panelFormulario.add(txtEquipo);
        
        panelFormulario.add(new JLabel("Días:"));
        spinDias = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        panelFormulario.add(spinDias);
        
        JButton btnPrestar = new JButton("Registrar Préstamo");
        btnPrestar.addActionListener(e -> registrarNuevoPrestamo());
        panelFormulario.add(btnPrestar);
        
        add(panelFormulario, BorderLayout.NORTH);

        // Psnrl central
        String[] columnas = {"ID", "Estudiante", "Equipo", "Fecha Préstamo", "Fecha Devolución"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabla = new JTable(modeloTabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Actualizar y terminar
        JPanel panelAcciones = new JPanel(new FlowLayout());
        
        JButton btnExtender = new JButton("Extender Tiempo");
        btnExtender.addActionListener(e -> extenderPrestamoSeleccionado());
        
        JButton btnDevolver = new JButton("Registrar Devolución (Eliminar)");
        btnDevolver.addActionListener(e -> eliminarPrestamoSeleccionado());
        
        panelAcciones.add(btnExtender);
        panelAcciones.add(btnDevolver);
        add(panelAcciones, BorderLayout.SOUTH);
    }

    private void registrarNuevoPrestamo() {
        String estudiante = txtEstudiante.getText().trim();
        String equipo = txtEquipo.getText().trim();
        int dias = (int) spinDias.getValue();

        if (estudiante.isEmpty() || equipo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor llene todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Prestamo nuevo = new Prestamo(estudiante, equipo, dias);
        gestor.registrarPrestamo(nuevo);
        
        txtEstudiante.setText("");
        txtEquipo.setText("");
        spinDias.setValue(1);
        actualizarTabla();
    }

    private void extenderPrestamoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo de la tabla.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloTabla.getValueAt(fila, 0);
        String input = JOptionPane.showInputDialog(this, "¿Cuántos días extra desea agregar?", "Extender Préstamo", JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int diasExtra = Integer.parseInt(input);
                if (gestor.extenderPrestamo(id, diasExtra)) {
                    actualizarTabla();
                    JOptionPane.showMessageDialog(this, "Tiempo extendido exitosamente.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarPrestamoSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo para devolver.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) modeloTabla.getValueAt(fila, 0);
        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Confirmar la devolución del equipo?\nEsto eliminará el registro.", "Confirmar Devolución", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (gestor.eliminarPrestamo(id)) {
                actualizarTabla();
            }
        }
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Prestamo> prestamos = gestor.obtenerTodos();
        for (Prestamo p : prestamos) {
            Object[] fila = {
                p.getId(),
                p.getNombreEstudiante(),
                p.getEquipo(),
                p.getFechaPrestamo().toString(),
                p.getFechaDevolucion().toString()
            };
            modeloTabla.addRow(fila);
        }
    }

    public static void main(String[] args) {
        // Ma quillar la vaina
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new VentanaPrestamos().setVisible(true);
        });
    }
}
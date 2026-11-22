package umb.pms.gui;

import umb.pms.logica.GestorPrestamos;
import umb.pms.modelo.EstadoPrestamo;
import umb.pms.modelo.Prestamo;
import umb.pms.excepciones.PrestamoException;
import umb.pms.patrones.observer.ObservadorPrestamos;
import umb.pms.patrones.observer.EventoPrestamo;
import umb.pms.patrones.strategy.*;
import umb.pms.utilidades.ExportadorCSV;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz Gráfica Principal Swing refactorizada bajo patrones Observer y Strategy.
 */
public class VentanaPrestamos extends JFrame implements ObservadorPrestamos {
    private final GestorPrestamos gestor;
    private DefaultTableModel modeloTabla;
    private JTable tablaPrestamos;
    private TableRowSorter<DefaultTableModel> sorter;

    private JLabel lblActivos, lblVencidos, lblPorVencer;
    private JTextField txtBuscar;
    private JComboBox<EstrategiaFiltroPrestamos> comboFiltros;

    public VentanaPrestamos(GestorPrestamos gestor) {
        this.gestor = gestor;
        this.gestor.registrarObservador(this);
        initUI();
        actualizarDatos();
    }

    private void initUI() {
        setTitle("Sistema de Control de Préstamos de Equipos - Universidad Manuela Beltrán v2.0");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel Superior: Dashboard de Indicadores
        JPanel panelDashboard = new JPanel(new GridLayout(1, 3, 10, 10));
        panelDashboard.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        lblActivos = crearTarjetaIndicador("Préstamos Activos", new Color(224, 242, 254));
        lblPorVencer = crearTarjetaIndicador("Por Vencer (<= 2 Días)", new Color(254, 243, 199));
        lblVencidos = crearTarjetaIndicador("Vencidos", new Color(254, 226, 226));

        panelDashboard.add(lblActivos);
        panelDashboard.add(lblPorVencer);
        panelDashboard.add(lblVencidos);

        // Panel Central: Filtros, Buscador y Tabla
        JPanel panelCentral = new JPanel(new BorderLayout(5, 5));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel panelHerramientas = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelHerramientas.add(new JLabel("Buscar (Texto):"));
        txtBuscar = new JTextField(15);
        panelHerramientas.add(txtBuscar);

        panelHerramientas.add(new JLabel("Estrategia Filtro:"));
        comboFiltros = new JComboBox<>(new EstrategiaFiltroPrestamos[]{
                new FiltroTodos(), new FiltroActivos(), new FiltroVencidos(), 
                new FiltroPorVencer(), new FiltroHistorial()
        });
        panelHerramientas.add(comboFiltros);

        panelCentral.add(panelHerramientas, BorderLayout.NORTH);

        // Configuración de Tabla
        String[] columnas = {"ID", "Código Est.", "Estudiante", "ID Equipo", "Equipo", "F. Préstamo", "F. Límite", "Estado", "F. Devolución"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaPrestamos = new JTable(modeloTabla);
        sorter = new TableRowSorter<>(modeloTabla);
        tablaPrestamos.setRowSorter(sorter);
        tablaPrestamos.setDefaultRenderer(Object.class, new RenderizadorEstadoTabla());

        panelCentral.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);

        // Panel Inferior: Botones de Acción
        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnNuevo = new JButton("Nuevo Préstamo");
        JButton btnExtender = new JButton("Extender Plazo");
        JButton btnDevolver = new JButton("Registrar Devolución");
        JButton btnExportar = new JButton("Exportar CSV");

        panelAcciones.add(btnNuevo);
        panelAcciones.add(btnExtender);
        panelAcciones.add(btnDevolver);
        panelAcciones.add(btnExportar);

        add(panelDashboard, BorderLayout.NORTH);
        add(panelCentral, BorderLayout.CENTER);
        add(panelAcciones, BorderLayout.SOUTH);

        // Eventos
        comboFiltros.addActionListener(e -> actualizarTabla());
        btnNuevo.addActionListener(e -> mostrarDialogoNuevo());
        btnExtender.addActionListener(e -> accionExtender());
        btnDevolver.addActionListener(e -> accionDevolver());
        btnExportar.addActionListener(e -> accionExportar());

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarBuscador(); }
            public void removeUpdate(DocumentEvent e) { filtrarBuscador(); }
            public void changedUpdate(DocumentEvent e) { filtrarBuscador(); }
        });
    }

    private JLabel crearTarjetaIndicador(String titulo, Color bg) {
        JLabel lbl = new JLabel(titulo + ": 0", SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return lbl;
    }

    private void filtrarBuscador() {
        String text = txtBuscar.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void actualizarDatos() {
        lblActivos.setText("Préstamos Activos: " + gestor.contarPorEstado(EstadoPrestamo.AL_DIA));
        lblPorVencer.setText("Por Vencer: " + gestor.contarPorEstado(EstadoPrestamo.POR_VENCER));
        lblVencidos.setText("Vencidos: " + gestor.contarPorEstado(EstadoPrestamo.VENCIDO));
        actualizarTabla();
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        EstrategiaFiltroPrestamos estrategia = (EstrategiaFiltroPrestamos) comboFiltros.getSelectedItem();
        List<Prestamo> filtrados = estrategia.filtrar(gestor.getTodosLosPrestamos(), LocalDate.now());

        for (Prestamo p : filtrados) {
            modeloTabla.addRow(new Object[]{
                    p.getId(), p.getCodigoEstudiante(), p.getNombreEstudiante(),
                    p.getEquipoId(), p.getNombreEquipo(), p.getFechaPrestamo(),
                    p.getFechaLimite(), p.calcularEstado(LocalDate.now()).getEtiqueta(),
                    p.getFechaRealDevolucion() != null ? p.getFechaRealDevolucion() : "-"
            });
        }
    }

    private void mostrarDialogoNuevo() {
        JTextField txtCod = new JTextField();
        JTextField txtNom = new JTextField();
        JTextField txtEqId = new JTextField();
        JTextField txtEqNom = new JTextField();
        JTextField txtDias = new JTextField("7");

        Object[] message = {
                "Código Estudiante:", txtCod,
                "Nombre Estudiante:", txtNom,
                "ID Equipo (Único):", txtEqId,
                "Nombre Equipo:", txtEqNom,
                "Días Préstamo (1-30):", txtDias
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Registrar Nuevo Préstamo", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int dias = Integer.parseInt(txtDias.getText().trim());
                gestor.registrarPrestamo(txtCod.getText(), txtNom.getText(), txtEqId.getText(), txtEqNom.getText(), dias);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los días deben ser un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (PrestamoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Regla de Negocio", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void accionExtender() {
        int selectedRow = tablaPrestamos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo de la tabla.");
            return;
        }
        String id = (String) modeloTabla.getValueAt(tablaPrestamos.convertRowIndexToModel(selectedRow), 0);
        String input = JOptionPane.showInputDialog(this, "Ingrese días de extensión (1 a 15):");
        if (input != null && !input.trim().isEmpty()) {
            try {
                int dias = Integer.parseInt(input.trim());
                gestor.extenderPrestamo(id, dias);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionDevolver() {
        int selectedRow = tablaPrestamos.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo de la tabla.");
            return;
        }
        String id = (String) modeloTabla.getValueAt(tablaPrestamos.convertRowIndexToModel(selectedRow), 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Confirmar devolución del equipo?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gestor.registrarDevolucion(id);
            } catch (PrestamoException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void accionExportar() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("reporte_prestamos.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                ExportadorCSV.exportar(chooser.getSelectedFile(), gestor.getTodosLosPrestamos());
                JOptionPane.showMessageDialog(this, "Reporte CSV generado exitosamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void alCambiarEstadoPrestamos(EventoPrestamo evento, String mensaje) {
        if (evento == EventoPrestamo.DATOS_ACTUALIZADOS) {
            actualizarDatos();
        } else if (evento == EventoPrestamo.ERROR_PERSISTENCIA) {
            JOptionPane.showMessageDialog(this, mensaje, "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        }
    }
}

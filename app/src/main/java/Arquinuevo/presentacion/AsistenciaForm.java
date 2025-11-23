package Arquinuevo.presentacion;

import Arquinuevo.datos.Asistencia;
import Arquinuevo.datos.adapter.AsistenciaExcelAdapter;
import Arquinuevo.datos.adapter.AsistenciaPDFAdapter;
import Arquinuevo.datos.adapter.DataExportAdapter;
import Arquinuevo.datos.ExportResult;
import Arquinuevo.negocio.AsistenciaCU;
import Arquinuevo.negocio.ExportarAsistenciaCU;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Formulario de Asistencia - Capa de Presentación
 * Demuestra el uso de Strategy Pattern y Adapter Pattern
 */
public class AsistenciaForm extends JFrame {
    private JTextField txtAlumnoId;
    private JTextField txtGrupoId;
    private JTextField txtHora;
    private JButton btnMarcarAsistencia;
    private JButton btnExportarExcel;
    private JButton btnExportarPDF;
    private JTextArea txtAreaResultado;
    private AsistenciaCU asistenciaCU;
    private ExportarAsistenciaCU exportarCU;

    public AsistenciaForm() {
        this.asistenciaCU = new AsistenciaCU();
        this.exportarCU = new ExportarAsistenciaCU();
        inicializarComponentes();
        configurarVentana();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Marcar Asistencia"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Campo Alumno ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("ID Alumno:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtAlumnoId = new JTextField(20);
        panelFormulario.add(txtAlumnoId, gbc);

        // Campo Grupo ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("ID Grupo:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtGrupoId = new JTextField(20);
        panelFormulario.add(txtGrupoId, gbc);

        // Campo Hora
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Hora (HH:mm):"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtHora = new JTextField(20);
        txtHora.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        panelFormulario.add(txtHora, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnMarcarAsistencia = new JButton("Marcar Asistencia");
        btnExportarExcel = new JButton("Exportar a Excel");
        btnExportarPDF = new JButton("Exportar a PDF");
        panelBotones.add(btnMarcarAsistencia);
        panelBotones.add(btnExportarExcel);
        panelBotones.add(btnExportarPDF);

        // Area de resultado
        txtAreaResultado = new JTextArea(15, 40);
        txtAreaResultado.setEditable(false);
        txtAreaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtAreaResultado);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Asistencias y Resultados"));

        // Agregar componentes al frame
        add(panelFormulario, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        // Eventos
        btnMarcarAsistencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                marcarAsistencia();
            }
        });

        btnExportarExcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarAsistencias(new AsistenciaExcelAdapter());
            }
        });

        btnExportarPDF.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportarAsistencias(new AsistenciaPDFAdapter());
            }
        });
    }

    private void configurarVentana() {
        setTitle("Sistema de Asistencia - Strategy y Adapter Pattern");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void marcarAsistencia() {
        try {
            Integer alumnoId = Integer.parseInt(txtAlumnoId.getText().trim());
            Integer grupoId = Integer.parseInt(txtGrupoId.getText().trim());
            String hora = txtHora.getText().trim();
            String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Marcar asistencia (usa Strategy Pattern internamente)
            boolean exito = asistenciaCU.marcarAsistencia(alumnoId, grupoId, fecha, hora);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Asistencia marcada exitosamente!",
                    "Exito",
                    JOptionPane.INFORMATION_MESSAGE);
                actualizarListaAsistencias(grupoId);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al marcar asistencia. Verifique que el alumno esté inscrito.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error de Validacion",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarAsistencias(DataExportAdapter adapter) {
        try {
            Integer grupoId = Integer.parseInt(txtGrupoId.getText().trim());

            // Exportar usando Adapter Pattern
            ExportResult resultado = exportarCU.exportar(grupoId, adapter);

            if (resultado.isSuccess()) {
                // Guardar archivo
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(resultado.getNombreCompleto()));
                int option = fileChooser.showSaveDialog(this);

                if (option == JFileChooser.APPROVE_OPTION) {
                    File archivo = fileChooser.getSelectedFile();
                    try (FileOutputStream fos = new FileOutputStream(archivo)) {
                        fos.write(resultado.getDatos());
                        fos.flush();
                    }

                    JOptionPane.showMessageDialog(this,
                        "Archivo exportado exitosamente!\n" +
                        "Formato: " + resultado.getFormato() + "\n" +
                        "Registros: " + resultado.getCantidadRegistros(),
                        "Exito",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + resultado.getMensajeError(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarListaAsistencias(Integer grupoId) {
        List<Asistencia> asistencias = asistenciaCU.obtenerAsistenciasPorGrupo(grupoId);

        StringBuilder sb = new StringBuilder();
        if (asistencias.isEmpty()) {
            sb.append("No hay asistencias registradas.");
        } else {
            sb.append(String.format("%-5s %-10s %-10s %-12s %-10s %-10s%n",
                "ID", "Alumno", "Grupo", "Fecha", "Hora", "Estado"));
            sb.append("------------------------------------------------------------\n");
            for (Asistencia a : asistencias) {
                sb.append(String.format("%-5d %-10d %-10d %-12s %-10s %-10s%n",
                    a.getId(),
                    a.getAlumnoId(),
                    a.getGrupoId(),
                    a.getFecha(),
                    a.getHoraMarcada() != null ? a.getHoraMarcada() : "",
                    a.getEstado() != null ? a.getEstado() : ""));
            }
        }

        txtAreaResultado.setText(sb.toString());
    }

    public void mostrar() {
        setVisible(true);
        if (!txtGrupoId.getText().trim().isEmpty()) {
            try {
                Integer grupoId = Integer.parseInt(txtGrupoId.getText().trim());
                actualizarListaAsistencias(grupoId);
            } catch (NumberFormatException e) {
                // Ignorar
            }
        }
    }
}


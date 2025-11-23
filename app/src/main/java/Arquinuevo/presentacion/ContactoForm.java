package Arquinuevo.presentacion;

import Arquinuevo.datos.Contacto;
import Arquinuevo.negocio.AdministrarContactoUseCase;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Formulario de Contacto - Capa de Presentación
 * Interfaz gráfica para gestionar contactos
 * Similar a CategoriaActivity del diagrama UML proporcionado
 */
public class ContactoForm extends JFrame {
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JTextField txtBuscar;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private JButton btnLimpiar;
    private JTextArea txtAreaResultado;
    private AdministrarContactoUseCase contactoUseCase;
    private Contacto contactoEditando;

    public ContactoForm() {
        this.contactoUseCase = new AdministrarContactoUseCase();
        this.contactoEditando = null;
        inicializarComponentes();
        configurarVentana();
        configurarEventos();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));

        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Contacto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Campo Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelFormulario.add(txtNombre, gbc);

        // Campo Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtEmail = new JTextField(20);
        panelFormulario.add(txtEmail, gbc);

        // Campo Teléfono
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Telefono:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtTelefono = new JTextField(20);
        panelFormulario.add(txtTelefono, gbc);

        // Campo Buscar
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panelFormulario.add(new JLabel("Buscar:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtBuscar = new JTextField(20);
        panelFormulario.add(txtBuscar, gbc);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        btnLimpiar = new JButton("Limpiar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnLimpiar);

        // Area de resultado
        txtAreaResultado = new JTextArea(10, 30);
        txtAreaResultado.setEditable(false);
        txtAreaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtAreaResultado);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Contactos Guardados"));

        // Agregar componentes al frame
        add(panelFormulario, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void configurarEventos() {
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarContacto();
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelarEdicion();
            }
        });

        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarFormulario();
            }
        });

        // Evento de busqueda en tiempo real
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                buscarContactos(txtBuscar.getText());
            }
        });
    }

    private void configurarVentana() {
        setTitle("Formulario de Contacto - Arquitectura 3 Capas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void guardarContacto() {
        try {
            String nombre = txtNombre.getText().trim();
            String email = txtEmail.getText().trim();
            String telefono = txtTelefono.getText().trim();

            if (contactoEditando == null) {
                // Crear nuevo contacto
                contactoUseCase.crearContacto(nombre, email, telefono);
                JOptionPane.showMessageDialog(this, 
                    "Contacto guardado exitosamente!", 
                    "Exito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Actualizar contacto existente
                contactoUseCase.actualizarContacto(contactoEditando.getId(), nombre, email, telefono);
                JOptionPane.showMessageDialog(this, 
                    "Contacto actualizado exitosamente!", 
                    "Exito", 
                    JOptionPane.INFORMATION_MESSAGE);
                contactoEditando = null;
            }

            limpiarFormulario();
            cargarContactos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + e.getMessage(), 
                "Error de Validacion", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelarEdicion() {
        contactoEditando = null;
        limpiarFormulario();
        cargarContactos();
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        txtBuscar.setText("");
        contactoEditando = null;
        txtNombre.requestFocus();
    }

    private void cargarContactos() {
        List<Contacto> contactos = contactoUseCase.obtenerContactos();
        mostrarContactos(contactos);
    }

    private void buscarContactos(String criterio) {
        List<Contacto> contactos = contactoUseCase.buscarContactos(criterio);
        mostrarContactos(contactos);
    }

    private void mostrarContactos(List<Contacto> contactos) {
        StringBuilder sb = new StringBuilder();
        
        if (contactos.isEmpty()) {
            sb.append("No hay contactos guardados.");
        } else {
            sb.append(String.format("%-5s %-20s %-25s %-15s%n", 
                "ID", "Nombre", "Email", "Telefono"));
            sb.append("------------------------------------------------------------\n");
            for (Contacto c : contactos) {
                sb.append(String.format("%-5d %-20s %-25s %-15s%n", 
                    c.getId(), 
                    c.getNombre(), 
                    c.getEmail(), 
                    c.getTelefono()));
            }
        }
        
        txtAreaResultado.setText(sb.toString());
    }

    public void mostrar() {
        setVisible(true);
        cargarContactos();
    }
}


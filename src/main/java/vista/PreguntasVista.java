package vista;

import database.preguntas_DB;
import modelo.preguntas;
import controlador.sesion;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class PreguntasVista extends JFrame {

    // ==========================================
    // COMPONENTES
    // ==========================================

    private JTable tablaPreguntas;

    private JTextField txtTitulo;
    private JTextArea txtContenido;
    private JTextField txtUsuario;

    private JButton btnNuevo;
    private JButton btnGuardar;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizar;

    // ==========================================
    // BASE DE DATOS
    // ==========================================

    private preguntas_DB preguntasDB;
    private final boolean soloMias;

    // ==========================================
    // ID DE LA PREGUNTA SELECCIONADA
    // ==========================================

    private int idPreguntaSeleccionada = -1;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public PreguntasVista() {
        this(false);
    }

    public PreguntasVista(boolean soloMias) {
        this.soloMias = soloMias;
        preguntasDB = new preguntas_DB();

        if (!sesion.haySesion()) {
            JOptionPane.showMessageDialog(null, "Inicia sesión para gestionar preguntas.");
            dispose();
            return;
        }

        configurarVentana();

        crearComponentes();

        cargarPreguntas();
    }

    // ==========================================
    // CONFIGURAR VENTANA
    // ==========================================

    private void configurarVentana() {

        setTitle(soloMias ? "Mis preguntas" : "Preguntas del foro");

        setSize(900, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));
    }

    // ==========================================
    // CREAR COMPONENTES
    // ==========================================

    private void crearComponentes() {

        // ======================================
        // TABLA
        // ======================================

        String[] columnas = {
            "ID",
            "Título",
            "Contenido",
            "Usuario"
        };

        DefaultTableModel modeloTabla =
                new DefaultTableModel(columnas, 0) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column) {

                        return false;
                    }
                };

        tablaPreguntas = new JTable(modeloTabla);

        tablaPreguntas.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JScrollPane scrollTabla =
                new JScrollPane(tablaPreguntas);

        add(
                scrollTabla,
                BorderLayout.CENTER
        );

        // ======================================
        // FORMULARIO
        // ======================================

        JPanel panelFormulario =
                new JPanel(
                        new GridLayout(3, 2, 10, 10)
                );

        panelFormulario.setBorder(
                BorderFactory.createTitledBorder(
                        "Información de la pregunta"
                )
        );

        // TÍTULO

        panelFormulario.add(
                new JLabel("Título:")
        );

        txtTitulo = new JTextField();

        panelFormulario.add(txtTitulo);

        // CONTENIDO

        panelFormulario.add(
                new JLabel("Contenido:")
        );

        txtContenido = new JTextArea();

        txtContenido.setLineWrap(true);

        txtContenido.setWrapStyleWord(true);

        JScrollPane scrollContenido =
                new JScrollPane(txtContenido);

        panelFormulario.add(scrollContenido);

        // USUARIO

        panelFormulario.add(
                new JLabel("ID Usuario:")
        );

        txtUsuario = new JTextField();
        txtUsuario.setEditable(false);

        panelFormulario.add(txtUsuario);

        add(
                panelFormulario,
                BorderLayout.NORTH
        );

        // ======================================
        // BOTONES
        // ======================================

        JPanel panelBotones = new JPanel();

        btnNuevo =
                new JButton("Nuevo");

        btnGuardar =
                new JButton("Guardar");

        btnEditar =
                new JButton("Editar");

        btnEliminar =
                new JButton("Eliminar");

        btnActualizar =
                new JButton("Actualizar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        add(
                panelBotones,
                BorderLayout.SOUTH
        );

        // ======================================
        // EVENTOS
        // ======================================

        btnNuevo.addActionListener(
                e -> nuevo()
        );

        btnGuardar.addActionListener(
                e -> guardar()
        );

        btnEditar.addActionListener(
                e -> editar()
        );

        btnEliminar.addActionListener(
                e -> eliminar()
        );

        btnActualizar.addActionListener(
                e -> cargarPreguntas()
        );

        // ======================================
        // SELECCIÓN DE TABLA
        // ======================================

        tablaPreguntas
                .getSelectionModel()
                .addListSelectionListener(e -> {

                    if (!e.getValueIsAdjusting()) {

                        cargarSeleccion();
                    }
                });
    }

    // ==========================================
    // CARGAR PREGUNTAS
    // ==========================================

    private void cargarPreguntas() {

        DefaultTableModel modeloTabla =
                (DefaultTableModel)
                        tablaPreguntas.getModel();

        modeloTabla.setRowCount(0);

        try {

            List<preguntas> lista = soloMias
                    ? preguntasDB.obtenerPreguntasUsuario(sesion.getIdUsuario())
                    : preguntasDB.obtenerPreguntas();

            for (preguntas pregunta : lista) {

                modeloTabla.addRow(
                        new Object[]{
                            pregunta.getIdPregunta(),
                            pregunta.getTitulo(),
                            pregunta.getContenido(),
                pregunta.getIdUsuario()
                        }
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al cargar las preguntas:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        limpiarCampos();
    }

    // ==========================================
    // CARGAR PREGUNTA SELECCIONADA
    // ==========================================

    private void cargarSeleccion() {

        int fila =
                tablaPreguntas.getSelectedRow();

        if (fila == -1) {

            return;
        }

        idPreguntaSeleccionada =
                Integer.parseInt(
                        tablaPreguntas
                                .getValueAt(fila, 0)
                                .toString()
                );

        txtTitulo.setText(
                tablaPreguntas
                        .getValueAt(fila, 1)
                        .toString()
        );

        txtContenido.setText(
                tablaPreguntas
                        .getValueAt(fila, 2)
                        .toString()
        );

        txtUsuario.setText(
                tablaPreguntas
                        .getValueAt(fila, 3)
                        .toString()
        );
    }

    // ==========================================
    // NUEVO
    // ==========================================

    private void nuevo() {

        limpiarCampos();

        txtTitulo.requestFocus();
    }

    // ==========================================
    // GUARDAR
    // ==========================================

    private void guardar() {

        String titulo =
                txtTitulo.getText().trim();

        String contenido =
                txtContenido.getText().trim();


        // VALIDAR CAMPOS

        if (titulo.isEmpty() || contenido.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Todos los campos son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // INSERTAR

        try {

            boolean resultado =
                    preguntasDB.insertarPregunta(
                            titulo,
                            contenido,
                            sesion.getIdUsuario()
                    );

            if (resultado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Pregunta guardada correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

                cargarPreguntas();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo guardar la pregunta.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al guardar:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==========================================
    // EDITAR
    // ==========================================

    private void editar() {

        if (idPreguntaSeleccionada == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una pregunta de la tabla.",
                    "Pregunta no seleccionada",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String titulo =
                txtTitulo.getText().trim();

        String contenido =
                txtContenido.getText().trim();

        if (titulo.isEmpty()
                || contenido.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Título y contenido son obligatorios.",
                    "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            boolean resultado =
                    preguntasDB.actualizarPregunta(
                            idPreguntaSeleccionada,
                            titulo,
                            contenido,
                            sesion.getIdUsuario()
                    );

            if (resultado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Pregunta actualizada correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

                cargarPreguntas();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo actualizar. Solo puedes modificar preguntas tuyas.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al actualizar:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==========================================
    // ELIMINAR
    // ==========================================

    private void eliminar() {

        if (idPreguntaSeleccionada == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Seleccione una pregunta de la tabla.",
                    "Pregunta no seleccionada",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int confirmar =
                JOptionPane.showConfirmDialog(
                        this,
                        "¿Desea eliminar esta pregunta?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmar != JOptionPane.YES_OPTION) {

            return;
        }

        try {

            boolean resultado =
                    preguntasDB.eliminarPregunta(
                            idPreguntaSeleccionada,
                            sesion.getIdUsuario()
                    );

            if (resultado) {

                JOptionPane.showMessageDialog(
                        this,
                        "Pregunta eliminada correctamente.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );

                cargarPreguntas();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo eliminar. Solo puedes borrar preguntas tuyas.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al eliminar:\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // ==========================================
    // LIMPIAR CAMPOS
    // ==========================================

    private void limpiarCampos() {

        idPreguntaSeleccionada = -1;

        txtTitulo.setText("");

        txtContenido.setText("");

        txtUsuario.setText("");

        if (tablaPreguntas != null) {

            tablaPreguntas.clearSelection();
        }
    }

    // ==========================================
    // MAIN
    // ==========================================

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {

            PreguntasVista ventana =
                    new PreguntasVista();

            ventana.setVisible(true);
        });
    }
}

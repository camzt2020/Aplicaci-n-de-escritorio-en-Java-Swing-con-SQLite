package vista;

import controlador.sesion;
import database.respuesta_DB;
import database.user_DB;
import modelo.preguntas;
import modelo.respuestas;
import modelo.user;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.*;

/** Vista de respuestas y formulario para publicar una respuesta. */
public class RespuestasVista extends JFrame {
    private final preguntas pregunta;
    private final respuesta_DB respuestasDB = new respuesta_DB();
    private final JTextArea lista = new JTextArea();
    private final JTextArea nueva = new JTextArea(4, 40);

    public RespuestasVista(preguntas pregunta) {
        this.pregunta = pregunta;
        setTitle("Respuestas - " + pregunta.getTitulo());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JLabel encabezado = new JLabel("Pregunta: " + pregunta.getTitulo());
        encabezado.setBorder(BorderFactory.createEmptyBorder(10, 10, 4, 10));
        add(encabezado, BorderLayout.NORTH);

        lista.setEditable(false);
        lista.setLineWrap(true);
        lista.setWrapStyleWord(true);
        add(new JScrollPane(lista), BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(5, 5));
        pie.setBorder(BorderFactory.createTitledBorder("Tu respuesta"));
        nueva.setLineWrap(true);
        nueva.setWrapStyleWord(true);
        pie.add(new JScrollPane(nueva), BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton publicar = new JButton("Publicar");
        JButton cerrar = new JButton("Cerrar");
        botones.add(publicar);
        botones.add(cerrar);
        pie.add(botones, BorderLayout.SOUTH);
        add(pie, BorderLayout.SOUTH);
        publicar.addActionListener(e -> publicar());
        cerrar.addActionListener(e -> dispose());
        cargarRespuestas();
    }

    private void cargarRespuestas() {
        try {
            StringBuilder texto = new StringBuilder();
            for (respuestas respuesta : respuestasDB.obtenerRespuestas(pregunta.getIdPregunta())) {
                user autor = new user_DB().obtenerUsuario(respuesta.getIdUsuario());
                texto.append(autor == null ? "Usuario" : autor.getNombre())
                        .append(":\n").append(respuesta.getContenido()).append("\n\n")
                        .append("────────────────────────────────\n\n");
            }
            lista.setText(texto.length() == 0 ? "Sé la primera persona en responder.\n" : texto.toString());
            lista.setCaretPosition(0);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al cargar respuestas", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void publicar() {
        if (!sesion.haySesion()) {
            JOptionPane.showMessageDialog(this, "Inicia sesión para responder.");
            return;
        }
        String contenido = nueva.getText().trim();
        if (contenido.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe una respuesta antes de publicar.");
            return;
        }
        try {
          if (respuestasDB.insertarRespuesta(contenido, pregunta.getIdPregunta(), sesion.getIdUsuario())) {
            nueva.setText("");
            cargarRespuestas();
          } else {
            JOptionPane.showMessageDialog(this, "No se pudo publicar la respuesta.", "Error", JOptionPane.ERROR_MESSAGE);
          }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error al publicar", JOptionPane.ERROR_MESSAGE);
        }
    }
}

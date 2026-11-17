package database;

import controlador.conexion;
import modelo.preguntas;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class preguntas_DB {

    public List<preguntas> obtenerPreguntas() {

        List<preguntas> listaPreguntas =
                new ArrayList<>();

        String sql =
                "SELECT id_pregunta, titulo, contenido, id_usuario "
                + "FROM preguntas";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql);
            ResultSet rs =
                    ps.executeQuery()
        ) {

            while (rs.next()) {

                preguntas pregunta =
                        new preguntas();

                pregunta.setIdPregunta(
                        rs.getInt("id_pregunta")
                );

                pregunta.setTitulo(
                        rs.getString("titulo")
                );

                pregunta.setContenido(
                        rs.getString("contenido")
                );

                pregunta.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                listaPreguntas.add(pregunta);
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar preguntas: " + e.getMessage(), e);
        }

        return listaPreguntas;
    }

   
    public List<preguntas> obtenerPreguntasUsuario(
            int idUsuario) {

        List<preguntas> listaPreguntas =
                new ArrayList<>();

        String sql =
                "SELECT id_pregunta, titulo, contenido, id_usuario "
                + "FROM preguntas "
                + "WHERE id_usuario = ?";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idUsuario);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                preguntas pregunta =
                        new preguntas();

                pregunta.setIdPregunta(
                        rs.getInt("id_pregunta")
                );

                pregunta.setTitulo(
                        rs.getString("titulo")
                );

                pregunta.setContenido(
                        rs.getString("contenido")
                );

                pregunta.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                listaPreguntas.add(pregunta);
            }

            rs.close();

        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar tus preguntas: " + e.getMessage(), e);
        }

        return listaPreguntas;
    }

    
    public boolean insertarPregunta(
            String titulo,
            String contenido,
            int idUsuario) {

        String sql =
                "INSERT INTO preguntas "
                + "(titulo, contenido, id_usuario) "
                + "VALUES (?, ?, ?)";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, titulo);
            ps.setString(2, contenido);
            ps.setInt(3, idUsuario);

            int resultado =
                    ps.executeUpdate();

            return resultado > 0;

        } catch (SQLException e) {
            throw new IllegalStateException("Error al crear la pregunta: " + e.getMessage(), e);
        }
    }

    public boolean actualizarPregunta(
            int idPregunta,
            String titulo,
            String contenido,
            int idUsuario) {

        String sql =
                "UPDATE preguntas "
                + "SET titulo = ?, contenido = ? "
                + "WHERE id_pregunta = ? "
                + "AND id_usuario = ?";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, titulo);
            ps.setString(2, contenido);
            ps.setInt(3, idPregunta);
            ps.setInt(4, idUsuario);

            int resultado =
                    ps.executeUpdate();

            return resultado > 0;

        } catch (SQLException e) {
            throw new IllegalStateException("Error al actualizar la pregunta: " + e.getMessage(), e);
        }
    }

   
    public boolean eliminarPregunta(
            int idPregunta,
            int idUsuario) {

        String sql =
                "DELETE FROM preguntas "
                + "WHERE id_pregunta = ? "
                + "AND id_usuario = ?";

        try (Connection conn = conexion.conectar()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM respuestas WHERE id_pregunta = ?")) {
                    ps.setInt(1, idPregunta);
                    ps.executeUpdate();
                }
                // También limpia las respuestas que venían de la tabla antigua singular.
                if (tablaExiste(conn, "respuesta")) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM respuesta WHERE ID_pregunta = ?")) {
                        ps.setInt(1, idPregunta);
                        ps.executeUpdate();
                    }
                }
                int resultado;
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, idPregunta);
                    ps.setInt(2, idUsuario);
                    resultado = ps.executeUpdate();
                }
                if (resultado > 0) conn.commit(); else conn.rollback();
                return resultado > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Error al eliminar la pregunta: " + e.getMessage(), e);
        }
    }

    private boolean tablaExiste(Connection conn, String nombre) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 FROM sqlite_master WHERE type='table' AND name=?")) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }
}

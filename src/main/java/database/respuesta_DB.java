package database;

import controlador.conexion;
import modelo.respuestas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class respuesta_DB {

    
    public List<respuestas> obtenerRespuestas(
            int idPregunta) {

        List<respuestas> lista =
                new ArrayList<>();

        String sql =
                "SELECT id_respuesta, contenido, "
                + "id_pregunta, id_usuario "
                + "FROM respuestas "
                + "WHERE id_pregunta = ? "
                + "ORDER BY id_respuesta ASC";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setInt(1, idPregunta);

            ResultSet rs =
                    ps.executeQuery();

            while (rs.next()) {

                respuestas respuesta =
                        new respuestas();

                respuesta.setIdRespuesta(
                        rs.getInt("id_respuesta")
                );

                respuesta.setContenido(
                        rs.getString("contenido")
                );

                respuesta.setIdPregunta(
                        rs.getInt("id_pregunta")
                );

                respuesta.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                lista.add(respuesta);
            }

            rs.close();

        } catch (SQLException e) {
            throw new IllegalStateException("Error al consultar respuestas: " + e.getMessage(), e);
        }

        return lista;
    }

   
    public boolean insertarRespuesta(
            String contenido,
            int idPregunta,
            int idUsuario) {

        String sql =
                "INSERT INTO respuestas "
                + "(contenido, id_pregunta, id_usuario) "
                + "VALUES (?, ?, ?)";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            ps.setString(1, contenido);
            ps.setInt(2, idPregunta);
            ps.setInt(3, idUsuario);

            int resultado =
                    ps.executeUpdate();

            return resultado > 0;

        } catch (SQLException e) {
            throw new IllegalStateException("Error al publicar la respuesta: " + e.getMessage(), e);
        }
    }
}

package database;

import controlador.conexion;
import modelo.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class user_DB {

    // =====================================================
    // REGISTRAR USUARIO
    // =====================================================

    public boolean registrarUsuario(
            String nombre,
            String correo,
            String password) throws SQLException {

        String sql =
                "INSERT INTO usuarios "
                + "(nombre, correo, password) "
                + "VALUES (?, ?, ?)";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, password);

            int resultado =
                    ps.executeUpdate();

            return resultado > 0;

        }
    }

    // =====================================================
    // BUSCAR USUARIO PARA LOGIN
    // =====================================================

    public user iniciarSesion(
            String correo,
            String password) {

        String sql =
                "SELECT id_usuario, nombre, correo, password "
                + "FROM usuarios "
                + "WHERE correo = ? "
                + "AND password = ?";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            if (conn == null) return null;

            ps.setString(1, correo);
            ps.setString(2, password);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                user usuario =
                        new user();

                usuario.setIdUsuario(
                        rs.getInt("id_usuario")
                );

                usuario.setNombre(
                        rs.getString("nombre")
                );

                usuario.setCorreo(
                        rs.getString("correo")
                );

                usuario.setPassword(
                        rs.getString("password")
                );

                rs.close();

                return usuario;
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al iniciar sesión:"
            );

            e.printStackTrace();
        }

        return null;
    }

    // =====================================================
    // BUSCAR USUARIO POR ID
    // =====================================================

    public user obtenerUsuario(
            int idUsuario) {

        String sql =
                "SELECT id_usuario, nombre, correo, password "
                + "FROM usuarios "
                + "WHERE id_usuario = ?";

        try (
            Connection conn = conexion.conectar();
            PreparedStatement ps =
                    conn.prepareStatement(sql)
        ) {

            if (conn == null) return null;

            ps.setInt(1, idUsuario);

            ResultSet rs =
                    ps.executeQuery();

            if (rs.next()) {

                return new user(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("correo"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener usuario:"
            );

            e.printStackTrace();
        }

        return null;
    }
}

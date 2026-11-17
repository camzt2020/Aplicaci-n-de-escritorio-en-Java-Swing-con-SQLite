
package controlador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;


public class conexion {
    // Conserva el archivo que ya usa el proyecto aunque se ejecute desde NetBeans
    // o desde Maven con otra carpeta de trabajo.
    private static final String DB_FILE = "C:/Users/rodri/OneDrive/Desktop/u/crud_foro1/foro.db";
    
    public static Connection conectar() throws SQLException {
        Connection conexion =  null;
        
        try{
            Path archivo = Path.of(DB_FILE).toAbsolutePath();
            Files.createDirectories(archivo.getParent());
            conexion = DriverManager.getConnection("jdbc:sqlite:" + archivo);
            try (var st = conexion.createStatement()) {
                st.execute("PRAGMA foreign_keys = ON");
                st.execute("CREATE TABLE IF NOT EXISTS usuarios ("
                        + "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "nombre TEXT NOT NULL, correo TEXT NOT NULL UNIQUE,"
                        + "password TEXT NOT NULL)");
                // Esquema anterior del proyecto: la columna estaba escrita "pasword".
                if (!columnaExiste(conexion, "usuarios", "password")
                        && columnaExiste(conexion, "usuarios", "pasword")) {
                    st.execute("ALTER TABLE usuarios RENAME COLUMN pasword TO password");
                }
                st.execute("CREATE TABLE IF NOT EXISTS preguntas ("
                        + "id_pregunta INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "titulo TEXT NOT NULL, contenido TEXT NOT NULL,"
                        + "id_usuario INTEGER NOT NULL REFERENCES usuarios(id_usuario))");
                // Esquema anterior: ID_preguntas (plural) era la clave de preguntas.
                if (!columnaExiste(conexion, "preguntas", "id_pregunta")
                        && columnaExiste(conexion, "preguntas", "ID_preguntas")) {
                    st.execute("ALTER TABLE preguntas RENAME COLUMN ID_preguntas TO id_pregunta");
                }
                st.execute("CREATE TABLE IF NOT EXISTS respuestas ("
                        + "id_respuesta INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + "contenido TEXT NOT NULL,"
                        + "id_pregunta INTEGER NOT NULL REFERENCES preguntas(id_pregunta) ON DELETE CASCADE,"
                        + "id_usuario INTEGER NOT NULL REFERENCES usuarios(id_usuario))");
                // Conserva y traslada las respuestas de la tabla antigua "respuesta".
                if (tablaExiste(conexion, "respuesta")) {
                    st.executeUpdate("INSERT OR IGNORE INTO respuestas "
                            + "(id_respuesta, contenido, id_pregunta, id_usuario) "
                            + "SELECT ID_respuesta, contenido, ID_pregunta, ID_usuario FROM respuesta");
                }
            }
            System.out.println("Conexión exitosa: " + archivo);
        
        }catch(SQLException e){
            System.err.println("Error de conexión: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            throw new SQLException("No se pudo preparar la carpeta de la base de datos", e);
        }
        return conexion;
    
    }

    private static boolean tablaExiste(Connection conn, String tabla) throws SQLException {
        try (var ps = conn.prepareStatement(
                "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?")) {
            ps.setString(1, tabla);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean columnaExiste(Connection conn, String tabla, String columna)
            throws SQLException {
        try (var st = conn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(" + tabla + ")")) {
            while (rs.next()) {
                if (columna.equalsIgnoreCase(rs.getString("name"))) return true;
            }
            return false;
        }
    }
        
}

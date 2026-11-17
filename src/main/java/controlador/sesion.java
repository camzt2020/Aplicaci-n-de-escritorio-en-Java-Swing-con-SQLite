package controlador;

public class sesion {

    private static int idUsuario;
    private static String nombreUsuario;

    public static void iniciarSesion(
            int id,
            String nombre) {

        idUsuario = id;
        nombreUsuario = nombre;
    }

    public static int getIdUsuario() {
        return idUsuario;
    }

    public static String getNombreUsuario() {
        return nombreUsuario;
    }

    public static boolean haySesion() {
        return idUsuario != 0;
    }

    public static void cerrarSesion() {
        idUsuario = 0;
        nombreUsuario = null;
    }
}
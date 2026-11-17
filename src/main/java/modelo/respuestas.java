package modelo;

public class respuestas {

    private int idRespuesta;
    private String contenido;
    private int idPregunta;
    private int idUsuario;

    public respuestas() {
    }

    public respuestas(
            int idRespuesta,
            String contenido,
            int idPregunta,
            int idUsuario) {

        this.idRespuesta = idRespuesta;
        this.contenido = contenido;
        this.idPregunta = idPregunta;
        this.idUsuario = idUsuario;
    }

    public int getIdRespuesta() {
        return idRespuesta;
    }

    public void setIdRespuesta(int idRespuesta) {
        this.idRespuesta = idRespuesta;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return contenido;
    }
}
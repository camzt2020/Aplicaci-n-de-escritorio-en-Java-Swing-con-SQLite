package modelo;

public class preguntas {

    private int idPregunta;
    private String titulo;
    private String contenido;
    private int idUsuario;

    public preguntas() {
    }

    public preguntas(int idPregunta, String titulo, String contenido, int idUsuario) {
        this.idPregunta = idPregunta;
        this.titulo = titulo;
        this.contenido = contenido;
        this.idUsuario = idUsuario;
    }

    public int getIdPregunta() {
        return idPregunta;
    }

    public void setIdPregunta(int idPregunta) {
        this.idPregunta = idPregunta;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return idPregunta + " - " + titulo;
    }
}
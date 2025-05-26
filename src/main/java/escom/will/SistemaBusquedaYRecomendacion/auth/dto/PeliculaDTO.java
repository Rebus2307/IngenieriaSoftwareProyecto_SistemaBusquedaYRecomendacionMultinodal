package escom.will.SistemaBusquedaYRecomendacion.auth.dto;

public class PeliculaDTO {
    private String titulo;
    private String anio;
    private String director;
    private String genero;
    private String sinopsis;
    private String posterUrl;
    private String calificacion;

    // Getters y setters
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    @Override
    public String toString() {
        return "PeliculaDTO{" +
                "titulo='" + titulo + '\'' +
                ", anio='" + anio + '\'' +
                ", director='" + director + '\'' +
                ", genero='" + genero + '\'' +
                ", sinopsis='" + sinopsis + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                ", calificacion='" + calificacion + '\'' +
                '}';
    }
}
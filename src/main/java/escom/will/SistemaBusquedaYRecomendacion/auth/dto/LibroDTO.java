package escom.will.SistemaBusquedaYRecomendacion.auth.dto;

import lombok.Data;

@Data
public class LibroDTO {
    private String titulo;
    private String autor;
    private Integer anioPublicacion;
    private String isbn;
    private String portadaUrl;
}
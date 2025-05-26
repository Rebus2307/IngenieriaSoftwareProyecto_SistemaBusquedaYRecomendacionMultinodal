package escom.will.SistemaBusquedaYRecomendacion.auth.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LibroDTO {
    private Long id; // Identificador único
    private String titulo;
    private String autor;
    private Integer anioPublicacion;
    private String isbn;
    private String portadaUrl;
}
package escom.will.SistemaBusquedaYRecomendacion.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "favoritos") // Asegúrate de que el nombre coincida con la tabla en la base de datos
public class LibroFavorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "El usuario no puede ser nulo")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id", nullable = false)
    @NotNull(message = "El libro no puede ser nulo")
    private Libro libro;

    @Lob
    @Column(name = "libro_json", columnDefinition = "TEXT")
    private String libroJson; // Campo para almacenar el JSON del libro

    @Column(name = "fecha_agregado", updatable = false)
    private LocalDateTime fechaAgregado = LocalDateTime.now();

    // Constructor sin argumentos requerido por JPA
    public LibroFavorito() {}

    // Constructor adicional para facilitar la creación de objetos
    public LibroFavorito(Usuario usuario, Libro libro, String libroJson) {
        this.usuario = usuario;
        this.libro = libro;
        this.libroJson = libroJson;
        this.fechaAgregado = LocalDateTime.now();
    }
}
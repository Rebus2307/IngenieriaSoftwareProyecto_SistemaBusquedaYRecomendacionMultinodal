package escom.will.SistemaBusquedaYRecomendacion.auth.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Lob
    @Column(name = "imagen", columnDefinition = "LONGBLOB") // Cambiado a LONGBLOB para soportar imágenes más grandes
    private byte[] imagen;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LibroFavorito> librosFavoritos;

    // Constructor sin argumentos requerido por JPA
    public Usuario() {}

    // Constructor con ID
    public Usuario(Long id) {
        this.id = id;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public void setRoles(Set<Rol> roles) {
        this.roles = roles;
    }

    public List<LibroFavorito> getLibrosFavoritos() {
        return librosFavoritos;
    }

    public void setLibrosFavoritos(List<LibroFavorito> librosFavoritos) {
        this.librosFavoritos = librosFavoritos;
    }
}
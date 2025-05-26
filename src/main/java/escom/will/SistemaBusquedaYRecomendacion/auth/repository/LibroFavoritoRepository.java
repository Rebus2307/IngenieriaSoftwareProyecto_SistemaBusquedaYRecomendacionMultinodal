package escom.will.SistemaBusquedaYRecomendacion.auth.repository;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.LibroFavorito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroFavoritoRepository extends JpaRepository<LibroFavorito, Long> {
    List<LibroFavorito> findByUsuarioId(Long usuarioId);
    boolean existsByUsuarioIdAndLibroId(Long usuarioId, Long libroId);
    void deleteByUsuarioIdAndLibroId(Long usuarioId, Long libroId);

    // Método agregado para buscar un favorito específico
    Optional<LibroFavorito> findByUsuarioIdAndLibroId(Long usuarioId, Long libroId);

    // Método opcional para obtener el JSON del libro favorito
    @Query("SELECT l.libroJson FROM LibroFavorito l WHERE l.usuario.id = :usuarioId AND l.libro.id = :libroId")
    Optional<String> findLibroJsonByUsuarioIdAndLibroId(@Param("usuarioId") Long usuarioId, @Param("libroId") Long libroId);
}
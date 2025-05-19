package escom.will.SistemaBusquedaYRecomendacion.auth.repository;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {
    
    List<Libro> findByTituloContainingIgnoreCase(String titulo);
    
    List<Libro> findByAutorContainingIgnoreCase(String autor);
    
    List<Libro> findByCategoriaId(Long categoriaId);
    
    List<Libro> findTop10ByOrderByCalificacionDesc();
    
    List<Libro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(
        String titulo, String autor);
    
    boolean existsByTituloAndAutor(String titulo, String autor);
}
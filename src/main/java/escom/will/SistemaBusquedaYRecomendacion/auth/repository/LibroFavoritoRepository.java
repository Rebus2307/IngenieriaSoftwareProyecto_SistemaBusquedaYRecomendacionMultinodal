package escom.will.SistemaBusquedaYRecomendacion.auth.repository;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.LibroFavorito;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroFavoritoRepository extends JpaRepository<LibroFavorito, Long> {
    LibroFavorito findByUsuarioAndLibroId(Usuario usuario, String libroId);
}
package escom.will.SistemaBusquedaYRecomendacion.auth.repository;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByNombre(String nombre);
}
package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroService {
    List<Libro> buscarLibros(String titulo, String autor, Long categoriaId);
    Optional<Libro> obtenerLibroPorId(Long id);
    List<Libro> obtenerLibrosDestacados();
    Libro guardarLibro(Libro libro);
    void eliminarLibro(Long id);
    List<Libro> obtenerTodosLosLibros();
}
package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Libro;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.LibroRepository;
import escom.will.SistemaBusquedaYRecomendacion.auth.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Override
    public List<Libro> buscarLibros(String titulo, String autor, Long categoriaId) {
        if (titulo != null && !titulo.isEmpty()) {
            return libroRepository.findByTituloContainingIgnoreCase(titulo);
        }
        if (autor != null && !autor.isEmpty()) {
            return libroRepository.findByAutorContainingIgnoreCase(autor);
        }
        if (categoriaId != null) {
            return libroRepository.findByCategoriaId(categoriaId);
        }
        return obtenerLibrosDestacados();
    }

    @Override
    public Optional<Libro> obtenerLibroPorId(Long id) {
        return libroRepository.findById(id);
    }

    @Override
    public List<Libro> obtenerLibrosDestacados() {
        return libroRepository.findTop10ByOrderByCalificacionDesc();
    }

    @Override
    public Libro guardarLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    @Override
    public void eliminarLibro(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new ResourceNotFoundException("Libro no encontrado con id: " + id);
        }
        libroRepository.deleteById(id);
    }

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        return libroRepository.findAll();
    }
}
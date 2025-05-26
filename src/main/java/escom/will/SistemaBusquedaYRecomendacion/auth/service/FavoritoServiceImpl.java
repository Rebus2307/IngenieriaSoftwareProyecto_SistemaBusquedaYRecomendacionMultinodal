package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.LibroFavorito;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.LibroFavoritoRepository;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.LibroRepository;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoritoServiceImpl implements FavoritoService {

    @Autowired
    private LibroFavoritoRepository favoritoRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void agregarAFavoritos(Long usuarioId, Long libroId) {
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si el libro ya está en favoritos
        if (favoritoRepository.existsByUsuarioIdAndLibroId(usuario.getId(), libroId)) {
            throw new IllegalArgumentException("El libro ya está en favoritos.");
        }

        // Crear el objeto LibroFavorito
        LibroFavorito favorito = new LibroFavorito();
        favorito.setUsuario(usuario);

        // Buscar el libro
        favorito.setLibro(libroRepository.findById(libroId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado")));

        // Guardar el favorito
        favoritoRepository.save(favorito);
    }

    @Override
    public void eliminarDeFavoritos(Long usuarioId, Long libroId) {
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Verificar si el libro está en favoritos y eliminarlo
        if (favoritoRepository.existsByUsuarioIdAndLibroId(usuario.getId(), libroId)) {
            favoritoRepository.deleteByUsuarioIdAndLibroId(usuario.getId(), libroId);
        } else {
            throw new IllegalArgumentException("El libro no está en favoritos.");
        }
    }

    @Override
    public List<LibroFavorito> obtenerFavoritosPorUsuario(Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId);
    }
}
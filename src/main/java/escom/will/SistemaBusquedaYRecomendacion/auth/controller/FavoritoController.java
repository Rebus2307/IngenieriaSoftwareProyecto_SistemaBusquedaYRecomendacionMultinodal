package escom.will.SistemaBusquedaYRecomendacion.auth.controller;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.LibroFavorito;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.LibroFavoritoRepository;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.LibroRepository;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    @Autowired
    private LibroFavoritoRepository favoritoRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/{libroId}")
    public ResponseEntity<?> agregarFavorito(@PathVariable Long libroId, @RequestBody String libroJson, Authentication auth) {
        // Obtener el nombre del usuario autenticado
        String nombre = auth.getName(); // auth.getName() devuelve el nombre del usuario autenticado

        // Buscar al usuario en la base de datos usando el nombre
        Usuario usuario = usuarioRepository.findByNombre(nombre);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Verificar si el libro ya está en favoritos
        if (!favoritoRepository.existsByUsuarioIdAndLibroId(usuario.getId(), libroId)) {
            LibroFavorito favorito = new LibroFavorito();
            favorito.setUsuario(usuario);
            favorito.setLibro(libroRepository.findById(libroId)
                    .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado")));
            favorito.setLibroJson(libroJson); // Guardar el JSON del libro
            favoritoRepository.save(favorito);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{libroId}")
    public ResponseEntity<?> eliminarFavorito(@PathVariable Long libroId, Authentication auth) {
        // Obtener el nombre del usuario autenticado
        String nombre = auth.getName(); // auth.getName() devuelve el nombre del usuario autenticado

        // Buscar al usuario en la base de datos usando el nombre
        Usuario usuario = usuarioRepository.findByNombre(nombre);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        // Eliminar el libro de favoritos
        favoritoRepository.deleteByUsuarioIdAndLibroId(usuario.getId(), libroId);

        return ResponseEntity.ok().build();
    }
}
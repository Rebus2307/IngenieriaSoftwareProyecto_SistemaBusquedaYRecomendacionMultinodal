package escom.will.SistemaBusquedaYRecomendacion.auth.controller;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Libro;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.LibroFavorito;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.service.LibroService;
import escom.will.SistemaBusquedaYRecomendacion.auth.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LibroController {

    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    @Autowired
    private LibroService libroService;

    @Autowired
    private UsuarioService usuarioService;

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
               !"anonymousUser".equals(authentication.getPrincipal());
    }

    @GetMapping("/buscar-libros")
    public String buscarLibros(@RequestParam(value = "titulo", required = false) String titulo,
                               @RequestParam(value = "autor", required = false) String autor,
                               Model model) {

        if ((titulo == null || titulo.isEmpty()) && (autor == null || autor.isEmpty())) {
            model.addAttribute("mensaje", "Por favor, ingrese al menos un término de búsqueda.");
            return "buscar-libros";
        }

        List<Libro> libros = libroService.buscarLibros(titulo, autor);
        model.addAttribute("libros", libros);

        if (isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioService.findByUsername(username);
            if (usuario != null) {
                List<String> librosFavoritosIds = usuario.getLibrosFavoritos().stream()
                        .map(LibroFavorito::getLibroId)
                        .collect(Collectors.toList());
                model.addAttribute("librosFavoritos", librosFavoritosIds);
                model.addAttribute("usuario", usuario);

                if (usuario.getImagen() != null) {
                    String imagenBase64 = Base64.getEncoder().encodeToString(usuario.getImagen());
                    model.addAttribute("usuarioImagen", imagenBase64);
                }
            } else {
                logger.warn("Usuario no encontrado con username: {}", username);
            }
        }

        return "buscar-libros";
    }

    @PostMapping("/agregar-favorito")
    public ResponseEntity<?> agregarFavorito(@RequestParam("libroId") String libroId, HttpServletRequest request) {
        if (isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioService.findByUsername(username);
            if (usuario != null) {
                libroService.agregarLibroFavorito(usuario, libroId);
                logger.info("Libro con ID {} agregado a favoritos para el usuario {}", libroId, username);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.warn("Usuario no encontrado con username: {}", username);
                return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
        } else {
            logger.warn("Usuario no autenticado intentó agregar un libro a favoritos");
            return new ResponseEntity<>("Usuario no autenticado", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/quitar-favorito")
    public ResponseEntity<?> quitarFavorito(@RequestParam("libroId") String libroId, HttpServletRequest request) {
        if (isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioService.findByUsername(username);
            if (usuario != null) {
                libroService.quitarLibroFavorito(usuario, libroId);
                logger.info("Libro con ID {} quitado de favoritos para el usuario {}", libroId, username);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                logger.warn("Usuario no encontrado con username: {}", username);
                return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
            }
        } else {
            logger.warn("Usuario no autenticado intentó quitar un libro de favoritos");
            return new ResponseEntity<>("Usuario no autenticado", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/mostrar-favoritos")
    public String mostrarFavoritos(Model model, HttpServletRequest request) {
        if (isAuthenticated()) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuario = usuarioService.findByUsername(username);
            if (usuario != null) {
                List<Libro> librosFavoritos = usuario.getLibrosFavoritos().stream()
                        .map(libroFavorito -> {
                            Libro libro = libroService.obtenerLibroPorId(libroFavorito.getLibroId());
                            if (libro == null) {
                                logger.warn("Libro no encontrado con ID: {}", libroFavorito.getLibroId());
                                return null;
                            }
                            return libro;
                        })
                        .filter(libro -> libro != null)
                        .collect(Collectors.toList());

                model.addAttribute("libros", librosFavoritos);
                model.addAttribute("librosFavoritos", librosFavoritos.stream().map(Libro::getId).collect(Collectors.toList()));
                model.addAttribute("usuario", usuario);

                if (usuario.getImagen() != null) {
                    String imagenBase64 = Base64.getEncoder().encodeToString(usuario.getImagen());
                    model.addAttribute("usuarioImagen", imagenBase64);
                }
            } else {
                logger.warn("Usuario no encontrado con username: {}", username);
            }
        }
        return "buscar-libros";
    }
}

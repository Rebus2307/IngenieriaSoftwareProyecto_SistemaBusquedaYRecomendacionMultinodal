package escom.will.SistemaBusquedaYRecomendacion.auth.controller;

import escom.will.SistemaBusquedaYRecomendacion.auth.service.LibroService;
import escom.will.SistemaBusquedaYRecomendacion.auth.service.CategoriaService;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Libro;
import escom.will.SistemaBusquedaYRecomendacion.auth.exception.ResourceNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/buscar-libros")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String mostrarBusqueda(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            @RequestParam(required = false) Long categoria,
            Model model) {
        
        // Obtener categorías para el selector
        model.addAttribute("categorias", categoriaService.obtenerTodasCategorias());

        // Si no hay parámetros de búsqueda, mostrar libros destacados
        if (titulo == null && autor == null && categoria == null) {
            model.addAttribute("libros", libroService.obtenerLibrosDestacados());
            return "buscar-libros";
        }

        // Realizar búsqueda con filtros
        List<Libro> resultados = libroService.buscarLibros(titulo, autor, categoria);
        model.addAttribute("libros", resultados);
        
        return "buscar-libros";
    }

    @GetMapping("/{id}")
    public String verDetalleLibro(@PathVariable Long id, Model model) {
        Libro libro = libroService.obtenerLibroPorId(id)
            .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado"));
        
        model.addAttribute("libro", libro);
        return "detalle-libro";
    }
}
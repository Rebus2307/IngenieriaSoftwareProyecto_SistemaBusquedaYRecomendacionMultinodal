package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Libro;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;

import java.util.List;

public interface LibroService {
    List<Libro> buscarLibros(String titulo, String autor);
    void agregarLibroFavorito(Usuario usuario, String libroId);
    void quitarLibroFavorito(Usuario usuario, String libroId);
    Libro obtenerLibroPorId(String libroId);
}
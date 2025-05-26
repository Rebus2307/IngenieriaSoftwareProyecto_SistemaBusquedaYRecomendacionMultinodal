package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.LibroFavorito;
import java.util.List;

public interface FavoritoService {
    void agregarAFavoritos(Long usuarioId, Long libroId);
    void eliminarDeFavoritos(Long usuarioId, Long libroId);
    List<LibroFavorito> obtenerFavoritosPorUsuario(Long usuarioId);
}
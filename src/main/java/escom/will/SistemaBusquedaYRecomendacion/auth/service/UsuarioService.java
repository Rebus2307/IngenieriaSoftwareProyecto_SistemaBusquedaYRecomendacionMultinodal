package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;

public interface UsuarioService {
    Usuario findByUsername(String username);
    void registrarUsuario(Usuario usuario);
}
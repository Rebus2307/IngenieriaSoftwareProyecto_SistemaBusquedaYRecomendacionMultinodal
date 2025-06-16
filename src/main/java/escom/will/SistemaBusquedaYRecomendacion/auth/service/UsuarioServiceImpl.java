package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Rol;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.RolRepository;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.UsuarioRepository;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario findByUsername(String username) {
        return usuarioRepository.findByNombre(username);
    }

    @Override
    public void registrarUsuario(Usuario usuario) {
        // Codifica la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Buscar el rol "ROLE_USER" en la base de datos
        Rol rolUsuario = rolRepository.findByNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: El rol ROLE_USER no existe."));

        // Asignar el rol al usuario
        usuario.getRoles().add(rolUsuario);

        // Guarda el usuario en la base de datos
        usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
}
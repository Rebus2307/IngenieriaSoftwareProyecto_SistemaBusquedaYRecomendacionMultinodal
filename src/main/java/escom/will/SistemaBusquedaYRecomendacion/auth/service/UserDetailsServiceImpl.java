package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar por nombre de usuario
        Usuario usuario = usuarioRepository.findByNombre(username);
        
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username);
        }
        
        // Convertir roles a autoridades
        List<SimpleGrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());
        
        // Si no hay roles, asignar al menos uno básico
        if (authorities.isEmpty()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }
        
        // Construir el UserDetails usando el nombre como identificador principal
        return new User(
            usuario.getNombre(), // Usar nombre como username
            usuario.getPassword(),
            authorities
        );
    }
}
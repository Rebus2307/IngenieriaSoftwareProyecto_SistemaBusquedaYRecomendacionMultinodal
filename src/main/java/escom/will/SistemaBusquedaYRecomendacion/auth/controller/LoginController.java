package escom.will.SistemaBusquedaYRecomendacion.auth.controller;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/home")
    public String home(Model model) {
        // Obtener el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Buscar primero por email ya que parece que es el campo usado para login
            Usuario usuario = usuarioRepository.findByEmail(authentication.getName());
            
            // Si no se encuentra por email, intentar por nombre
            if (usuario == null) {
                usuario = usuarioRepository.findByNombre(authentication.getName());
            }
            
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                
                // Codificar la imagen en base64 si existe
                if (usuario.getImagen() != null && usuario.getImagen().length > 0) {
                    String base64Image = Base64.getEncoder().encodeToString(usuario.getImagen());
                    model.addAttribute("usuarioImagen", base64Image);
                }
            }
        }
        
        return "home";
    }
    
    @GetMapping("/perfil")
    public String perfil(Model model) {
        // Obtener el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Buscar primero por email
            Usuario usuario = usuarioRepository.findByEmail(authentication.getName());
            
            // Si no se encuentra por email, intentar por nombre
            if (usuario == null) {
                usuario = usuarioRepository.findByNombre(authentication.getName());
            }
            
            if (usuario != null) {
                model.addAttribute("usuario", usuario);
                
                // Codificar la imagen en base64 si existe
                if (usuario.getImagen() != null && usuario.getImagen().length > 0) {
                    String base64Image = Base64.getEncoder().encodeToString(usuario.getImagen());
                    model.addAttribute("usuarioImagen", base64Image);
                }
            } else {
                // Si por alguna razón no se encontró al usuario
                model.addAttribute("error", "No se pudo cargar la información del perfil");
            }
        }
        
        return "perfil";
    }
    
    @PostMapping("/actualizarPerfil")
    public String actualizarPerfil(
        @RequestParam("nombre") String nombre,
        @RequestParam("email") String email,
        @RequestParam(value = "password", required = false) String password,
        @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
        HttpServletRequest request,
        RedirectAttributes redirectAttributes) {
    
    try {
        // Obtener el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Buscar usuario por email o nombre
            String currentUsername = authentication.getName();
            Usuario usuario = usuarioRepository.findByEmail(currentUsername);
            
            if (usuario == null) {
                usuario = usuarioRepository.findByNombre(currentUsername);
            }
            
            if (usuario != null) {
                // Guardar valores originales para comprobar si hubo cambios
                String originalEmail = usuario.getEmail();
                
                // Actualizar nombre
                usuario.setNombre(nombre);
                
                // Actualizar email si ha cambiado
                if (!usuario.getEmail().equals(email)) {
                    // Verificar que el nuevo email no esté en uso
                    Usuario existingUser = usuarioRepository.findByEmail(email);
                    if (existingUser != null && !existingUser.getId().equals(usuario.getId())) {
                        redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está en uso.");
                        return "redirect:/home";
                    }
                    usuario.setEmail(email);
                }
                
                // Actualizar contraseña si se proporcionó una nueva
                if (password != null && !password.trim().isEmpty()) {
                    usuario.setPassword(passwordEncoder.encode(password));
                }
                
                // Actualizar imagen si se proporcionó una nueva
                if (imagenFile != null && !imagenFile.isEmpty()) {
                    try {
                        usuario.setImagen(imagenFile.getBytes());
                    } catch (IOException e) {
                        redirectAttributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
                        return "redirect:/home";
                    }
                }
                
                // Guardar cambios
                usuarioRepository.save(usuario);
                
                // Si el email cambió, necesitamos actualizar la autenticación
                boolean credentialsChanged = !originalEmail.equals(usuario.getEmail());
                
                if (credentialsChanged) {
                    // Cerrar sesión y redirigir al login para que vuelva a iniciar sesión
                    // con las nuevas credenciales
                    SecurityContextHolder.clearContext();
                    redirectAttributes.addFlashAttribute("mensaje", 
                        "Tu perfil ha sido actualizado. Por favor inicia sesión de nuevo con tu nuevo correo electrónico.");
                    return "redirect:/login";
                } else {
                    // Actualizar la sesión con información actualizada
                    // Esta es la parte más importante para resolver tu problema:
                    // Cerrar la sesión actual y obtener un nuevo token de autenticación
                    SecurityContextHolder.clearContext();
                    
                    // Forzar el cierre de la sesión HTTP
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.invalidate();
                    }
                    
                    // Añadir mensaje de éxito
                    redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente.");
                    
                    // Como cerramos la sesión, redirigimos al login
                    return "redirect:/login";
                }
                
            } else {
                redirectAttributes.addFlashAttribute("error", "No se pudo encontrar tu perfil de usuario.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "No has iniciado sesión");
            return "redirect:/login";
        }
        
        return "redirect:/home";
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        return "redirect:/home";
    }
}
    
    @PostMapping("/eliminarPerfil")
    public String eliminarPerfil(RedirectAttributes redirectAttributes) {
        try {
            // Obtener el usuario autenticado actual
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                Usuario usuario = usuarioRepository.findByEmail(username);
                
                if (usuario == null) {
                    usuario = usuarioRepository.findByNombre(username);
                }
                
                if (usuario != null) {
                    usuarioRepository.delete(usuario);
                    SecurityContextHolder.clearContext(); // Cerrar sesión
                    redirectAttributes.addFlashAttribute("mensaje", "Cuenta eliminada correctamente.");
                    return "redirect:/login?logout";
                } else {
                    redirectAttributes.addFlashAttribute("error", "No se pudo encontrar tu perfil de usuario.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "No has iniciado sesión");
                return "redirect:/login";
            }
            
            return "redirect:/perfil";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la cuenta: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        // Obtener el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            // Buscar primero por email ya que parece que es el campo usado para login
            Usuario usuario = usuarioRepository.findByEmail(authentication.getName());

            // Si no se encuentra por email, intentar por nombre
            if (usuario == null) {
                usuario = usuarioRepository.findByNombre(authentication.getName());
            }

            if (usuario != null) {
                model.addAttribute("usuario", usuario);

                // Codificar la imagen en base64 si existe
                if (usuario.getImagen() != null && usuario.getImagen().length > 0) {
                    String base64Image = Base64.getEncoder().encodeToString(usuario.getImagen());
                    model.addAttribute("usuarioImagen", base64Image);
                }

                // Verificar si el usuario tiene el rol de administrador
                boolean esAdmin = usuario.getRoles().stream()
                        .anyMatch(rol -> "ROLE_ADMIN".equals(rol.getNombre()));
                if (!esAdmin) {
                    return "redirect:/home"; // Redirigir a home si no es administrador
                }

                // Obtener la lista de usuarios
                model.addAttribute("usuarios", usuarioRepository.findAll());
            }
        }

        return "usuarios"; // Nombre de la plantilla HTML
    }
}
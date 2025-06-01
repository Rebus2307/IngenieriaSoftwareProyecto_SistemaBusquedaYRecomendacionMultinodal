package escom.will.SistemaBusquedaYRecomendacion.auth.controller;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        Usuario usuario = getAuthenticatedUser(authentication);
            
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
                
            // Codificar la imagen en base64 si existe
            if (usuario.getImagen() != null && usuario.getImagen().length > 0) {
                String base64Image = Base64.getEncoder().encodeToString(usuario.getImagen());
                model.addAttribute("usuarioImagen", base64Image);
            }
        }
        
        return "home";
    }
    
    @GetMapping("/perfil")
    public String perfil(Model model) {
        // Obtener el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = getAuthenticatedUser(authentication);
            
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
                
            // Codificar la imagen en base64 si existe
            if (usuario.getImagen() != null && usuario.getImagen().length > 0) {
                String base64Image = Base64.getEncoder().encodeToString(usuario.getImagen());
                model.addAttribute("usuarioImagen", base64Image);
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
                Usuario usuario = getAuthenticatedUser(authentication);

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
                        // **Update the Authentication object**
                        // Create a new Authentication object with the updated user details
                        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                            usuario.getEmail(), // or usuario.getNombre(), depending on your login logic
                            authentication.getCredentials(),
                            authentication.getAuthorities()
                        );

                        // Update the SecurityContextHolder with the new Authentication object
                        SecurityContextHolder.getContext().setAuthentication(newAuth);

                        redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
                        return "redirect:/perfil";
                    }

                } else {
                    redirectAttributes.addFlashAttribute("error", "No se pudo encontrar tu perfil de usuario.");
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
                return "redirect:/home";
            }
            return "redirect:/home";
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

    /**
     * Lista todos los usuarios y verifica que el usuario autenticado sea administrador.
     */
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) {
        // Obtener el usuario autenticado actual
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = getAuthenticatedUser(authentication);

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

        return "usuarios"; // Nombre de la plantilla HTML
    }

    /**
     * Procesa la solicitud para actualizar un usuario.
     */
    @PostMapping("/usuarios/editar")
    public String actualizarUsuario(@RequestParam("id") Long id, 
                                    @RequestParam("nombre") String nombre,
                                    @RequestParam("email") String email,
                                    @RequestParam(value = "password", required = false) String password,
                                    RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioRepository.findById(id).orElse(null);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/usuarios";
            }
    
            usuario.setNombre(nombre);
            usuario.setEmail(email);
    
            if (password != null && !password.trim().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(password));
            }
    
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    /**
     * Procesa la solicitud para eliminar un usuario.
     */
    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioRepository.findById(id).orElse(null);
            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                return "redirect:/usuarios";
            }
    
            usuarioRepository.delete(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/usuarios";
    }

    private Usuario getAuthenticatedUser(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            // Buscar primero por email ya que parece que es el campo usado para login
            Usuario usuario = usuarioRepository.findByEmail(authentication.getName());
            
            // Si no se encuentra por email, intentar por nombre
            if (usuario == null) {
                usuario = usuarioRepository.findByNombre(authentication.getName());
            }
            
            return usuario;
        }
        return null;
    }
}
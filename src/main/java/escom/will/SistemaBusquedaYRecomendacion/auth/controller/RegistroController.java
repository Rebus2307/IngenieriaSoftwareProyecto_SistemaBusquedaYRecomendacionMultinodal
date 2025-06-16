package escom.will.SistemaBusquedaYRecomendacion.auth.controller;

import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.service.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("usuario") Usuario usuario, Model model) {
        try {
            // Lógica para registrar al usuario en la base de datos
            usuarioService.registrarUsuario(usuario);

            // Añadir mensaje de éxito al modelo
            model.addAttribute("success", "Registro exitoso. Ahora puedes iniciar sesión.");

            return "login"; // Redirige a la página de login con mensaje de éxito
        } catch (Exception e) {
            // Añadir mensaje de error al modelo
            model.addAttribute("error", "Error al registrar el usuario: " + e.getMessage());

            // Mantener los datos del usuario en el formulario para que no los pierda.
            model.addAttribute("usuario", usuario);

            return "register"; // Redirige a la página de registro con mensaje de error
        }
    }
}
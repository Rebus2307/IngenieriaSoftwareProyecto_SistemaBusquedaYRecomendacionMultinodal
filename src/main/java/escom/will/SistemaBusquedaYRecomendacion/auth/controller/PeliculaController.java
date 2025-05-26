package escom.will.SistemaBusquedaYRecomendacion.auth.controller;

import escom.will.SistemaBusquedaYRecomendacion.auth.dto.PeliculaDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/buscar-peliculas")
public class PeliculaController {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(PeliculaController.class);
    private static final String API_KEY = "8e7293e3"; // Tu API Key de OMDB

    public PeliculaController() {
        this.webClient = WebClient.create();
    }

    @GetMapping
    public String mostrarBusqueda(@RequestParam(required = false) String titulo, Model model) {
        if (titulo == null || titulo.trim().isEmpty()) {
            model.addAttribute("error", "Debe proporcionar un título para buscar.");
            return "buscar-peliculas";
        }

        try {
            // Construir la URL de la API
            String url = String.format("http://www.omdbapi.com/?apikey=%s&t=%s", API_KEY, titulo.trim().replace(" ", "+"));
            logger.debug("URL de búsqueda: {}", url);

            // Llamar a la API de OMDB
            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

            // Validar la respuesta de la API
            if (response != null && response.has("Response") && response.get("Response").asText().equals("True")) {
                PeliculaDTO pelicula = procesarPelicula(response);
                model.addAttribute("pelicula", pelicula);
                logger.debug("Película procesada: {}", pelicula);
            } else {
                logger.warn("No se encontraron resultados para el título: {}", titulo);
                model.addAttribute("error", "No se encontraron resultados para el título proporcionado.");
            }
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener datos de OMDB API: Status {}, Body {}", e.getStatusCode(), e.getResponseBodyAsString());
            model.addAttribute("error", "Error al conectar con el servicio de búsqueda.");
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage(), e);
            model.addAttribute("error", "Error inesperado al procesar la búsqueda.");
        }

        model.addAttribute("busquedaRealizada", true);
        return "buscar-peliculas";
    }

    private PeliculaDTO procesarPelicula(JsonNode response) {
        PeliculaDTO pelicula = new PeliculaDTO();
        pelicula.setTitulo(getTextValue(response, "Title"));
        pelicula.setAnio(getTextValue(response, "Year"));
        pelicula.setDirector(getTextValue(response, "Director"));
        pelicula.setGenero(getTextValue(response, "Genre"));
        pelicula.setSinopsis(getTextValue(response, "Plot"));
        pelicula.setPosterUrl(getTextValue(response, "Poster"));
        pelicula.setCalificacion(getTextValue(response, "imdbRating"));
        return pelicula;
    }

    private String getTextValue(JsonNode node, String field) {
        try {
            return node.has(field) ? node.get(field).asText() : "";
        } catch (Exception e) {
            logger.warn("Error al obtener el valor de texto para el campo {}: {}", field, e.getMessage());
            return "";
        }
    }
}
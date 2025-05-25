package escom.will.SistemaBusquedaYRecomendacion.auth.controller;

import escom.will.SistemaBusquedaYRecomendacion.auth.dto.LibroDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/buscar-libros")
public class LibroController {

    private final WebClient webClient;
    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    public LibroController() {
        this.webClient = WebClient.create();
    }

    @GetMapping
    public String mostrarBusqueda(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String autor,
            Model model) {
        
        List<LibroDTO> libros = new ArrayList<>();

        try {
            if (titulo != null || autor != null) {
                UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("openlibrary.org")
                    .path("/search.json");

                // Add appropriate query parameters based on what was provided
                if (titulo != null && !titulo.trim().isEmpty()) {
                    builder.queryParam("q", titulo.trim());
                }
                if (autor != null && !autor.trim().isEmpty()) {
                    builder.queryParam("author", autor.trim());
                }
                
                // Add common parameters
                builder.queryParam("limit", 20)
                      .queryParam("sort", "new");

                String url = builder.build().toUriString();
                logger.debug("URL de búsqueda: {}", url);

                JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

                if (response != null && response.has("docs")) {
                    response.get("docs").forEach(doc -> {
                        LibroDTO libro = new LibroDTO();
                        libro.setTitulo(getTextValue(doc, "title"));
                        libro.setAutor(getArrayValue(doc, "author_name"));
                        libro.setAnioPublicacion(getIntValue(doc, "first_publish_year"));
                        libro.setIsbn(getArrayValue(doc, "isbn"));
                        
                        // Get cover ID if available, otherwise use ISBN
                        String coverId = getTextValue(doc, "cover_i");
                        if (!coverId.isEmpty()) {
                            libro.setPortadaUrl("https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg");
                        } else {
                            libro.setPortadaUrl(getCoverUrl(getArrayValue(doc, "isbn")));
                        }
                        
                        libros.add(libro);
                    });
                }
            }
        } catch (WebClientResponseException e) {
            logger.error("Error al obtener datos de Open Library API: {}", e.getMessage());
            model.addAttribute("error", "Error al conectar con el servicio de búsqueda");
        } catch (Exception e) {
            logger.error("Error inesperado: {}", e.getMessage());
            model.addAttribute("error", "Error inesperado al procesar la búsqueda");
        }

        model.addAttribute("libros", libros);
        model.addAttribute("busquedaRealizada", titulo != null || autor != null);
        return "buscar-libros";
    }

    private String getTextValue(JsonNode node, String field) {
        try {
            return node.has(field) ? node.get(field).asText() : "";
        } catch (Exception e) {
            logger.warn("Error al obtener el valor de texto para el campo {}: {}", field, e.getMessage());
            return "";
        }
    }

    private String getArrayValue(JsonNode node, String field) {
        try {
            if (node.has(field) && node.get(field).isArray() && node.get(field).size() > 0) {
                return node.get(field).get(0).asText();
            }
            return "";
        } catch (Exception e) {
            logger.warn("Error al obtener el valor del array para el campo {}: {}", field, e.getMessage());
            return "";
        }
    }

    private Integer getIntValue(JsonNode node, String field) {
        try {
            return node.has(field) ? node.get(field).asInt() : null;
        } catch (Exception e) {
            logger.warn("Error al obtener el valor entero para el campo {}: {}", field, e.getMessage());
            return null;
        }
    }

    private String getCoverUrl(String isbn) {
        if (isbn != null && !isbn.isEmpty()) {
            return "https://covers.openlibrary.org/b/isbn/" + isbn + "-M.jpg";
        }
        return null;
    }
}
package escom.will.SistemaBusquedaYRecomendacion.auth.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Libro;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.LibroFavorito;
import escom.will.SistemaBusquedaYRecomendacion.auth.entity.Usuario;
import escom.will.SistemaBusquedaYRecomendacion.auth.repository.LibroFavoritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LibroServiceImpl implements LibroService {

    @Autowired
    private LibroFavoritoRepository libroFavoritoRepository;

    private final String OPEN_LIBRARY_API_URL = "https://openlibrary.org/search.json?";

    @Override
    public List<Libro> buscarLibros(String titulo, String autor) {
        RestTemplate restTemplate = new RestTemplate();
        StringBuilder url = new StringBuilder(OPEN_LIBRARY_API_URL);

        if (titulo != null && !titulo.isEmpty()) {
            url.append("title=").append(titulo.replace(" ", "+"));
        }

        if (autor != null && !autor.isEmpty()) {
            if (url.toString().contains("title=")) {
                url.append("&");
            }
            url.append("author=").append(autor.replace(" ", "+"));
        }

        OpenLibraryResponse response = restTemplate.getForObject(url.toString(), OpenLibraryResponse.class);
        List<Libro> libros = new ArrayList<>();

        if (response != null && response.getDocs() != null) {
            for (OpenLibraryDoc doc : response.getDocs()) {
                Libro libro = new Libro();
                libro.setId(doc.getKey()); // antes usabas hashCode
                libro.setTitulo(doc.getTitle());
                libro.setAutor(doc.getAuthorName() != null ? String.join(", ", doc.getAuthorName()) : "Unknown");
                libro.setAnioPublicacion(doc.getPublishYear() != null && !doc.getPublishYear().isEmpty() ? doc.getPublishYear().get(0) : null);
                libro.setIsbn(doc.getIsbn() != null && !doc.getIsbn().isEmpty() ? doc.getIsbn().get(0) : null);
                if (doc.getCoverI() != null) {
                    libro.setPortadaUrl("https://covers.openlibrary.org/b/id/" + doc.getCoverI() + "-M.jpg");
                }
                libros.add(libro);
            }
        }

        return libros;
    }

    @Override
    public void agregarLibroFavorito(Usuario usuario, String libroId) {
        if (libroFavoritoRepository.findByUsuarioAndLibroId(usuario, libroId) == null) {
            LibroFavorito libroFavorito = new LibroFavorito();
            libroFavorito.setUsuario(usuario);
            libroFavorito.setLibroId(libroId);
            libroFavoritoRepository.save(libroFavorito);
        }
    }

    @Override
    public void quitarLibroFavorito(Usuario usuario, String libroId) {
        LibroFavorito libroFavorito = libroFavoritoRepository.findByUsuarioAndLibroId(usuario, libroId);
        if (libroFavorito != null) {
            libroFavoritoRepository.delete(libroFavorito);
        }
    }

    @Override
    public Libro obtenerLibroPorId(String libroId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://openlibrary.org/search.json?q=" + libroId;
        OpenLibraryResponse response = restTemplate.getForObject(url, OpenLibraryResponse.class);

        if (response != null && response.getDocs() != null && !response.getDocs().isEmpty()) {
            OpenLibraryDoc doc = response.getDocs().get(0);
            Libro libro = new Libro();
            libro.setId(doc.getKey());
            libro.setTitulo(doc.getTitle());
            libro.setAutor(doc.getAuthorName() != null ? String.join(", ", doc.getAuthorName()) : "Unknown");
            libro.setAnioPublicacion(doc.getPublishYear() != null && !doc.getPublishYear().isEmpty() ? doc.getPublishYear().get(0) : null);
            libro.setIsbn(doc.getIsbn() != null && !doc.getIsbn().isEmpty() ? doc.getIsbn().get(0) : null);
            if (doc.getCoverI() != null) {
                libro.setPortadaUrl("https://covers.openlibrary.org/b/id/" + doc.getCoverI() + "-M.jpg");
            }
            return libro;
        }

        return null;
    }

    // Clases internas para mapear JSON

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenLibraryResponse {
        private List<OpenLibraryDoc> docs;
        public List<OpenLibraryDoc> getDocs() { return docs; }
        public void setDocs(List<OpenLibraryDoc> docs) { this.docs = docs; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OpenLibraryDoc {
        private String key;
        private String title;

        @JsonProperty("author_name")
        private List<String> authorName;

        @JsonProperty("publish_year")
        private List<Integer> publishYear;

        private List<String> isbn;

        @JsonProperty("cover_i")
        private Integer coverI;

        public String getKey() { return key; }
        public String getTitle() { return title; }
        public List<String> getAuthorName() { return authorName; }
        public List<Integer> getPublishYear() { return publishYear; }
        public List<String> getIsbn() { return isbn; }
        public Integer getCoverI() { return coverI; }
    }
}

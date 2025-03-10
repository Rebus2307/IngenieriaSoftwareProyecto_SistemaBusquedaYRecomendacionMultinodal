package escom.will.SistemaBusquedaYRecomendacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "escom.will.SistemaBusquedaYRecomendacion.auth.config",
    "escom.will.SistemaBusquedaYRecomendacion.auth.controller",
    "escom.will.SistemaBusquedaYRecomendacion.auth.entity",
    "escom.will.SistemaBusquedaYRecomendacion.auth.repository",
    "escom.will.SistemaBusquedaYRecomendacion.auth.service",
    "escom.will.SistemaBusquedaYRecomendacion.auth.SistemaAutenticacion"
})
@EnableJpaRepositories(basePackages = "escom.will.SistemaBusquedaYRecomendacion.auth.repository")
@EntityScan(basePackages = "escom.will.SistemaBusquedaYRecomendacion.auth.entity")
public class SistemaBusquedaYRecomendacionApplication {
    public static void main(String[] args) {
        SpringApplication.run(SistemaBusquedaYRecomendacionApplication.class, args);
    }
}
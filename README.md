# IngenieriaSoftwareProyecto_SistemaBusquedaYRecomendacionMultinodal

## Descripción
Este proyecto es un sistema de autenticación de usuarios desarrollado con Spring Boot. Permite el registro e inicio de sesión con manejo de roles y almacenamiento en MySQL. Se puede ejecutar localmente con XAMPP o mediante Docker.

## Documentación
La documentación de este proyecto se encuentra en la carpeta "Documentación"

## Tecnologías Utilizadas
- **Java 21 (Corretto)**
- **Maven 3.9.9**
- **Spring Boot**
- **MySQL** (phpMyAdmin con XAMPP o Docker)
- **Docker y Docker Compose**

## Requisitos Previos
Antes de ejecutar el proyecto, asegúrese de tener instalado:
- Java 21 (Corretto)
- Maven 3.9.9
- XAMPP (si desea ejecutarlo localmente) o Docker (para ejecutarlo con contenedores)

## Clonar el Repositorio
Para obtener el código fuente, clone el repositorio con el siguiente comando:
```sh
git clone https://github.com/tu-usuario/tu-repositorio.git
cd tu-repositorio
```

## Configuración de la Base de Datos
El esquema de la base de datos se encuentra en el archivo `bd.sql`. Puede importarlo en phpMyAdmin si ejecuta el proyecto localmente con XAMPP.

## Ejecución del Proyecto

### Modo Local (XAMPP + phpMyAdmin)
1. Iniciar XAMPP y activar Apache y MySQL.
2. Crear la base de datos e importar `bd.sql` en phpMyAdmin.
3. Ejecutar el siguiente comando en la terminal:
   ```sh
   mvn spring-boot:run
   ```

### Modo Docker
1. Asegurarse de que XAMPP esté apagado (para evitar conflictos en el puerto MySQL).
2. Ejecutar el siguiente comando en la terminal:
   ```sh
   docker-compose up --build
   ```

## Endpoints Principales
- **Registro de Usuario:** `/api/auth/register`
- **Inicio de Sesión:** `/api/auth/login`
- **Acceso a Recursos Protegidos:** (requiere autenticación)

## Notas
- Para personalizar la configuración de la base de datos, edite el archivo `application.properties`.
- Se recomienda usar Postman o un cliente HTTP para probar los endpoints.
- Para detener la ejecución en Docker, utilice `docker-compose down`.

## Funcionalidades Implementadas
- Plantillas HTML para Login y Registro (Thymeleaf)
- Configuración básica de Spring Boot
- Implementación de bae de datos
- Ejecución con Docker
- Administración de roles
- Implementación de cambio de tema claro y oscuro

## Pruebas de ejecucion

![image](https://github.com/user-attachments/assets/381ec8f4-c7e9-4546-a186-86bb5621645f)
![image](https://github.com/user-attachments/assets/c9d16757-847d-4e3b-90b9-c0e70a1cf222)
![image](https://github.com/user-attachments/assets/68368626-ec88-4101-8da6-23953128b7ad)
![image](https://github.com/user-attachments/assets/27a734f0-df5f-472c-9859-2fbc7899de29)
![image](https://github.com/user-attachments/assets/7eceff46-b030-49fc-8a8c-1d60944b0c0f)


-- Eliminar la base de datos "RecomendacionMultinodal" si ya existe
DROP DATABASE IF EXISTS RecomendacionMultinodal;

-- Crear la base de datos "RecomendacionMultinodal" con codificación UTF-8
CREATE DATABASE RecomendacionMultinodal CHARACTER SET utf8 COLLATE utf8_general_ci;

-- Usar la base de datos "RecomendacionMultinodal"
USE RecomendacionMultinodal;

-- Crear la tabla de usuarios
CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    imagen MEDIUMBLOB 
);

-- Crear la tabla de roles
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL UNIQUE
);

-- Crear la tabla intermedia para la relación muchos a muchos entre usuarios y roles
CREATE TABLE usuario_roles (
    usuario_id BIGINT,
    rol_id BIGINT,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- Crear tabla de categorías
CREATE TABLE categorias (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(64) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- Crear tabla de libros
CREATE TABLE libros (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    autor VARCHAR(255) NOT NULL,
    descripcion TEXT,
    anio_publicacion INTEGER,
    url_imagen VARCHAR(255),
    calificacion DOUBLE DEFAULT 0.0,
    categoria_id BIGINT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Crear tabla para guardar los libros favoritos de los usuarios
CREATE TABLE favoritos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    libro_id BIGINT NOT NULL,
    libro_json JSON, -- Nueva columna para almacenar el JSON del libro
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (libro_id) REFERENCES libros(id) ON DELETE CASCADE,
    UNIQUE (usuario_id, libro_id) -- Evitar duplicados
);

-- Insertar roles en la tabla roles
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_USER');

-- Insertar el usuario 'Rebus' con contraseña hasheada '23072003'
INSERT INTO usuarios (nombre, email, password) 
VALUES ('Rebus', 'rebus@admin.com', '$2a$10$Br9dhimafoajCFQe4Vya1u5xclWVVzysNZy/.UT.1W8tRgfA2htem');

-- Asignar el rol de administrador al usuario 'Rebus'
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.nombre = 'Rebus' AND r.nombre = 'ROLE_ADMIN';

-- Insertar categorías iniciales
INSERT INTO categorias (nombre, descripcion) VALUES 
('Ficcion', 'Libros de ficcion en general'),
('No Ficcion', 'Libros basados en hechos reales'),
('Ciencia Ficcion', 'Libros de ciencia ficcion y fantasia'),
('Historia', 'Libros historicos'),
('Tecnologia', 'Libros sobre tecnologia y computacion');

-- Insertar libros de ejemplo
INSERT INTO libros (titulo, autor, descripcion, anio_publicacion, categoria_id, calificacion) VALUES 
('1984', 'George Orwell', 'Una novela distópica sobre un futuro totalitario', 1949, 
 (SELECT id FROM categorias WHERE nombre = 'Ficcion'), 4.5),
('Clean Code', 'Robert C. Martin', 'Una guía práctica para escribir mejor código', 2008, 
 (SELECT id FROM categorias WHERE nombre = 'Tecnologia'), 4.8),
('Dune', 'Frank Herbert', 'Una épica space opera sobre política y religión', 1965, 
 (SELECT id FROM categorias WHERE nombre = 'Ciencia Ficcion'), 4.7);

-- Configuración de usuario MySQL
DROP USER IF EXISTS 'admin'@'localhost';
FLUSH PRIVILEGES;
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON RecomendacionMultinodal.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
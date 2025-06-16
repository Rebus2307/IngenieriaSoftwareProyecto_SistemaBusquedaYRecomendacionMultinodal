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

-- Crear tabla para guardar los libros favoritos de los usuarios
CREATE TABLE libros_favoritos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    usuario_id BIGINT NOT NULL,
    libro_id VARCHAR(255) NOT NULL, -- Almacena el ID del libro de Open Library
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
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

-- Configuración de usuario MySQL
DROP USER IF EXISTS 'admin'@'localhost';
FLUSH PRIVILEGES;
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON RecomendacionMultinodal.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
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
    password VARCHAR(128) NOT NULL
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
-- Insertar roles en la tabla roles
INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN'), ('ROLE_USER');
-- Insertar el usuario 'Rebus' con contraseña hasheada '23072003'
INSERT INTO usuarios (nombre, email, password) 
VALUES ('Rebus', 'rebus@admin.com', '$2a$10$GsjRsIhuS9YOn5sVCWDuY.W5mOxE7T05GMdiK1MWQ2QDGtuUGn5TG');

-- Asignar el rol de administrador al usuario 'Rebus'
INSERT INTO usuario_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u, roles r
WHERE u.nombre = 'Rebus' AND r.nombre = 'ROLE_ADMIN';
-- Eliminar el usuario 'admin' si ya existe
DROP USER IF EXISTS 'admin'@'localhost';
FLUSH PRIVILEGES;
-- Crear el usuario 'admin' con la contraseña 'admin'
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'admin';
-- Otorgar todos los permisos sobre la base de datos "RecomendacionMultinodal" al usuario 'admin'
GRANT ALL PRIVILEGES ON RecomendacionMultinodal.* TO 'admin'@'localhost';
-- Aplicar los cambios
FLUSH PRIVILEGES;
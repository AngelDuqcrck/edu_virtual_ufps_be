--Creamos la tabla roles si no existe
CREATE TABLE IF NOT EXISTS roles (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

--Insertamos los roles en el sistema si estos no existen
INSERT IGNORE INTO roles (id, nombre) VALUES 
(1, 'Estudiante'),
(2, 'Docente'),
(3, 'Director'),
(4, 'Jurado');

--Creamos la tabla admins si no existe
CREATE TABLE IF NOT EXISTS admins (
    id INT NOT NULL AUTO_INCREMENT,
    primer_nombre VARCHAR(50) NOT NULL,
    segundo_nombre VARCHAR(50) NOT NULL,
    primer_apellido VARCHAR(50) NOT NULL,
    segundo_apellido VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    es_super_admin BOOLEAN NOT NULL,
    activo BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);
--Insertamos los usuarios en el sistema si estos no existen

INSERT IGNORE INTO admins (
    id,
    primer_nombre, 
    segundo_nombre, 
    primer_apellido, 
    segundo_apellido, 
    email, 
    password, 
    es_super_admin,
    activo
) VALUES (
    1,
    'Unidad', 
    ' ', 
    'Virtual', 
    'UFPS', 
    'uvirtual@ufps.edu.co', 
    '$2a$10$ddkeQ8RKhqKAT6XTQe2iTuW5vBjWbOUAM0l4EUKVjK.rsJCnHHqZu',  -- Contraseña: 123456789 encriptada
    true,
    true
);


-- Creamos la tabla de semestres si no existe
CREATE TABLE IF NOT EXISTS semestres (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    numero INT NOT NULL,
    numeroRomano VARCHAR(50) NOT NULL
);

-- Insertamos los semestres en el sistema si estos no existen
INSERT IGNORE INTO semestres (id, nombre, numero, numeroRomano)
VALUES 
    (1, 'Semestre I', 1, 'I'),
    (2, 'Semestre II', 2, 'II'),
    (3, 'Semestre III', 3, 'III'),
    (4, 'Semestre IV', 4, 'IV'),
    (5, 'Semestre V', 5, 'V'),
    (6, 'Semestre VI', 6, 'VI'),
    (7, 'Semestre VII', 7, 'VII'),
    (8, 'Semestre VIII', 8, 'VIII'),
    (9, 'Semestre IX', 9, 'IX'),
    (10, 'Semestre X', 10, 'X');
    

--Creamos la tabla de estados de estudiantes si no existe
CREATE TABLE IF NOT EXISTS estados_estudiantes (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

--Insertamos los estados de estudiantes en el sistema si estos no existen
INSERT IGNORE INTO estados_estudiantes (id, nombre) VALUES 
(1, 'En curso'),
(2, 'Inactivo'),
(3, 'Egresado');

--Creamos la tabla de estados de la matricula de estudiantes en el sistema
CREATE TABLE  IF NOT EXISTS estados_matriculas (
    id INTEGER NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

--Insertamos los estados de la matricula de estudiantes en el sistema si estos no existen
INSERT IGNORE INTO estados_matriculas (id, nombre) VALUES 
(1, 'Aprobada'),
(2, 'En curso'),
(3, 'Cancelada'),
(4, 'Reprobada'),
(5, 'Anulada'),
(6, 'Correo enviado');

CREATE TABLE IF NOT EXISTS tipos_solicitudes (
    id INTEGER NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT IGNORE INTO tipos_solicitudes (id, nombre) VALUES 
(1, 'Cancelacion de materias'),
(2, 'Aplazamiento de semestre'),
(3, 'Reintegro');


CREATE TABLE IF NOT EXISTS tipos_contraprestaciones (
    id INTEGER NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    porcentaje VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

INSERT IGNORE INTO tipos_contraprestaciones (id, nombre, porcentaje) VALUES 
(1, 'Tesis Laureada', '50%'),
(2, 'Tesis Meritoria', '30%'),
(3, 'Medalla en Plata', '30%'),
(4, 'Matricula de Honor', '50%');

--Creamos la tabla de tipos de programa si no existe
CREATE TABLE IF NOT EXISTS tipos_programas (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    moodle_id VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

--Insertamos los tipos de programas en el sistema si estos no existen
INSERT IGNORE INTO tipos_programas(id, nombre, moodle_id) VALUES
(1, 'Tecnologia','307'),
(2, 'Maestria', '306')

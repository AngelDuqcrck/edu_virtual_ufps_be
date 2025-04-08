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
    'Angel', 
    'Yesid', 
    'Duque', 
    'Cruz', 
    'angelduque1@example.com', 
    '$2a$10$u1L5aDVRx0tQtfTyqylYVu3mXH6TXvzxX0vEj/yuNwTHK.aj6R3oG',  -- Contraseña: 123456 encriptada
    false,
    true
);

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
    2,
    'Angel', 
    'Yesid', 
    'Duque', 
    'Cruz', 
    'angelduque@example.com', 
    '$2a$10$u1L5aDVRx0tQtfTyqylYVu3mXH6TXvzxX0vEj/yuNwTHK.aj6R3oG',  -- Contraseña: 123456 encriptada
    true,
    true
);

--Creamos la tabla de programas si no existe
CREATE TABLE IF NOT EXISTS programas (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    codigo VARCHAR(255) NOT NULL,
    es_posgrado BOOLEAN NOT NULL,   
    PRIMARY KEY (id)
);

--Creamos la tabla de pensums si no existe
CREATE TABLE IF NOT EXISTS pensums (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    programa_id INT NOT NULL,
    PRIMARY KEY (id)
);

--Creamos la tabla de materias si no existe
CREATE TABLE IF NOT EXISTS materias (
    id INT NOT NULL AUTO_INCREMENT,
    codigo VARCHAR(255) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    creditos VARCHAR(255) NOT NULL,
    pensum_id INT NOT NULL,
    semestre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

 -- Insertamos los programas académicos 
INSERT IGNORE INTO programas (id,nombre, codigo , es_posgrado) VALUES 
(1,'Maestria en Tecnologias de Informacion y Comunicacion (TIC) Aplicadas a la Educacion', '001', true),
(2,'Tecnologia en Analitica de Datos', '002', false);


-- Pensum para Maestría en TIC Aplicadas a la Educación
INSERT IGNORE INTO pensums (id, nombre, programa_id) VALUES (
    1,'Pensum 1 - Maestria en TIC Aplicadas a la Educacion', 1
);

-- Pensum para Tecnología en Analítica de Datos
INSERT IGNORE INTO pensums (id, nombre, programa_id) VALUES (
    2,'Pensum 1 - Tecnologia en Analitica de Datos',2
);
--Insertamos los materias del pensum de Maestría en TIC Aplicadas a la Educación
-- I SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(1, 'MTIC101', 'Investigacion en educacion', '3', 1, 'I'),
(2, 'MTIC102', 'Comunicacion digital y educacion', '3', 1, 'I'),
(3, 'MTIC103', 'TIC y sociedad', '3', 1, 'I'),
(4, 'MTIC104', 'Taller de linea I', '1', 1, 'I');

-- II SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(5, 'MTIC201', 'Saber pedagogico y TIC', '3', 1, 'II'),
(6, 'MTIC202', 'Materiales educativos digitales', '3', 1, 'II'),
(7, 'MTIC203', 'Taller de linea II', '2', 1, 'II'),
(8, 'MTIC204', 'Electiva I', '3', 1, 'II');

-- III SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(9, 'MTIC301', 'Didactica de la educacion virtual', '3', 1, 'III'),
(10, 'MTIC302', 'TIC y diseño curricular', '3', 1, 'III'),
(11, 'MTIC303', 'Taller de linea III', '3', 1, 'III'),
(12, 'MTIC304', 'Electiva II', '3', 1, 'III');

-- IV SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(13, 'MTIC401', 'Gestion de proyectos educativos', '3', 1, 'IV'),
(14, 'MTIC402', 'Trabajo de grado', '6', 1, 'IV'),
(15, 'MTIC403', 'Electiva III', '3', 1, 'IV');

--Insertamos las materias del pensum de Tecnología en Analítica de Datos
-- Materias para Tecnología en Analítica de Datos (Pensum ID 2)

-- I SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(16, 'TAD101', 'Fundamentos de programacion', '3', 2, 'I'),
(17, 'TAD102', 'Introduccion a la analitica de datos', '2', 2, 'I'),
(18, 'TAD103', 'Calculo diferencial', '4', 2, 'I'),
(19, 'TAD104', 'Introduccion a la vida universitaria', '1', 2, 'I'),
(20, 'TAD105', 'Competencias comunicativas', '2', 2, 'I'),
(21, 'TAD106', 'Ingles I', '2', 2, 'I'),
(22, 'TAD107', 'Tecnicas de estudio', '2', 2, 'I');

-- II SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(23, 'TAD201', 'Estructuras de datos', '3', 2, 'II'),
(24, 'TAD202', 'Fundamentos bases de datos', '3', 2, 'II'),
(25, 'TAD203', 'Algebra lineal', '3', 2, 'II'),
(26, 'TAD204', 'Seminario de proyecto de grado I', '1', 2, 'II'),
(27, 'TAD205', 'Estadistica descriptiva y probabilidad', '3', 2, 'II'),
(28, 'TAD206', 'Ingles II', '2', 2, 'II'),
(29, 'TAD207', 'Constitucion politica', '1', 2, 'II');

-- III SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(30, 'TAD301', 'Programacion para analitica de datos', '3', 2, 'III'),
(31, 'TAD302', 'Bases de datos avanzadas', '3', 2, 'III'),
(32, 'TAD303', 'Matematicas discretas', '3', 2, 'III'),
(33, 'TAD304', 'Manipulacion de datos', '3', 2, 'III'),
(34, 'TAD305', 'Machine learning', '3', 2, 'III'),
(35, 'TAD306', 'Etica en la ciencia de datos y comunicacion', '1', 2, 'III');

-- IV SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(36, 'TAD401', 'Visualización de datos', '3', 2, 'IV'),
(37, 'TAD402', 'Electiva profesional I', '3', 2, 'IV'),
(38, 'TAD403', 'Calculo integral', '4', 2, 'IV'),
(39, 'TAD404', 'Seminario de proyecto de grado II', '1', 2, 'IV'),
(40, 'TAD405', 'Deep learning', '3', 2, 'IV'),
(41, 'TAD406', 'Estadistica inferencial', '3', 2, 'IV');

-- V SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(42, 'TAD501', 'Big Data', '3', 2, 'V'),
(43, 'TAD502', 'Inteligencia de negocios', '2', 2, 'V'),
(44, 'TAD503', 'Electiva profesional II', '3', 2, 'V'),
(45, 'TAD504', 'Analitica prescriptiva', '3', 2, 'V'),
(46, 'TAD505', 'Analisis de series temporales', '3', 2, 'V'),
(47, 'TAD506', 'Ingles III', '2', 2, 'V');

-- VI SEMESTRE
INSERT IGNORE INTO materias (id, codigo, nombre, creditos, pensum_id, semestre) VALUES
(48, 'TAD601', 'Electiva profesional III', '3', 2, 'VI'),
(49, 'TAD602', 'Proyecto de grado', '4', 2, 'VI'),
(50, 'TAD603', 'Innovacion y emprendimiento', '2', 2, 'VI'),
(51, 'TAD604', 'Practicas en Analitica de Datos', '6', 2, 'VI');


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
(5, 'Anulada');
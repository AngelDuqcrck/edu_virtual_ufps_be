
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
    es_super_admin
) VALUES (
    1,
    'Angel', 
    'Yesid', 
    'Duque', 
    'Cruz', 
    'angelduque1@example.com', 
    '$2a$10$u1L5aDVRx0tQtfTyqylYVu3mXH6TXvzxX0vEj/yuNwTHK.aj6R3oG',  -- Contraseña: 123456 encriptada
    false
);

INSERT IGNORE INTO admins (
    id,
    primer_nombre, 
    segundo_nombre, 
    primer_apellido, 
    segundo_apellido, 
    email, 
    password, 
    es_super_admin
) VALUES (
    2,
    'Angel', 
    'Yesid', 
    'Duque', 
    'Cruz', 
    'angelduque@example.com', 
    '$2a$10$u1L5aDVRx0tQtfTyqylYVu3mXH6TXvzxX0vEj/yuNwTHK.aj6R3oG',  -- Contraseña: 123456 encriptada
    true
);

 -- Insertamos los programas académicos 
INSERT IGNORE INTO programas (id,nombre, codigo) VALUES 
(1,'Maestria en Tecnologias de Informacion y Comunicacion (TIC) Aplicadas a la Educacion', '001'),
(2,'Tecnologia en Analitica de Datos', '002');

-- Pensum para Maestría en TIC Aplicadas a la Educación
INSERT IGNORE INTO pensums (id, nombre, programa_id) VALUES (
    1,'Pensum 1 - Maestria en TIC Aplicadas a la Educacion', 1
);

-- Pensum para Tecnología en Analítica de Datos
INSERT IGNORE INTO pensums (id, nombre, programa_id) VALUES (
    2,'Pensum 1 - Tecnologia en Analitica de Datos',2
);


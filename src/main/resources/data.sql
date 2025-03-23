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
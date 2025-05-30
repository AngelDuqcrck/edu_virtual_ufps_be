# 🎓 SIAV - Backend

Sistema de Informacion Academico Virtual para la Universidad Francisco de Paula Santander, desarrollado con Spring Boot para la administración integral de estudiantes, matrículas, notas y contenidos académicos.

## 📋 Descripción del Proyecto

SSIAV UFPS es una plataforma backend robusta que facilita la gestión académica universitaria de la Maestria en TIC aplicadas a la educación, integrando múltiples sistemas como Moodle, bases de datos Oracle y MySQL, y servicios de almacenamiento en la nube. El sistema permite gestionar estudiantes, docentes, matrículas, programas académicos, y la sincronización de datos con sistemas legados.

## 🚀 Características Principales

- **Gestión de Estudiantes**: Registro, actualización y seguimiento de estudiantes
- **Sistema de Matrículas**: Control completo del proceso de matrícula académica
- **Integración con Moodle**: Sincronización automática de cursos y usuarios
- **Doble Base de Datos**: MySQL para datos operacionales y Oracle para datos legados
- **Autenticación Segura**: JWT + OAuth2 (Google)
- **Gestión de Archivos**: Integración con AWS S3
- **Sistema de Notas**: Manejo de calificaciones para pregrado y posgrado
- **API RESTful**: Endpoints bien documentados para frontend

## 🛠️ Stack Tecnológico

### Framework Principal

<div align="center">

|                                                   **Spring Boot**                                                    | **Versión** |
| :------------------------------------------------------------------------------------------------------------------: | :---------: |
| ![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white) |   `3.4.3`   |

</div>

### Dependencias Core

<div align="center">

|                                                         **Tecnología**                                                         | **Versión** |        **Propósito**         |
| :----------------------------------------------------------------------------------------------------------------------------: | :---------: | :--------------------------: |
| ![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=spring-security&logoColor=white) |    `6.x`    | Autenticación y Autorización |
|     ![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square&logo=spring&logoColor=white)      |    `3.x`    |    Persistencia de Datos     |
|          ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=flat-square&logo=hibernate&logoColor=white)          |   `6.6.8`   |        ORM Principal         |
|                ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)                |    `8.x`    |   Base de Datos Principal    |
|              ![Oracle](https://img.shields.io/badge/Oracle-F80000?style=flat-square&logo=oracle&logoColor=white)               | `19.3.0.0`  |     Base de Datos Legada     |

</div>

### Servicios en la Nube & Integración

<div align="center">

|                                                  **Servicio**                                                   | **Versión** |          **Uso**           |
| :-------------------------------------------------------------------------------------------------------------: | :---------: | :------------------------: |
|     ![AWS S3](https://img.shields.io/badge/AWS_S3-FF9900?style=flat-square&logo=amazon-aws&logoColor=white)     | `1.12.782`  | Almacenamiento de Archivos |
| ![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=flat-square&logo=spring&logoColor=white) |   `2.2.6`   |    Integración con AWS     |
|       ![OAuth2](https://img.shields.io/badge/OAuth2-4285F4?style=flat-square&logo=google&logoColor=white)       |  `latest`   |    Autenticación Google    |

</div>

### Seguridad & Tokens

<div align="center">

|                                            **Herramienta**                                             | **Versión** |         **Función**         |
| :----------------------------------------------------------------------------------------------------: | :---------: | :-------------------------: |
| ![JWT](https://img.shields.io/badge/JWT-000000?style=flat-square&logo=json-web-tokens&logoColor=white) |   `0.9.1`   |   Tokens de Autenticación   |
|        ![BCrypt](https://img.shields.io/badge/BCrypt-FF6B6B?style=flat-square&logoColor=white)         |  `latest`   | Encriptación de Contraseñas |

</div>

### Herramientas de Desarrollo

<div align="center">

|                                                **Tool**                                                 | **Versión** |          **Propósito**          |
| :-----------------------------------------------------------------------------------------------------: | :---------: | :-----------------------------: |
|         ![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=flat-square&logoColor=white)         |  `latest`   | Reducción de Código Boilerplate |
|  ![Flying Saucer](https://img.shields.io/badge/Flying_Saucer-9B59B6?style=flat-square&logoColor=white)  |  `9.1.22`   |       Generación de PDFs        |
|        ![Jackson](https://img.shields.io/badge/Jackson-2E86C1?style=flat-square&logoColor=white)        |  `latest`   |     Serialización JSON/XML      |
| ![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apache-maven&logoColor=white) |  `latest`   |     Gestión de Dependencias     |

</div>

## 🏗️ Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (Angular/React)                 │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP/REST API
┌─────────────────────▼───────────────────────────────────────┐
│                SPRING BOOT APPLICATION                      │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────────────┐    │
│  │ Controllers │ │  Services   │ │   Security Config   │    │
│  └─────────────┘ └─────────────┘ └─────────────────────┘    │
└─────────────────────┬───────────────────────────────────────┘
                      │
      ┌───────────────┼───────────────┐
      │               │               │
┌─────▼─────┐ ┌──────▼──────┐ ┌──────▼──────┐
│   MySQL   │ │   Moodle    │ │  AWS S3     │
│(Principal)│ │ Integration | │  (Archivos) │
└───────────┘ └─────────────┘ └─────────────┘
                  
```

## 📦 Instalación y Configuración

### Prerrequisitos

- **Java**: JDK 17 o superior
- **Maven**: 3.6 o superior
- **MySQL**: 8.0 o superior
- **Oracle Database**: 11g o superior (opcional)
- **AWS Account**: Para S3 (opcional)

### 🔧 Configuración de Base de Datos

#### MySQL (Principal)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/edu_virtual_ufps
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

#### Oracle (Datos Legados)

```properties
oracle.datasource.url=jdbc:oracle:thin:@localhost:1521:xe
oracle.datasource.username=tu_usuario_oracle
oracle.datasource.password=tu_password_oracle
```

### 🚀 Pasos de Instalación

1. **Clonar el repositorio**

```bash
git clone https://github.com/tu-usuario/edu_virtual_ufps_be.git
cd edu_virtual_ufps_be
```

2. **Configurar variables de entorno**

```bash
# Crear archivo application-local.properties
cp src/main/resources/application.properties src/main/resources/application-local.properties
```

3. **Configurar credenciales**

```properties
# Google OAuth2
spring.security.oauth2.client.registration.google.client-id=TU_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=TU_CLIENT_SECRET

# AWS S3
cloud.aws.credentials.access-key=TU_ACCESS_KEY
cloud.aws.credentials.secretkey=TU_SECRET_KEY
cloud.aws.region.static=us-east-1
```

4. **Instalar dependencias y ejecutar**

```bash
mvn clean install
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

## 🔐 Autenticación y Seguridad

### Métodos de Autenticación Soportados

1. **JWT Tokens**: Para autenticación de API
2. **OAuth2 Google**: Login social
3. **Autenticación básica**: Usuario y contraseña

### Endpoints de Autenticación

```http
POST /auth/login           # Login tradicional
GET  /oauth2/authorization/google  # Login con Google
POST /auth/refresh         # Renovar token
POST /auth/logout          # Cerrar sesión
```

### Roles del Sistema

- **ADMIN**: Administrador general
- **COORDINADOR**: Coordinador académico
- **DOCENTE**: Profesor
- **ESTUDIANTE**: Estudiante

## 📚 API Endpoints Principales

### 👨‍🎓 Gestión de Estudiantes

```http
GET    /estudiantes                    # Listar todos
GET    /estudiantes/{id}               # Obtener por ID
POST   /estudiantes                    # Crear estudiante
PUT    /estudiantes/{id}               # Actualizar estudiante
DELETE /estudiantes/{id}               # Eliminar estudiante
```

### 📝 Gestión de Matrículas

```http
GET    /matriculas/estudiante/{id}     # Matrículas por estudiante
POST   /matriculas/crear               # Crear matrícula
DELETE /matriculas/{id}                # Anular matrícula
GET    /matriculas/pensum/estudiante/{id}  # Pensum por estudiante
```

### 📊 Gestión de Notas

```http
GET    /notas/pregrado/{grupoId}       # Notas pregrado
POST   /notas/pregrado                 # Registrar notas pregrado
GET    /notas/posgrado/{matriculaId}   # Notas posgrado
POST   /notas/posgrado                 # Registrar notas posgrado
```

### 🏫 Gestión de Programas

```http
GET    /programas                      # Listar programas
GET    /programas/{id}                 # Programa por ID
POST   /programas                      # Crear programa
PUT    /programas/{id}                 # Actualizar programa
```

### 📑 Gestión de Solicitudes

```http
GET    /solicitudes/estudiante/{id}    # Solicitudes por estudiante
POST   /solicitudes                    # Crear solicitud
PUT    /solicitudes/{id}/aprobar       # Aprobar solicitud
```

## 🗄️ Modelo de Base de Datos

### Entidades Principales

#### Estudiantes

```sql
CREATE TABLE estudiantes (
    id INT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(15),
    cedula VARCHAR(20),
    fecha_nacimiento DATE,
    fecha_ingreso DATE,
    es_posgrado BOOLEAN,
    pensum_id INT,
    programa_id INT,
    estado_estudiante_id INT,
    usuario_id INT
);
```

#### Matrículas

```sql
CREATE TABLE matriculas (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    estudiante_id INT NOT NULL,
    grupo_cohorte_id BIGINT NOT NULL,
    estado_matricula_id INT NOT NULL,
    fecha_matriculacion DATETIME,
    nueva_matricula BOOLEAN,
    nota DOUBLE,
    fecha_nota DATETIME,
    semestre VARCHAR(10),
    correo_enviado BOOLEAN,
    nota_abierta BOOLEAN
);
```

#### Programas Académicos

```sql
CREATE TABLE programas (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(200) NOT NULL,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    es_posgrado BOOLEAN NOT NULL,
    moodle_id VARCHAR(50) UNIQUE,
    semestre_actual VARCHAR(10),
    tipo_programa_id INT
);
```

## 🔧 Configuración Avanzada

### Perfiles de Ejecución

- **dev**: Desarrollo local con MySQL
- **oracle**: Incluye configuración Oracle
- **prod**: Producción

```bash
# Ejecutar con perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=dev,oracle
```

### Configuración de CORS

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:4200",  // Angular
        "http://localhost:3000"   // React
    ));
    configuration.setAllowedMethods(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "OPTIONS"
    ));
    return source;
}
```

## 🔄 Integración con Moodle

El sistema incluye servicios para sincronizar datos con Moodle:

- **Creación automática de cursos**
- **Matrícula de estudiantes**
- **Sincronización de notas**
- **Gestión de categorías**

```java
@Service
public class MoodleService {
    public void crearCurso(GrupoCohorte grupoCohorte);
    public void matricularEstudiante(Estudiante estudiante, String moodleCourseId);
    public void sincronizarNotas(Long grupoCohorteId);
}
```

## 📝 Logging y Monitoreo

### Configuración de Logs

```properties
logging.level.com.sistemas_mangager_be=DEBUG
logging.file.name=logs/edu-virtual-ufps.log
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

### Métricas Disponibles

- Número de estudiantes activos
- Matrículas por semestre
- Uso de endpoints de API
- Errores de sincronización con Moodle

## 🧪 Testing

### Ejecutar Tests

```bash
# Tests unitarios
mvn test

# Tests de integración
mvn integration-test

# Tests con perfiles específicos
mvn test -Dspring.profiles.active=test
```

### Cobertura de Tests

```bash
mvn jacoco:report
```

## 🚀 Despliegue

### Construcción para Producción

```bash
# Generar JAR ejecutable
mvn clean package -Pprod

# El archivo se genera en:
target/edu_virtual_ufps-0.0.1-SNAPSHOT.jar
```

### Docker

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/edu_virtual_ufps-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# Construir imagen
docker build -t siav .

# Ejecutar contenedor
docker run -p 8080:8080 siav
```

## 🤝 Contribución

### Flujo de Trabajo

1. **Fork** el repositorio
2. Crear rama feature: `git checkout -b feature/nueva-funcionalidad`
3. **Commit** cambios: `git commit -m 'Agregar nueva funcionalidad'`
4. **Push** a la rama: `git push origin feature/nueva-funcionalidad`
5. Crear **Pull Request**

### Estándares de Código

- Seguir convenciones de Spring Boot
- Usar Lombok para reducir boilerplate
- Documentar métodos complejos
- Escribir tests para nuevas funcionalidades
- Usar nombres descriptivos para variables y métodos

### Estructura de Commits

```
feat: agregar endpoint para gestión de notas
fix: corregir validación de email en estudiantes
docs: actualizar documentación de API
test: agregar tests para servicio de matrículas
```





<div align="center">

**SIAV** - Sistema de Informacion Academica Virtual 
Universidad Francisco de Paula Santander

[![Spring Boot](https://img.shields.io/badge/Powered%20by-Spring%20Boot-6DB33F?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-4479A1?style=flat-square&logo=mysql)](https://www.mysql.com/)

</div>

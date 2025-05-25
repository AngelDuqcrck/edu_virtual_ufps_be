package com.sistemas_mangager_be.edu_virtual_ufps.services.moodle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MoodleApiClient {

    private final RestTemplate restTemplate;
    private final String moodleApiUrl;
    private final String moodleApiToken;

    public MoodleApiClient(
            RestTemplate restTemplate,
            @Value("${moodle.api.url}") String moodleApiUrl,
            @Value("${moodle.api.token}") String moodleApiToken) {
        this.restTemplate = restTemplate;
        this.moodleApiUrl = moodleApiUrl;
        this.moodleApiToken = moodleApiToken;
    }

    /**
     * Matricula un estudiante en un curso de Moodle
     * 
     * @param moodleUserId   ID del estudiante en Moodle
     * @param moodleCourseId ID del curso en Moodle
     * @param roleId         Rol del estudiante (5 = estudiante)
     * @return Resultado de la operación
     */
    public String matricularEstudiante(String moodleUserId, String moodleCourseId, int roleId) {
        log.info("Matriculando estudiante con ID {} en curso {}", moodleUserId, moodleCourseId);

        // Creación de los parámetros individualmente según formato esperado por Moodle
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "enrol_manual_enrol_users");
        params.add("moodlewsrestformat", "json");

        // En lugar de un JSON string, Moodle espera parámetros con formato específico
        // Para cada enrolment debemos crear: enrolments[0][roleid],
        // enrolments[0][userid], enrolments[0][courseid]
        params.add("enrolments[0][roleid]", String.valueOf(roleId));
        params.add("enrolments[0][userid]", moodleUserId);
        params.add("enrolments[0][courseid]", moodleCourseId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición a Moodle para matricular: {}", params);
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.info("Respuesta de Moodle: {}", response);

            // Una respuesta nula o vacía generalmente indica éxito en Moodle
            if (response == null || response.isEmpty() || "null".equals(response)) {
                return "Matriculación exitosa";
            }

            return response;
        } catch (Exception e) {
            log.error("Error al matricular estudiante en Moodle: {}", e.getMessage(), e);
            throw new RuntimeException("Error al matricular estudiante en Moodle: " + e.getMessage());
        }
    }

    /**
     * Usado para cancelar la matricula de un estudiante en un curso de Moodle
     * Suspendiendo la matricula del estudiante en el curso
     * 
     * @param moodleUserId   ID del estudiante en Moodle
     * @param moodleCourseId ID del curso en Moodle
     * @param roleId         Rol del estudiante (5 = estudiante)
     * @return Resultado de la operación
     */
    public String cancelarMatriculaSemestre(String moodleUserId, String moodleCourseId, int roleId) {
        log.info("Matriculando estudiante con ID {} en curso {}", moodleUserId, moodleCourseId);

        // Creación de los parámetros individualmente según formato esperado por Moodle
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "enrol_manual_enrol_users");
        params.add("moodlewsrestformat", "json");

        // En lugar de un JSON string, Moodle espera parámetros con formato específico
        // Para cada enrolment debemos crear: enrolments[0][roleid],
        // enrolments[0][userid], enrolments[0][courseid]
        params.add("enrolments[0][roleid]", String.valueOf(roleId));
        params.add("enrolments[0][userid]", moodleUserId);
        params.add("enrolments[0][courseid]", moodleCourseId);
        params.add("enrolments[0][suspend]", "1"); // Suspender la matricula

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición a Moodle para matricular: {}", params);
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.info("Respuesta de Moodle: {}", response);

            // Una respuesta nula o vacía generalmente indica éxito en Moodle
            if (response == null || response.isEmpty() || "null".equals(response)) {
                return "Matriculación exitosa";
            }

            return response;
        } catch (Exception e) {
            log.error("Error al matricular estudiante en Moodle: {}", e.getMessage(), e);
            throw new RuntimeException("Error al matricular estudiante en Moodle: " + e.getMessage());
        }
    }

    /**
     * Desmatricula un estudiante de un curso de Moodle
     * 
     * @param moodleUserId   ID del estudiante en Moodle
     * @param moodleCourseId ID del curso en Moodle
     * @return Resultado de la operación
     */
    public String desmatricularEstudiante(String moodleUserId, String moodleCourseId) {
        log.info("Desmatriculando estudiante con ID {} del curso {}", moodleUserId, moodleCourseId);

        // Creación de los parámetros individualmente según formato esperado por Moodle
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "enrol_manual_unenrol_users");
        params.add("moodlewsrestformat", "json");

        // Para cada desenrolment debemos crear: enrolments[0][userid],
        // enrolments[0][courseid]
        params.add("enrolments[0][userid]", moodleUserId);
        params.add("enrolments[0][courseid]", moodleCourseId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición a Moodle para desmatricular: {}", params);
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.info("Respuesta de Moodle: {}", response);

            // Una respuesta nula o vacía generalmente indica éxito en Moodle
            if (response == null || response.isEmpty() || "null".equals(response)) {
                return "Desmatriculación exitosa";
            }

            return response;
        } catch (Exception e) {
            log.error("Error al desmatricular estudiante en Moodle: {}", e.getMessage(), e);
            throw new RuntimeException("Error al desmatricular estudiante en Moodle: " + e.getMessage());
        }
    }

    /**
     * Copia un curso de Moodle a una categoría específica
     * 
     * @param cursoOrigenId      ID del curso a copiar
     * @param categoriaDestinoId ID de la categoría destino
     * @param nombreCurso        Nombre para el nuevo curso
     * @return ID del nuevo curso
     */
    public String copiarCurso(String cursoOrigenId, String categoriaDestinoId, String nombreCurso) {
        log.info("Copiando curso {} a la categoría {}", cursoOrigenId, categoriaDestinoId);

        String shortName = nombreCurso.replaceAll("\\s+", "_");
        if (shortName.length() > 15) {
            shortName = shortName.substring(0, 12) + "_" + System.currentTimeMillis() % 1000;
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "core_course_duplicate_course");
        params.add("moodlewsrestformat", "json");
        params.add("courseid", cursoOrigenId);
        params.add("fullname", nombreCurso);
        params.add("shortname", shortName);
        params.add("categoryid", categoriaDestinoId);
        params.add("visible", "0");

        // Incluir todos los componentes del curso
        params.add("options[0][name]", "users");
        params.add("options[0][value]", "1"); // Incluir usuarios
        params.add("options[1][name]", "activities");
        params.add("options[1][value]", "1"); // Incluir actividades
        params.add("options[2][name]", "blocks");
        params.add("options[2][value]", "1"); // Incluir bloques
        params.add("options[3][name]", "filters");
        params.add("options[3][value]", "1"); // Incluir filtros

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición para duplicar curso en Moodle");

            ObjectMapper objectMapper = new ObjectMapper();
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.debug("Respuesta de Moodle (duplicar curso): {}", response);

            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("id")) {
                String courseId = rootNode.path("id").asText();
                log.info("Curso duplicado con ID: {}", courseId);
                return courseId;
            } else if (response.contains("exception")) {
                throw new RuntimeException("Error de Moodle al duplicar curso: " + response);
            }

            return null;
        } catch (Exception e) {
            log.error("Error al duplicar curso en Moodle: {}", e.getMessage(), e);
            throw new RuntimeException("Error al duplicar curso en Moodle: " + e.getMessage());
        }
    }

    /**
     * Copia un curso de Moodle sin incluir estudiantes, solo docentes
     * 
     * @param cursoOrigenId      ID del curso a copiar
     * @param categoriaDestinoId ID de la categoría destino
     * @param nombreCurso        Nombre para el nuevo curso
     * @param shortName          Nombre corto del curso
     * @return ID del nuevo curso
     */
    public String copiarCursoSinEstudiantes(String cursoOrigenId, String categoriaDestinoId, String nombreCurso,
            String shortName) {
        log.info("Copiando curso {} a la categoría {} sin estudiantes", cursoOrigenId, categoriaDestinoId);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "core_course_duplicate_course");
        params.add("moodlewsrestformat", "json");
        params.add("courseid", cursoOrigenId);
        params.add("fullname", nombreCurso);
        params.add("shortname", shortName);
        params.add("categoryid", categoriaDestinoId);
        params.add("visible", "1"); // El curso duplicado debe estar visible

        // Configuración para NO incluir usuarios pero SÍ asignaciones de roles
        // Esto hace que solo se copien los docentes y no los estudiantes
        params.add("options[0][name]", "users");
        params.add("options[0][value]", "0"); // No incluir usuarios
        params.add("options[1][name]", "role_assignments");
        params.add("options[1][value]", "1"); // Mantener asignaciones de roles
        params.add("options[2][name]", "activities");
        params.add("options[2][value]", "1"); // Incluir actividades
        params.add("options[3][name]", "blocks");
        params.add("options[3][value]", "1"); // Incluir bloques
        params.add("options[4][name]", "filters");
        params.add("options[4][value]", "1"); // Incluir filtros

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición para duplicar curso sin estudiantes en Moodle");

            ObjectMapper objectMapper = new ObjectMapper();
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.debug("Respuesta de Moodle (duplicar curso sin estudiantes): {}", response);

            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("id")) {
                String courseId = rootNode.path("id").asText();
                log.info("Curso duplicado con ID: {}", courseId);

                // Actualizar el idnumber para que coincida con el shortname
                actualizarCurso(courseId, null, null, null, null, shortName);

                return courseId;
            } else if (response.contains("exception")) {
                throw new RuntimeException("Error de Moodle al duplicar curso: " + response);
            }

            return null;
        } catch (Exception e) {
            log.error("Error al duplicar curso en Moodle: {}", e.getMessage(), e);
            throw new RuntimeException("Error al duplicar curso en Moodle: " + e.getMessage());
        }
    }

    /**
     * Actualiza un curso en Moodle (cambio de nombre, categoría, etc.)
     * 
     * @param cursoId        ID del curso a actualizar
     * @param nombreCompleto El nombre completo del curso (puede ser null para no
     *                       modificarlo)
     * @param nombreCorto    Nombre corto del curso (puede ser null para no
     *                       modificarlo)
     * @param categoriaId    ID de la nueva categoría (puede ser null para no
     *                       modificarla)
     * @param visible        Si el curso está visible (1) o no (0) (puede ser null
     *                       para no modificarlo)
     * @param idNumber       ID number del curso (puede ser null para no
     *                       modificarlo)
     * @return Resultado de la operación
     */
    public String actualizarCurso(String cursoId, String nombreCompleto, String nombreCorto,
            String categoriaId, String visible, String idNumber) {
        log.info("Actualizando curso {} con nombre: {}, shortname: {}, idnumber: {}",
                cursoId, nombreCompleto, nombreCorto, idNumber);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "core_course_update_courses");
        params.add("moodlewsrestformat", "json");
        params.add("courses[0][id]", cursoId);

        // Solo añadir los parámetros que no son null
        if (nombreCompleto != null) {
            params.add("courses[0][fullname]", nombreCompleto);
        }

        if (nombreCorto != null) {
            params.add("courses[0][shortname]", nombreCorto);
        }

        if (categoriaId != null) {
            params.add("courses[0][categoryid]", categoriaId);
        }

        if (visible != null) {
            params.add("courses[0][visible]", visible);
        }

        if (idNumber != null) {
            params.add("courses[0][idnumber]", idNumber);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición para actualizar curso en Moodle");
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.debug("Respuesta de Moodle (actualizar curso): {}", response);

            // Una respuesta nula o vacía generalmente indica éxito en Moodle
            if (response == null || response.isEmpty() || "null".equals(response)) {
                log.info("Curso actualizado exitosamente");
                return "Curso actualizado exitosamente";
            }

            return response;
        } catch (Exception e) {
            log.error("Error al actualizar curso en Moodle: {}", e.getMessage(), e);
            throw new RuntimeException("Error al actualizar curso en Moodle: " + e.getMessage());
        }
    }

    /**
     * Crea una categoría en Moodle con idNumber personalizado según el tipo
     * 
     * @param nombre           Nombre de la categoría
     * @param parentId         ID de la categoría padre
     * @param periodoAcademico Período académico (opcional, puede ser null)
     * @param codigoPrograma   Código del programa (opcional, puede ser null)
     * @return ID de la nueva categoría
     */
    public String crearCategoria(String nombre, String parentId, String periodoAcademico, String codigoPrograma) {
        log.info("Creando categoría '{}' bajo la categoría padre {} con periodo {} y código programa {}",
                nombre, parentId, periodoAcademico, codigoPrograma);

        // Primero verificar si la categoría ya existe
        String existingCategoryId = buscarCategoria(nombre, parentId);
        if (existingCategoryId != null) {
            log.info("La categoría ya existe, usando la categoría existente con ID: {}", existingCategoryId);
            return existingCategoryId;
        }

        // Generar el idNumber según el tipo de categoría
        String idNumber;

        if (nombre.startsWith("Semestre ")) {
            // Para categorías de semestre, formato: "CodigoPrograma-SemestreX-Periodo"
            String semestreRomano = nombre.replace("Semestre ", "");
            if (codigoPrograma != null && periodoAcademico != null) {
                // Primera letra en mayúscula, resto en minúscula
                String semestreFormateado = "Semestre" + semestreRomano;
                idNumber = codigoPrograma + "-" + semestreFormateado + "-" + periodoAcademico;
            } else if (periodoAcademico != null) {
                // Si no hay código de programa pero sí periodo
                String semestreFormateado = "Semestre" + semestreRomano;
                idNumber = semestreFormateado + "-" + periodoAcademico;
            } else {
                // Si no hay periodo ni código
                idNumber = "Semestre" + semestreRomano;
            }
        } else if (nombre.matches("\\d{4}-[I|II]")) {
            // Para categorías de período académico (2025-I), añadir código programa
            if (codigoPrograma != null && !codigoPrograma.isEmpty()) {
                idNumber = codigoPrograma + "-" + nombre; // Ej: "115-2025-I"
            } else {
                idNumber = nombre; // Mantener como está si no hay código de programa
            }
        } else if (nombre.contains(" - ") && periodoAcademico != null) {
            // Para categorías de materia con formato "CODIGO - NOMBRE"
            String codigo = nombre.split(" - ")[0].trim();
            if (codigoPrograma != null && !codigoPrograma.isEmpty()) {
                idNumber = codigoPrograma + "-" + codigo + "-" + periodoAcademico;
            } else {
                idNumber = codigo + "_" + periodoAcademico;
            }
        } else if (esCodigoMateria(nombre) && periodoAcademico != null) {
            // Para categorías que solo tienen código de materia
            if (codigoPrograma != null && !codigoPrograma.isEmpty()) {
                idNumber = codigoPrograma + "-" + nombre.replaceAll("\\s+", "") + "-" + periodoAcademico;
            } else {
                idNumber = nombre.replaceAll("\\s+", "") + "_" + periodoAcademico;
            }
        } else {
            // Formato genérico para otros tipos de categorías
            idNumber = nombre.replaceAll("\\s+", "_").toLowerCase();
        }

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "core_course_create_categories");
        params.add("moodlewsrestformat", "json");
        params.add("categories[0][name]", nombre);
        params.add("categories[0][parent]", parentId);
        params.add("categories[0][idnumber]", idNumber);
        params.add("categories[0][description]", "Categoría creada automáticamente por el sistema");
        params.add("categories[0][descriptionformat]", "1"); // 1 = HTML

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición para crear categoría en Moodle");

            ObjectMapper objectMapper = new ObjectMapper();
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.debug("Respuesta de Moodle (crear categoría): {}", response);

            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.isArray() && rootNode.size() > 0) {
                String categoryId = rootNode.get(0).path("id").asText();
                log.info("Categoría creada con ID: {} e idNumber: {}", categoryId, idNumber);
                return categoryId;
            } else if (response.contains("exception")) {
                throw new RuntimeException("Error de Moodle al crear categoría: " + response);
            }

            return null;
        } catch (Exception e) {
            log.error("Error al crear categoría en Moodle: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear categoría en Moodle: " + e.getMessage());
        }
    }

    /**
     * Sobrecarga del método crearCategoria para mantener compatibilidad con código
     * existente
     */
    public String crearCategoria(String nombre, String parentId, String periodoAcademico) {
        // Llamada al método con codigoPrograma null para mantener compatibilidad
        return crearCategoria(nombre, parentId, periodoAcademico, null);
    }

    /**
     * Crea una categoría específica para materia en histórico con formato especial
     * 
     * @param nombreMateria    Nombre completo de la materia
     * @param codigoMateria    Código de la materia
     * @param periodoAcademico Período académico (2025-I, etc.)
     * @param categoriaPadreId ID de la categoría padre (semestre romano)
     * @return ID de la categoría creada
     */
    public String crearCategoriaMateriaHistorico(String nombreMateria, String codigoMateria,
            String periodoAcademico, String categoriaPadreId) {
        // Formato especial para categorías de materia en histórico
        // Nombre: Nombre de la materia - Periodo Académico
        String nombreCategoria = nombreMateria + " - " + periodoAcademico;

        // idNumber: Código de la materia - Periodo Académico (sin código de programa)
        String idNumber = codigoMateria + "-" + periodoAcademico;

        log.info("Creando categoría de materia en histórico: '{}' con idNumber '{}'",
                nombreCategoria, idNumber);

        // Primero verificar si la categoría ya existe
        String existingCategoryId = buscarCategoria(nombreCategoria, categoriaPadreId);
        if (existingCategoryId != null) {
            log.info("La categoría de materia ya existe, usando la existente con ID: {}", existingCategoryId);
            return existingCategoryId;
        }

        // Crear la categoría con formato personalizado
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "core_course_create_categories");
        params.add("moodlewsrestformat", "json");
        params.add("categories[0][name]", nombreCategoria);
        params.add("categories[0][parent]", categoriaPadreId);
        params.add("categories[0][idnumber]", idNumber);
        params.add("categories[0][description]", "Categoría de materia histórica creada automáticamente");
        params.add("categories[0][descriptionformat]", "1"); // 1 = HTML

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición para crear categoría de materia en histórico");

            ObjectMapper objectMapper = new ObjectMapper();
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.debug("Respuesta de Moodle (crear categoría de materia): {}", response);

            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.isArray() && rootNode.size() > 0) {
                String categoryId = rootNode.get(0).path("id").asText();
                log.info("Categoría de materia en histórico creada con ID: {} e idNumber: {}", categoryId, idNumber);
                return categoryId;
            } else if (response.contains("exception")) {
                throw new RuntimeException("Error de Moodle al crear categoría de materia en histórico: " + response);
            }

            return null;
        } catch (Exception e) {
            log.error("Error al crear categoría de materia en histórico: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear categoría de materia en histórico: " + e.getMessage());
        }
    }

    /**
     * Busca categorías de cursos en Moodle por su nombre
     * 
     * @param nombre   Nombre de la categoría a buscar
     * @param parentId ID de la categoría padre (opcional)
     * @return ID de la categoría si existe, null en caso contrario
     */
    public String buscarCategoria(String nombre, String parentId) {
        log.info("Buscando categoría '{}' bajo la categoría padre {}", nombre, parentId);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "core_course_get_categories");
        params.add("moodlewsrestformat", "json");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición para buscar categorías en Moodle");

            ObjectMapper objectMapper = new ObjectMapper();
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.debug("Respuesta de Moodle (buscar categorías): {}", response);

            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.isArray()) {
                for (JsonNode category : rootNode) {
                    String categoryName = category.path("name").asText();
                    String categoryParent = category.path("parent").asText();

                    // Verificar si coincide el nombre y el padre (si se especifica)
                    if (categoryName.equals(nombre) &&
                            (parentId == null || categoryParent.equals(parentId))) {
                        String categoryId = category.path("id").asText();
                        log.info("Categoría encontrada con ID: {}", categoryId);
                        return categoryId;
                    }
                }
            }

            log.info("No se encontró la categoría '{}'", nombre);
            return null;
        } catch (Exception e) {
            log.error("Error al buscar categoría en Moodle: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Obtiene la categoría a la que pertenece un curso en Moodle
     * 
     * @param cursoId ID del curso
     * @return ID de la categoría si se encuentra, null en caso contrario
     */
    public String obtenerCategoriaCurso(String cursoId) {
        log.info("Obteniendo información de categoría para el curso {}", cursoId);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("wstoken", moodleApiToken);
        params.add("wsfunction", "core_course_get_courses_by_field");
        params.add("moodlewsrestformat", "json");
        params.add("field", "id");
        params.add("value", cursoId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            log.debug("Enviando petición para obtener información del curso en Moodle");

            ObjectMapper objectMapper = new ObjectMapper();
            String response = restTemplate.postForObject(moodleApiUrl, request, String.class);
            log.debug("Respuesta de Moodle (obtener curso): {}", response);

            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("courses") && rootNode.path("courses").isArray() && rootNode.path("courses").size() > 0) {
                JsonNode course = rootNode.path("courses").get(0);

                if (course.has("categoryid")) {
                    String categoryId = course.path("categoryid").asText();
                    log.info("Categoría del curso {} obtenida: {}", cursoId, categoryId);
                    return categoryId;
                }
            }

            log.warn("No se encontró información de categoría para el curso {}", cursoId);
            return null;
        } catch (Exception e) {
            log.error("Error al obtener categoría del curso en Moodle: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Determina si el nombre parece ser un código de materia
     */
    private boolean esCodigoMateria(String nombre) {
        // Asumimos que un código de materia comienza con números
        return nombre.matches("^\\d+.*");
    }

    /**
     * Obtiene la URL de la API de Moodle
     */
    public String getMoodleApiUrl() {
        return moodleApiUrl;
    }

    /**
     * Obtiene el token de la API de Moodle
     */
    public String getMoodleApiToken() {
        return moodleApiToken;
    }
}

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
 * Crea una categoría en Moodle
 * 
 * @param nombre Nombre de la categoría
 * @param parentId ID de la categoría padre
 * @return ID de la nueva categoría
 */
public String crearCategoria(String nombre, String parentId) {
    log.info("Creando categoría '{}' bajo la categoría padre {}", nombre, parentId);
    
    // Primero verificar si la categoría ya existe
    String existingCategoryId = buscarCategoria(nombre, parentId);
    if (existingCategoryId != null) {
        log.info("La categoría ya existe, usando la categoría existente con ID: {}", existingCategoryId);
        return existingCategoryId;
    }
    
    // Generar un idnumber único agregando un timestamp
    String idNumber = nombre.replaceAll("\\s+", "_").toLowerCase() + "_" + System.currentTimeMillis();
    
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
            log.info("Categoría creada con ID: {}", categoryId);
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
 * Busca categorías de cursos en Moodle por su nombre
 * 
 * @param nombre Nombre de la categoría a buscar
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
}

package com.sistemas_mangager_be.edu_virtual_ufps.services.moodle;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
}

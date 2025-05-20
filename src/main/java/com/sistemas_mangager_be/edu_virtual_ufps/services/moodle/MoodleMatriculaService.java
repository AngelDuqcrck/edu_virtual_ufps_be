package com.sistemas_mangager_be.edu_virtual_ufps.services.moodle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MatriculaException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MoodleMatriculaService {

    private static final int ROLE_STUDENT = 5; // ID del rol estudiante en Moodle

    @Autowired
    private MoodleApiClient moodleApiClient;

    /**
     * Matricular un estudiante en un curso de Moodle
     * 
     * @param estudiante   Entidad de estudiante
     * @param grupoCohorte Entidad de grupo cohorte (curso)
     * @throws MatriculaException Si ocurre un error durante la matriculación
     */
    public void matricularEstudianteEnMoodle(Estudiante estudiante, GrupoCohorte grupoCohorte)
            throws MatriculaException {

        // Verificar que existan los IDs de Moodle
        if (estudiante.getMoodleId() == null || estudiante.getMoodleId().isEmpty()) {
            throw new MatriculaException("El estudiante no tiene un ID de Moodle asociado");
        }

        if (grupoCohorte.getMoodleId() == null || grupoCohorte.getMoodleId().isEmpty()) {
            throw new MatriculaException("El curso no tiene un ID de Moodle asociado");
        }

        try {
            // Realizar la matriculación en Moodle
            String resultado = moodleApiClient.matricularEstudiante(
                    estudiante.getMoodleId(),
                    grupoCohorte.getMoodleId(),
                    ROLE_STUDENT);

            // Verificar si la respuesta contiene un error
            if (resultado.contains("exception") || resultado.contains("error")) {
                throw new MatriculaException("Error en la respuesta de Moodle: " + resultado);
            }

            log.info("Estudiante {} (ID Moodle: {}) matriculado exitosamente en curso {} (ID Moodle: {})",
                    estudiante.getCodigo(),
                    estudiante.getMoodleId(),
                    grupoCohorte.getGrupoId().getCodigo(),
                    grupoCohorte.getMoodleId());

        } catch (Exception e) {
            log.error("Error al matricular estudiante en Moodle: {}", e.getMessage(), e);
            throw new MatriculaException("Error al matricular en Moodle: " + e.getMessage());
        }
    }

    /**
     * Desmatricular un estudiante de un curso de Moodle
     * 
     * @param estudiante   Entidad de estudiante
     * @param grupoCohorte Entidad de grupo cohorte (curso)
     * @throws MatriculaException Si ocurre un error durante la desmatriculación
     */
    public void desmatricularEstudianteEnMoodle(Estudiante estudiante, GrupoCohorte grupoCohorte)
            throws MatriculaException {

        // Verificar que existan los IDs de Moodle
        if (estudiante.getMoodleId() == null || estudiante.getMoodleId().isEmpty()) {
            throw new MatriculaException("El estudiante no tiene un ID de Moodle asociado");
        }

        if (grupoCohorte.getMoodleId() == null || grupoCohorte.getMoodleId().isEmpty()) {
            throw new MatriculaException("El curso no tiene un ID de Moodle asociado");
        }

        try {
            // Realizar la desmatriculación en Moodle
            String resultado = moodleApiClient.desmatricularEstudiante(
                    estudiante.getMoodleId(),
                    grupoCohorte.getMoodleId());

            // Verificar si la respuesta contiene un error
            if (resultado.contains("exception") || resultado.contains("error")) {
                throw new MatriculaException("Error en la respuesta de Moodle: " + resultado);
            }

            log.info("Estudiante {} (ID Moodle: {}) desmatriculado exitosamente del curso {} (ID Moodle: {})",
                    estudiante.getCodigo(),
                    estudiante.getMoodleId(),
                    grupoCohorte.getGrupoId().getCodigo(),
                    grupoCohorte.getMoodleId());

        } catch (Exception e) {
            log.error("Error al desmatricular estudiante en Moodle: {}", e.getMessage(), e);
            throw new MatriculaException("Error al desmatricular en Moodle: " + e.getMessage());
        }
    }

    /**
     * Suspende la matrícula de un estudiante en un curso de Moodle (alternativa a
     * desmatricular)
     * 
     * @param estudiante   Entidad de estudiante
     * @param grupoCohorte Entidad de grupo cohorte (curso)
     * @throws MatriculaException Si ocurre un error durante la suspensión
     */
    public void suspenderMatriculaEnMoodle(Estudiante estudiante, GrupoCohorte grupoCohorte)
            throws MatriculaException {

        // Verificar que existan los IDs de Moodle
        if (estudiante.getMoodleId() == null || estudiante.getMoodleId().isEmpty()) {
            throw new MatriculaException("El estudiante no tiene un ID de Moodle asociado");
        }

        if (grupoCohorte.getMoodleId() == null || grupoCohorte.getMoodleId().isEmpty()) {
            throw new MatriculaException("El curso no tiene un ID de Moodle asociado");
        }

        try {
            // Realizar la suspensión de matrícula en Moodle
            String resultado = moodleApiClient.cancelarMatriculaSemestre(
                    estudiante.getMoodleId(),
                    grupoCohorte.getMoodleId(),
                    ROLE_STUDENT);

            // Verificar si la respuesta contiene un error
            if (resultado.contains("exception") || resultado.contains("error")) {
                throw new MatriculaException("Error en la respuesta de Moodle: " + resultado);
            }

            log.info("Estudiante {} (ID Moodle: {}) suspendido exitosamente en el curso {} (ID Moodle: {})",
                    estudiante.getCodigo(),
                    estudiante.getMoodleId(),
                    grupoCohorte.getGrupoId().getCodigo(),
                    grupoCohorte.getMoodleId());

        } catch (Exception e) {
            log.error("Error al suspender matrícula del estudiante en Moodle: {}", e.getMessage(), e);
            throw new MatriculaException("Error al suspender matrícula en Moodle: " + e.getMessage());
        }
    }
}
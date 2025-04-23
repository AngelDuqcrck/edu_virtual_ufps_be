package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Matricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Solicitud;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.TipoSolicitud;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SolicitudException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SolicitudRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SoporteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.TipoSolicitudRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.ISolicitudService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.SolicitudDTO;

@Service
public class SolicitudServiceImplementation implements ISolicitudService {

    public static final String IS_ALREADY_USE = "%s ya esta en uso";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valida";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es correcta";

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private TipoSolicitudRepository tipoSolicitudRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private MatriculaRepository matriculaRepository;

    @Autowired
    private SoporteRepository soporteRepository;

    @Autowired
    private CohorteGrupoRepository cohorteGrupoRepository;

    public Solicitud crearSolicitud(SolicitudDTO solicitudDTO, Integer tipoSolicitudId)
            throws SolicitudException, EstudianteNotFoundException {

        // Validar tipo de solicitud
        TipoSolicitud tipoSolicitud = tipoSolicitudRepository.findById(tipoSolicitudId)
                .orElseThrow(() -> new SolicitudException(
                        String.format(IS_NOT_FOUND_F, "Tipo de solicitud con ID: " + tipoSolicitudId)));

        // Validar estudiante
        Estudiante estudiante = estudianteRepository.findById(solicitudDTO.getEstudianteId())
                .orElseThrow(() -> new EstudianteNotFoundException(
                        String.format(IS_NOT_FOUND, "Estudiante con ID: " + solicitudDTO.getEstudianteId())));

        // Validaciones específicas por tipo de solicitud
        switch (tipoSolicitud.getId()) {
            case 1: // Cancelación de materias
                validarCancelacionMaterias(solicitudDTO, estudiante);
                break;

            case 2: // Aplazamiento de semestre
                validarAplazamientoSemestre(estudiante);
                break;

            case 3: // Reintegro
                validarReintegro(estudiante);
                break;

            default:
                throw new SolicitudException(String.format(IS_NOT_VALID, "Tipo de solicitud"));
        }

        // Crear la solicitud
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoSolicitudId(tipoSolicitud);
        solicitud.setEstudianteId(estudiante);
        solicitud.setFechaCreacion(new Date());
        solicitud.setEstaAprobada(false);

        // Asignar matrícula si es cancelación
        if (tipoSolicitud.getId() == 1) {
            Matricula matricula = matriculaRepository.findById(solicitudDTO.getMatriculaId())
                    .orElseThrow(() -> new SolicitudException(
                            String.format(IS_NOT_FOUND_F, "Matrícula con ID: " + solicitudDTO.getMatriculaId())));
            solicitud.setMatriculaId(matricula);
        }

        return solicitudRepository.save(solicitud);
    }

    public Solicitud actualizarSolicitud(Long solicitudId, Integer tipoSolicitudId, SolicitudDTO solicitudDTO)
            throws SolicitudException, EstudianteNotFoundException {

        // 1. Buscar la solicitud existente
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudException(
                        String.format(IS_NOT_FOUND_F, "Solicitud con ID: " + solicitudId)));

        // 2. Validar que el tipo de solicitud no cambie
        if (solicitud.getTipoSolicitudId().getId() != tipoSolicitudId) {
            throw new SolicitudException(
                    "Error al actualizar la solicitud, el tipo de solicitud no puede ser modificado");
        }

        // 3. Validar y actualizar estudiante si viene en el DTO
        if (solicitudDTO.getEstudianteId() != null &&
                !solicitudDTO.getEstudianteId().equals(solicitud.getEstudianteId().getId())) {

            Estudiante estudiante = estudianteRepository.findById(solicitudDTO.getEstudianteId())
                    .orElseThrow(() -> new EstudianteNotFoundException(
                            String.format(IS_NOT_FOUND, "Estudiante con ID: " + solicitudDTO.getEstudianteId())));

            // Validar que el nuevo estudiante cumpla con los requisitos del tipo de
            // solicitud
            validarEstudianteParaTipoSolicitud(estudiante, solicitud.getTipoSolicitudId());

            solicitud.setEstudianteId(estudiante);
        }

        // 4. Validar y actualizar matrícula si es cancelación y viene en el DTO
        if (solicitud.getTipoSolicitudId().getId() == 1 && solicitudDTO.getMatriculaId() != null) {

            // Verificar si la matrícula está siendo modificada
            if (solicitud.getMatriculaId() == null ||
                    !solicitudDTO.getMatriculaId().equals(solicitud.getMatriculaId().getId())) {

                Matricula matricula = matriculaRepository.findActiveByIdAndEstudianteId(
                        solicitudDTO.getMatriculaId(), solicitud.getEstudianteId())
                        .orElseThrow(() -> new SolicitudException(
                                "La matrícula no pertenece al estudiante o no está activa"));

                solicitud.setMatriculaId(matricula);
            }
        }

        // 6. Guardar los cambios
        return solicitudRepository.save(solicitud);
    }

    private void validarEstudianteParaTipoSolicitud(Estudiante estudiante, TipoSolicitud tipoSolicitud)
            throws SolicitudException {
        switch (tipoSolicitud.getId()) {
            case 1: // Cancelación de materias
                if (estudiante.getEstadoEstudianteId() == null ||
                        estudiante.getEstadoEstudianteId().getId() != 1) {
                    throw new SolicitudException("Solo estudiantes activos pueden tener solicitudes de cancelación");
                }
                break;

            case 2: // Aplazamiento de semestre
                validarAplazamientoSemestre(estudiante);
                break;

            case 3: // Reintegro
                validarReintegro(estudiante);
                break;
        }
    }

    // Métodos de validación específicos
    private void validarCancelacionMaterias(SolicitudDTO solicitudDTO, Estudiante estudiante)
            throws SolicitudException {
        if (solicitudDTO.getMatriculaId() == null) {
            throw new SolicitudException("Para cancelación de materias se requiere el ID de matrícula");
        }

        // Validar que la matrícula pertenece al estudiante y tiene estado activo
        Matricula matricula = matriculaRepository
                .findActiveByIdAndEstudianteId(solicitudDTO.getMatriculaId(), estudiante)
                .orElseThrow(() -> new SolicitudException("La matrícula no pertenece al estudiante"));

        // Validar que la matrícula esté en estado activo (2 = En curso)
        if (matricula.getEstadoMatriculaId() == null || matricula.getEstadoMatriculaId().getId() != 2) {
            throw new SolicitudException("Solo se pueden cancelar materias con matrícula en estado 'En curso'");
        }

        // Validar que el estudiante esté activo
        if (estudiante.getEstadoEstudianteId() == null || estudiante.getEstadoEstudianteId().getId() != 1) {
            throw new SolicitudException("Solo estudiantes activos pueden cancelar materias");
        }
    }

    private void validarReintegro(Estudiante estudiante) throws SolicitudException {
        // Validar que el estudiante esté inactivo
        if (estudiante.getEstadoEstudianteId() == null ||
                estudiante.getEstadoEstudianteId().getId() != 2) { // 2 = Inactivo
            throw new SolicitudException("Solo estudiantes inactivos pueden solicitar reintegro");
        }
    }

    private void validarAplazamientoSemestre(Estudiante estudiante) throws SolicitudException {
        // Validar que el estudiante tenga fecha de ingreso
        if (estudiante.getFechaIngreso() == null) {
            throw new SolicitudException("El estudiante no tiene fecha de ingreso registrada");
        }

        // Validar que no sea primer semestre basado en la fecha de ingreso
        if (esPrimerSemestre(estudiante.getFechaIngreso())) {
            throw new SolicitudException("No se puede solicitar aplazamiento en el primer semestre");
        }

        // Validar que el estudiante esté activo
        if (estudiante.getEstadoEstudianteId() == null || estudiante.getEstadoEstudianteId().getId() != 1) {
            throw new SolicitudException("Solo estudiantes activos pueden solicitar aplazamiento");
        }
    }

    private boolean esPrimerSemestre(Date fechaIngreso) {
        // Obtener el semestre actual
        String semestreActual = calcularSemestre(new Date());

        // Obtener el semestre de ingreso
        String semestreIngreso = calcularSemestre(fechaIngreso);

        // Comparar si son iguales (primer semestre)
        return semestreActual.equals(semestreIngreso);
    }

    private String calcularSemestre(Date fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fecha);

        int mes = cal.get(Calendar.MONTH) + 1; // Enero = 0
        int anio = cal.get(Calendar.YEAR);

        return anio + "-" + (mes <= 6 ? "I" : "II");
    }
}

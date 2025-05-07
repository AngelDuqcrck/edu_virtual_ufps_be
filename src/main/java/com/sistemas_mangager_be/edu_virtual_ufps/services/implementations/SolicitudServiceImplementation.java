package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.CambioEstadoMatricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoEstudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoMatricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Matricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Solicitud;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Soporte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.TipoSolicitud;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SolicitudException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CambioEstadoMatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstadoEstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstadoMatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SolicitudRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.SoporteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.TipoSolicitudRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.ISolicitudService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.SolicitudDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.SolicitudResponse;

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
    private CambioEstadoMatriculaRepository cambioEstadoMatriculaRepository;

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

    @Autowired
    private EstadoEstudianteRepository estadoEstudianteRepository;

    @Autowired
    private EstadoMatriculaRepository estadoMatriculaRepository;

    @Autowired
    private S3Service s3Service;

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
        solicitud.setDescripcion(solicitudDTO.getDescripcion());
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

        // Vincular reintegro con la última solicitud de aplazamiento aprobada
        if (tipoSolicitud.getId() == 3) { // Si es reintegro
            Optional<Solicitud> ultimoAplazamiento = solicitudRepository
                    .findLastAplazamientoAprobadoByEstudiante(estudiante);

            if (ultimoAplazamiento.isPresent()) {
                solicitud.setSolicitudAplazamientoId(ultimoAplazamiento.get());
            } else {
                throw new SolicitudException("No se encontró un aplazamiento aprobado previo.");
            }
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
        if (solicitudDTO.getEstudianteId() != null) {
            Estudiante estudiante = estudianteRepository.findById(solicitudDTO.getEstudianteId())
                    .orElseThrow(() -> new EstudianteNotFoundException(
                            String.format(IS_NOT_FOUND, "Estudiante con ID: " + solicitudDTO.getEstudianteId())));

            // Validar que el nuevo estudiante cumpla con los requisitos del tipo de
            // solicitud
            validarEstudianteParaTipoSolicitud(estudiante, solicitud.getTipoSolicitudId());

            // Si es reintegro, verificar que el nuevo estudiante tenga un aplazamiento
            // aprobado
            if (solicitud.getTipoSolicitudId().getId() == 3) { // Reintegro
                if (!solicitudRepository.existsAplazamientoAprobadoByEstudiante(estudiante)) {
                    throw new SolicitudException("El nuevo estudiante no tiene un aplazamiento aprobado previo.");
                }

                // Actualizar el vínculo con el último aplazamiento aprobado del nuevo
                // estudiante
                Optional<Solicitud> ultimoAplazamiento = solicitudRepository
                        .findLastAplazamientoAprobadoByEstudiante(estudiante);

                if (ultimoAplazamiento.isPresent()) {
                    solicitud.setSolicitudAplazamientoId(ultimoAplazamiento.get());
                } else {
                    throw new SolicitudException("No se encontró un aplazamiento aprobado para el nuevo estudiante.");
                }
            }

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
        solicitud.setDescripcion(solicitudDTO.getDescripcion());

        // 6. Guardar los cambios
        return solicitudRepository.save(solicitud);
    }

    public SolicitudResponse listarSolicitudPorId(Long id) throws SolicitudException {
        Solicitud solicitud = solicitudRepository.findById(id).orElse(null);
        if (solicitud == null) {
            throw new SolicitudException(String.format(IS_NOT_FOUND_F, "La solicitud con ID: " + id));
        }

        SolicitudResponse solicitudResponse = new SolicitudResponse();
        BeanUtils.copyProperties(solicitud, solicitudResponse);
        if (solicitud.getTipoSolicitudId().getId() == 1) {

            solicitudResponse.setGrupoCohorteId(solicitud.getMatriculaId().getGrupoCohorteId().getId());
            solicitudResponse.setGrupoId(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getId());
            solicitudResponse.setGrupoNombre(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getNombre());
            solicitudResponse.setGrupoCodigo(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getCodigo());
            solicitudResponse.setMateriaNombre(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getMateriaId().getNombre());
            solicitudResponse.setMateriaCodigo(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getMateriaId().getCodigo());
        }

        solicitudResponse.setEstudianteId(solicitud.getEstudianteId().getId());
        solicitudResponse.setEstudianteNombre(
                solicitud.getEstudianteId().getNombre() + " " + solicitud.getEstudianteId().getNombre2() + " "
                        + solicitud.getEstudianteId().getApellido() + " " + solicitud.getEstudianteId().getApellido2());
        solicitudResponse.setDescripcion(solicitud.getDescripcion());
        solicitudResponse.setTipoSolicitudId(solicitud.getTipoSolicitudId().getId());
        solicitudResponse.setTipoSolicitudNombre(solicitud.getTipoSolicitudId().getNombre());
        solicitudResponse.setSemestre(calcularSemestre(solicitud.getFechaCreacion()));
        if (solicitud.getTipoSolicitudId().getId() == 3) {
            solicitudResponse.setSemestreAplazamiento(
                    calcularSemestre(solicitud.getSolicitudAplazamientoId().getFechaAprobacion()));

            if (solicitud.getFechaAprobacion() != null) {
                solicitudResponse.setSemestre(calcularSemestre(solicitud.getFechaAprobacion()));
            } else {
                solicitudResponse.setSemestre(null);
            }

        }
        solicitudResponse.setEstaAprobado(solicitud.getEstaAprobada());
        solicitudResponse.setEstudianteCodigo(solicitud.getEstudianteId().getCodigo());
        solicitudResponse.setSoporte(solicitud.getSoporteId());

        return solicitudResponse;

    }

    public List<SolicitudResponse> listarSolicitudesPorTipo(Integer tipoSolicitudId) throws SolicitudException {
        List<Solicitud> solicitudes = solicitudRepository.findByTipoSolicitudId_Id(tipoSolicitudId);
        return solicitudes.stream().map(solicitud -> {
            SolicitudResponse solicitudResponse = new SolicitudResponse();
            BeanUtils.copyProperties(solicitud, solicitudResponse);

            if (solicitud.getTipoSolicitudId().getId() == 1) {
                solicitudResponse.setGrupoCohorteId(solicitud.getMatriculaId().getGrupoCohorteId().getId());
                solicitudResponse.setGrupoId(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getId());
                solicitudResponse
                        .setGrupoNombre(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getNombre());
                solicitudResponse
                        .setGrupoCodigo(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getCodigo());
                solicitudResponse.setMateriaNombre(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getMateriaId().getNombre());
                solicitudResponse.setMateriaCodigo(solicitud.getMatriculaId().getGrupoCohorteId().getGrupoId().getMateriaId().getCodigo());
            }

            solicitudResponse.setEstudianteId(solicitud.getEstudianteId().getId());
            solicitudResponse.setEstudianteNombre(solicitud.getEstudianteId().getNombre() + " "
                    + solicitud.getEstudianteId().getNombre2() + " " + solicitud.getEstudianteId().getApellido() + " "
                    + solicitud.getEstudianteId().getApellido2());
            solicitudResponse.setDescripcion(solicitud.getDescripcion());
            solicitudResponse.setTipoSolicitudId(solicitud.getTipoSolicitudId().getId());
            solicitudResponse.setTipoSolicitudNombre(solicitud.getTipoSolicitudId().getNombre());
            solicitudResponse.setSemestre(calcularSemestre(solicitud.getFechaCreacion()));
            if (solicitud.getTipoSolicitudId().getId() == 3) {
                solicitudResponse.setSemestreAplazamiento(
                        calcularSemestre(solicitud.getSolicitudAplazamientoId().getFechaAprobacion()));

                if (solicitud.getFechaAprobacion() != null) {
                    solicitudResponse.setSemestreReintegro(calcularSemestre(solicitud.getFechaAprobacion()));
                } else {
                    solicitudResponse.setSemestreReintegro(null);
                }

            }
            solicitudResponse.setEstaAprobado(solicitud.getEstaAprobada());
            solicitudResponse.setEstudianteCodigo(solicitud.getEstudianteId().getCodigo());
            solicitudResponse.setSoporte(solicitud.getSoporteId());

            return solicitudResponse;
        }).toList();
    }

    public void aprobarSolicitud(Long solicitudId, Integer tiposolicitudId, MultipartFile documento, String usuario)
            throws SolicitudException, IOException {

        // 1. Obtener la solicitud
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new SolicitudException(
                        String.format(IS_NOT_FOUND_F, "Solicitud con ID: " + solicitudId)));

        if (solicitud.getTipoSolicitudId().getId() != tiposolicitudId) {
            throw new SolicitudException("Tipo de solicitud no válido");
        }

        // 2. Verificar que no esté ya aprobada
        if (solicitud.getEstaAprobada()) {
            throw new SolicitudException("La solicitud ya está aprobada");
        }

        // 3. Procesar según el tipo de solicitud
        switch (solicitud.getTipoSolicitudId().getId()) {
            case 1: // Cancelación de materias
                aprobarCancelacionMaterias(solicitud, documento, usuario);
                break;

            case 2: // Aplazamiento de semestre
                aprobarAplazamientoSemestre(solicitud, documento, usuario);
                break;

            case 3: // Reintegro
                aprobarReintegro(solicitud, documento);
                break;

            default:
                throw new SolicitudException("Tipo de solicitud no válido");
        }

        // 4. Actualizar estado de aprobación
        solicitud.setEstaAprobada(true);
        solicitud.setFechaAprobacion(new Date());

        solicitudRepository.save(solicitud);
    }

    // ------------------------------------------------------- MÉTODOS AUXILIARES
    // -------------------------------------------------------------------

    private void aprobarCancelacionMaterias(Solicitud solicitud, MultipartFile documento, String usuario) throws SolicitudException, IOException {
        // 1. Validar que tenga matrícula asociada
        if (solicitud.getMatriculaId() == null) {
            throw new SolicitudException("La solicitud de cancelación no tiene matrícula asociada");
        }
        //2. Subir documento a S3
        Soporte soporte = s3Service.uploadFile(documento, "cancelaciones");
        solicitud.setSoporteId(soporte);
        // 3. Cambiar estado de la matrícula a "Cancelada" (ID 3)
        EstadoMatricula estadoCancelada = estadoMatriculaRepository.findById(3)
                .orElseThrow(() -> new SolicitudException("Estado 'Cancelada' no configurado"));

        solicitud.getMatriculaId().setEstadoMatriculaId(estadoCancelada);
        crearCambioEstadoMatricula(solicitud.getMatriculaId(), estadoCancelada, usuario );
        matriculaRepository.save(solicitud.getMatriculaId());
    }

    private void aprobarAplazamientoSemestre(Solicitud solicitud, MultipartFile documento, String usuario)
            throws SolicitudException, IOException {
        // 1. Validar documento de soporte
        if (documento == null || documento.isEmpty()) {
            throw new SolicitudException("Se requiere documento de soporte para aprobar aplazamiento");
        }

        // 2. Subir documento a S3
        Soporte soporte = s3Service.uploadFile(documento, "aplazamientos");
        solicitud.setSoporteId(soporte);

        // 3. Calcular el semestre actual
        String semestreActual = calcularSemestre(new Date());

        // 4. Buscar todas las matrículas en curso del estudiante para el semestre
        // actual
        List<Matricula> matriculasEnCurso = matriculaRepository.findByEstudianteIdAndEstadoMatriculaId_IdAndSemestre(
                solicitud.getEstudianteId(), 2, semestreActual); // 2 = En curso

        // 5. Cambiar estado de las matrículas a "Cancelada" (ID 3)
        if (!matriculasEnCurso.isEmpty()) {
            EstadoMatricula estadoCancelada = estadoMatriculaRepository.findById(3)
                    .orElseThrow(() -> new SolicitudException("Estado 'Cancelada' no configurado"));

            for (Matricula matricula : matriculasEnCurso) {
                matricula.setEstadoMatriculaId(estadoCancelada);
                matriculaRepository.save(matricula);
                crearCambioEstadoMatricula(matricula, estadoCancelada, usuario );
            }
        }

        // 6. Cambiar estado del estudiante a "Inactivo" (ID 2)
        EstadoEstudiante estadoInactivo = estadoEstudianteRepository.findById(2)
                .orElseThrow(() -> new SolicitudException("Estado 'Inactivo' no configurado"));

        solicitud.getEstudianteId().setEstadoEstudianteId(estadoInactivo);
        estudianteRepository.save(solicitud.getEstudianteId());
    }

    private void aprobarReintegro(Solicitud solicitud, MultipartFile documento)
            throws SolicitudException, IOException {
        // 1. Validar documento de soporte
        if (documento == null || documento.isEmpty()) {
            throw new SolicitudException("Se requiere documento de soporte para aprobar reintegro");
        }

        // 2. Subir documento a S3
        Soporte soporte = s3Service.uploadFile(documento, "reintegros");
        solicitud.setSoporteId(soporte);

        // 3. Cambiar estado del estudiante a "En curso" (ID 1)
        EstadoEstudiante estadoEnCurso = estadoEstudianteRepository.findById(1)
                .orElseThrow(() -> new SolicitudException("Estado 'En curso' no configurado"));

        solicitud.getEstudianteId().setEstadoEstudianteId(estadoEnCurso);
        estudianteRepository.save(solicitud.getEstudianteId());
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
            throw new SolicitudException("Solo estudiantes inactivos pueden solicitar reintegro.");
        }

        // Validar que tenga al menos un aplazamiento aprobado
        if (!solicitudRepository.existsAplazamientoAprobadoByEstudiante(estudiante)) {
            throw new SolicitudException("El estudiante no tiene un aplazamiento aprobado previo.");
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

    private void crearCambioEstadoMatricula(Matricula matricula, EstadoMatricula estadoMatricula, String usuario) {
               CambioEstadoMatricula cambioEstado = new CambioEstadoMatricula();
                cambioEstado.setMatriculaId(matricula);
                cambioEstado.setEstadoMatriculaId(estadoMatricula);
                cambioEstado.setFechaCambioEstado(new Date());
                cambioEstado.setUsuarioCambioEstado(usuario);
                cambioEstado.setSemestre(calcularSemestre(new Date()));

                cambioEstadoMatriculaRepository.save(cambioEstado);

        }
}

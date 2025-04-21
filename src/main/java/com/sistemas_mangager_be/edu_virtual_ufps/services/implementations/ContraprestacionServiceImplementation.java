package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Contraprestacion;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Soporte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.TipoContraprestacion;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ContraprestacionException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ContraprestacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.TipoContraprestacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IContraprestacionService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ContraprestacionDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.ContraprestacionResponse;

@Service
public class ContraprestacionServiceImplementation implements IContraprestacionService {

    public static final String IS_ALREADY_USE = "%s ya esta en uso";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valido";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es correcta";
    private static final String CONTRAPRESTACION_EXISTENTE = "El estudiante ya tiene una contraprestación activa para el semestre %s";

    @Autowired
    private S3Service s3Service;

    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private ContraprestacionRepository contraprestacionRepository;

    @Autowired
    private TipoContraprestacionRepository tipoContraprestacionRepository;

    public void crearContraprestacion(ContraprestacionDTO contraprestacionDTO)
            throws EstudianteNotFoundException, ContraprestacionException {

        Estudiante estudiante = estudianteRepository.findById(contraprestacionDTO.getEstudianteId())
                .orElseThrow(() -> new EstudianteNotFoundException(
                        String.format(IS_NOT_FOUND, "Estudiante con ID: " + contraprestacionDTO.getEstudianteId())));

        TipoContraprestacion tipoContraprestacion = tipoContraprestacionRepository
                .findById(contraprestacionDTO.getTipoContraprestacionId())
                .orElseThrow(() -> new ContraprestacionException(
                        String.format(IS_NOT_FOUND_F,
                                "Tipo de contraprestacion con ID: " + contraprestacionDTO.getTipoContraprestacionId())
                                .toLowerCase()));

        // Validar si ya existe una contraprestación para este estudiante en el semestre
        String semestre = calcularSemestre(new Date());
        if (contraprestacionRepository.existsByEstudianteIdAndSemestre(estudiante, semestre)) {
            throw new ContraprestacionException(
                    String.format(CONTRAPRESTACION_EXISTENTE, semestre));
        }

        Contraprestacion contraprestacion = Contraprestacion.builder()
                .activa(true)
                .actividades(contraprestacionDTO.getActividades())
                .fechaCreacion(new Date())
                .semestre(semestre)
                .fechaFin(contraprestacionDTO.getFechaFin())
                .fechaInicio(contraprestacionDTO.getFechaInicio())
                .estudianteId(estudiante)
                .tipoContraprestacionId(tipoContraprestacion)
                .build();

        contraprestacionRepository.save(contraprestacion);
    }

    public void actualizarContraprestacion(Integer id, ContraprestacionDTO contraprestacionDTO)
            throws EstudianteNotFoundException, ContraprestacionException {

        Contraprestacion contraprestacion = contraprestacionRepository.findById(id)
                .orElseThrow(() -> new ContraprestacionException(
                        String.format(IS_NOT_FOUND_F, "Contraprestación con ID: " + id).toLowerCase()));

        // Validar si se está cambiando el estudiante o el semestre
        boolean estudianteCambiado = !contraprestacion.getEstudianteId().getId().equals(contraprestacionDTO.getEstudianteId());
        boolean semestreCambiado = !contraprestacion.getSemestre().equals(calcularSemestre(new Date()));

        if (estudianteCambiado || semestreCambiado) {
            Estudiante nuevoEstudiante = estudianteCambiado ? 
                estudianteRepository.findById(contraprestacionDTO.getEstudianteId())
                    .orElseThrow(() -> new EstudianteNotFoundException(
                            String.format(IS_NOT_FOUND,
                                    "Estudiante con ID: " + contraprestacionDTO.getEstudianteId()))) :
                contraprestacion.getEstudianteId();

            String nuevoSemestre = semestreCambiado ? calcularSemestre(new Date()) : contraprestacion.getSemestre();

            // Validar si ya existe una contraprestación para el nuevo estudiante y semestre
            if (contraprestacionRepository.existsByEstudianteIdAndSemestre(nuevoEstudiante, nuevoSemestre)) {
                throw new ContraprestacionException(
                        String.format(CONTRAPRESTACION_EXISTENTE, nuevoSemestre));
            }

            if (estudianteCambiado) {
                contraprestacion.setEstudianteId(nuevoEstudiante);
            }
            if (semestreCambiado) {
                contraprestacion.setSemestre(nuevoSemestre);
            }
        }

        // Actualizar tipo de contraprestación si cambió
        if (!contraprestacion.getTipoContraprestacionId().getId()
                .equals(contraprestacionDTO.getTipoContraprestacionId())) {
            TipoContraprestacion tipoContraprestacion = tipoContraprestacionRepository
                    .findById(contraprestacionDTO.getTipoContraprestacionId())
                    .orElseThrow(() -> new ContraprestacionException(
                            String.format(IS_NOT_FOUND_F,
                                    "Tipo de contraprestación con ID: "
                                            + contraprestacionDTO.getTipoContraprestacionId())
                                    .toLowerCase()));
            contraprestacion.setTipoContraprestacionId(tipoContraprestacion);
        }

        contraprestacion.setActividades(contraprestacionDTO.getActividades());
        contraprestacion.setFechaInicio(contraprestacionDTO.getFechaInicio());
        contraprestacion.setFechaFin(contraprestacionDTO.getFechaFin());

        contraprestacionRepository.save(contraprestacion);
    }

    public ContraprestacionResponse listarContraprestacion(Integer idContraprestacion) throws ContraprestacionException {
        Contraprestacion contraprestacion = contraprestacionRepository.findById(idContraprestacion)
                .orElseThrow(() -> new ContraprestacionException(
                        String.format(IS_NOT_FOUND_F, "Contraprestacion con ID: " + idContraprestacion)
                                .toLowerCase()));
        return ContraprestacionResponse.builder()
                .id(contraprestacion.getId())
                .estudianteId(contraprestacion.getEstudianteId().getId())
                .estudianteNombre(contraprestacion.getEstudianteId().getNombre())
                .actividades(contraprestacion.getActividades())
                .fechaCreacion(contraprestacion.getFechaCreacion())
                .fechaInicio(contraprestacion.getFechaInicio())
                .fechaFin(contraprestacion.getFechaFin())
                .tipoContraprestacionId(contraprestacion.getTipoContraprestacionId().getId())
                .tipoContraprestacionNombre(contraprestacion.getTipoContraprestacionId().getNombre())
                .porcentajeContraprestacion(
                        String.valueOf(contraprestacion.getTipoContraprestacionId().getPorcentaje()))
                .build();
    }

    public List<ContraprestacionResponse> listarContraprestaciones() {
        return contraprestacionRepository.findAll().stream().map(contraprestacion -> ContraprestacionResponse.builder()
                .id(contraprestacion.getId())
                .estudianteId(contraprestacion.getEstudianteId().getId())
                .estudianteNombre(contraprestacion.getEstudianteId().getNombre())
                .actividades(contraprestacion.getActividades())
                .fechaCreacion(contraprestacion.getFechaCreacion())
                .fechaInicio(contraprestacion.getFechaInicio())
                .fechaFin(contraprestacion.getFechaFin())
                .tipoContraprestacionId(contraprestacion.getTipoContraprestacionId().getId())
                .tipoContraprestacionNombre(contraprestacion.getTipoContraprestacionId().getNombre())
                .porcentajeContraprestacion(
                        String.valueOf(contraprestacion.getTipoContraprestacionId().getPorcentaje()))
                .build()).collect(Collectors.toList());
    }

    public List<ContraprestacionResponse> listarContraprestacionesPorEstudiante(Integer estudianteId)
            throws EstudianteNotFoundException {
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new EstudianteNotFoundException(
                        String.format(IS_NOT_FOUND, "Estudiante con ID: " + estudianteId)));

        return contraprestacionRepository.findByEstudianteId(estudiante).stream()
                .map(contraprestacion -> ContraprestacionResponse.builder()
                        .id(contraprestacion.getId())
                        .estudianteId(contraprestacion.getEstudianteId().getId())
                        .estudianteNombre(contraprestacion.getEstudianteId().getNombre())
                        .actividades(contraprestacion.getActividades())
                        .fechaCreacion(contraprestacion.getFechaCreacion())
                        .fechaInicio(contraprestacion.getFechaInicio())
                        .fechaFin(contraprestacion.getFechaFin())
                        .tipoContraprestacionId(contraprestacion.getTipoContraprestacionId().getId())
                        .tipoContraprestacionNombre(contraprestacion.getTipoContraprestacionId().getNombre())
                        .porcentajeContraprestacion(
                                String.valueOf(contraprestacion.getTipoContraprestacionId().getPorcentaje()))
                        .build())
                .collect(Collectors.toList());
    }

    public List<ContraprestacionResponse> listarContraprestacionesPorTipoContraprestacion(
            Integer tipoContraprestacionId) throws ContraprestacionException {
        TipoContraprestacion tipoContraprestacion = tipoContraprestacionRepository.findById(tipoContraprestacionId)
                .orElseThrow(() -> new ContraprestacionException(
                        String.format(IS_NOT_FOUND_F, "Tipo de contraprestacion con ID: " + tipoContraprestacionId)
                                .toLowerCase()));

        return contraprestacionRepository.findByTipoContraprestacionId(tipoContraprestacion).stream()
                .map(contraprestacion -> ContraprestacionResponse.builder()
                        .id(contraprestacion.getId())
                        .estudianteId(contraprestacion.getEstudianteId().getId())
                        .estudianteNombre(contraprestacion.getEstudianteId().getNombre())
                        .actividades(contraprestacion.getActividades())
                        .fechaCreacion(contraprestacion.getFechaCreacion())
                        .fechaInicio(contraprestacion.getFechaInicio())
                        .fechaFin(contraprestacion.getFechaFin())
                        .tipoContraprestacionId(contraprestacion.getTipoContraprestacionId().getId())
                        .tipoContraprestacionNombre(contraprestacion.getTipoContraprestacionId().getNombre())
                        .porcentajeContraprestacion(
                                String.valueOf(contraprestacion.getTipoContraprestacionId().getPorcentaje()))
                        .build())
                .collect(Collectors.toList());
    }


    public void aprobarContraprestacion(Integer id, MultipartFile informeFinal) 
            throws ContraprestacionException, IOException {
        
       
        Contraprestacion contraprestacion = contraprestacionRepository.findById(id)
                .orElseThrow(() -> new ContraprestacionException(
                        String.format(IS_NOT_FOUND_F, "Contraprestación con ID: " + id).toLowerCase()));

        
        if (informeFinal == null || informeFinal.isEmpty()) {
            throw new ContraprestacionException("El informe final es requerido para aprobar la contraprestación");
        }

       
        Soporte soporte = s3Service.uploadFile(informeFinal, "contraprestaciones");

        // 4. Actualizar la contraprestación
        contraprestacion.setFechaFin(new Date());
        contraprestacion.setActiva(false);
        contraprestacion.setSoporteId(soporte); // Asociar el soporte subido

        contraprestacionRepository.save(contraprestacion);
    }


    private String calcularSemestre(Date fechaMatriculacion) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaMatriculacion);

                int mes = cal.get(Calendar.MONTH) + 1; // Enero = 0
                int anio = cal.get(Calendar.YEAR);

                return  anio + "-"+ (mes <= 6 ? "I" : "II") ;
        }
}

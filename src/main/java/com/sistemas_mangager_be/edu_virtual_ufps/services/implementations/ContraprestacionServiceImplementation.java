package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Contraprestacion;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
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

        Contraprestacion contraprestacion = new Contraprestacion().builder()
                .activa(true)
                .actividades(contraprestacionDTO.getActividades())
                .fechaCreacion(new Date())
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

        
        if (!contraprestacion.getEstudianteId().getId().equals(contraprestacionDTO.getEstudianteId())) {
            Estudiante estudiante = estudianteRepository.findById(contraprestacionDTO.getEstudianteId())
                    .orElseThrow(() -> new EstudianteNotFoundException(
                            String.format(IS_NOT_FOUND,
                                    "Estudiante con ID: " + contraprestacionDTO.getEstudianteId())));
            contraprestacion.setEstudianteId(estudiante);
        }

       
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
}

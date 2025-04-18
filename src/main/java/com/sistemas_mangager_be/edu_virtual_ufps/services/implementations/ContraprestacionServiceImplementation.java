package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.Date;

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

    public void crearContraprestacion(ContraprestacionDTO contraprestacionDTO) throws EstudianteNotFoundException, ContraprestacionException {
       

       Estudiante estudiante = estudianteRepository.findById(contraprestacionDTO.getEstudianteId())
                .orElseThrow(() -> new EstudianteNotFoundException(
                                                String.format(IS_NOT_FOUND, "Estudiante con ID: " + contraprestacionDTO.getEstudianteId())));

        TipoContraprestacion tipoContraprestacion = tipoContraprestacionRepository.findById(contraprestacionDTO.getTipoContraprestacionId())
                .orElseThrow(() -> new ContraprestacionException(
                                                String.format(IS_NOT_FOUND_F, "Tipo de contraprestacion con ID: " + contraprestacionDTO.getTipoContraprestacionId())
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
}

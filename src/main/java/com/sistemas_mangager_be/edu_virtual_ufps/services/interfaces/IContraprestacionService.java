package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ContraprestacionException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ContraprestacionDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.ContraprestacionResponse;

import java.util.List;

public interface IContraprestacionService {
    
     void crearContraprestacion(ContraprestacionDTO contraprestacionDTO) throws EstudianteNotFoundException, ContraprestacionException;

     void actualizarContraprestacion(Integer id, ContraprestacionDTO contraprestacionDTO)
             throws EstudianteNotFoundException, ContraprestacionException;

     ContraprestacionResponse listarContraprestacion(Integer idContraprestacion) throws ContraprestacionException;
     List<ContraprestacionResponse> listarContraprestaciones();

     List<ContraprestacionResponse> listarContraprestacionesPorEstudiante(Integer estudianteId)
             throws EstudianteNotFoundException;

     List<ContraprestacionResponse> listarContraprestacionesPorTipoContraprestacion(
             Integer tipoContraprestacionId) throws ContraprestacionException;


}

package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ContraprestacionException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ContraprestacionDTO;

public interface IContraprestacionService {
    
     public void crearContraprestacion(ContraprestacionDTO contraprestacionDTO) throws EstudianteNotFoundException, ContraprestacionException;
}

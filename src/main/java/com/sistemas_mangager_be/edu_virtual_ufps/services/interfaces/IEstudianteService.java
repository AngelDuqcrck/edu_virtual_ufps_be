package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;

import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.EstudianteDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.*;

public interface IEstudianteService {
    

    public EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO) 
        throws PensumNotFoundException, CohorteNotFoundException, EstadoEstudianteNotFoundException, RoleNotFoundException;
}

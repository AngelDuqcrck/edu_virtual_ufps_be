package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstadoMatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MatriculaDTO;

@Service
public class MatriculaServiceImplementation {
    
    @Autowired
    private EstadoMatriculaRepository estadoMatriculaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    public MatriculaDTO crearMatricula(MatriculaDTO matriculaDTO) throws EstudianteNotFoundException, GrupoNotFoundException {
        
        return null;
    }
}

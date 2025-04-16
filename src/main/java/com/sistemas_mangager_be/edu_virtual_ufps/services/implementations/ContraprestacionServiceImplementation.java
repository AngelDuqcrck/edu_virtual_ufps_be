package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ContraprestacionRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ContraprestacionDTO;

@Service
public class ContraprestacionServiceImplementation {
    
    @Autowired
    private ContraprestacionRepository contraprestacionRepository;


    public ContraprestacionDTO crearContraprestacion(ContraprestacionDTO contraprestacionDTO) {
       return null;
    }
}

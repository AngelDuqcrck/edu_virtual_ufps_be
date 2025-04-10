package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations.oracle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistemas_mangager_be.edu_virtual_ufps.entitiesOracle.EstudianteVista;
import com.sistemas_mangager_be.edu_virtual_ufps.repositoriesOracle.EstudianteVistaRepository;



@Service
public class EstudianteVistaService {
    
    @Autowired
    private EstudianteVistaRepository vistaRepository;

    @Transactional(readOnly = true)
     public List<EstudianteVista> listarEstudiantes() {
        return vistaRepository.findAll();
    }
}

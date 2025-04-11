package com.sistemas_mangager_be.edu_virtual_ufps.controllers.oracle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.EstudianteOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.EstudianteOracleRepository;

@RestController
@RequestMapping("/api/oracle/estudiantes")
public class EstudianteOracleController {

    private final EstudianteOracleRepository estudianteOracleRepository;

    public EstudianteOracleController(EstudianteOracleRepository estudianteOracleRepository) {
        this.estudianteOracleRepository = estudianteOracleRepository;
    }

    @GetMapping
    public List<EstudianteOracle> obtenerTodos() {
        return estudianteOracleRepository.findAll();
    }

    @GetMapping("/sistemas")
    public List<EstudianteOracle> obtenerEstudiantesSistemas() {
        return estudianteOracleRepository.findByNOMCARRERA("INGENIERIA DE SISTEMAS");
    }

    @GetMapping("/posgrado")
    public List<EstudianteOracle> obtenerEstudiantesMaestria() {
        return estudianteOracleRepository.findByNOMCARRERA(
                "MAESTRIA EN TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION (TIC) APLICADAS A LA EDUCACION");
    }
}
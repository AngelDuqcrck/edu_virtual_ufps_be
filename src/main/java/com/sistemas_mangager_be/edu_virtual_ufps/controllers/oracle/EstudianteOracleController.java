package com.sistemas_mangager_be.edu_virtual_ufps.controllers.oracle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.EstudianteOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.VistaTestOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.EstudianteOracleRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.VistaTestOracleRepository;

@RestController
@RequestMapping("/api/oracle/estudiantes")
public class EstudianteOracleController {

    private final EstudianteOracleRepository estudianteOracleRepository;

    public EstudianteOracleController(EstudianteOracleRepository estudianteOracleRepository) {
        this.estudianteOracleRepository = estudianteOracleRepository;
    }

    @Autowired
    private VistaTestOracleRepository vistaTestOracleRepository;

    @GetMapping
    public List<EstudianteOracle> obtenerTodos() {
        return estudianteOracleRepository.findAll();
    }

    @GetMapping("/vista")
    public List<VistaTestOracle> obtenerTodosVistas() {
        return vistaTestOracleRepository.findAll();
    }
}
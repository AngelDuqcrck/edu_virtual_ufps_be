package com.sistemas_mangager_be.edu_virtual_ufps.controllers.oracle;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.ProfesorOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.ProfesorOracleRepository;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/oracle/profesores")
public class ProfesorOracleController {

    private final ProfesorOracleRepository profesorOracleRepository;

    public ProfesorOracleController(ProfesorOracleRepository profesorOracleRepository) {
        this.profesorOracleRepository = profesorOracleRepository;
    }

    @GetMapping()
    public List<ProfesorOracle> obtenerTodos() {
        return profesorOracleRepository.findAll();
    }

}

package com.sistemas_mangager_be.edu_virtual_ufps.controllers.oracle;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.MateriaOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.MateriaOracleRepository;

@Profile("oracle")
@RestController
@RequestMapping("/api/oracle/materias")
public class MateriaOracleController {

    private final MateriaOracleRepository materiaOracleRepository;

    public MateriaOracleController(MateriaOracleRepository materiaOracleRepository) {
        this.materiaOracleRepository = materiaOracleRepository;
    }

    @GetMapping
    public List<MateriaOracle> obtenerTodos() {
        return materiaOracleRepository.findAll();
    }

    @GetMapping("/sistemas")
    public List<MateriaOracle> obtenerMateriasSistemas() {
        return materiaOracleRepository.findByCodCarrera("215");
    }
}

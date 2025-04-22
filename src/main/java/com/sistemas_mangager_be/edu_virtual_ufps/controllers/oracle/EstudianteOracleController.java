package com.sistemas_mangager_be.edu_virtual_ufps.controllers.oracle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.EstudianteOracle;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.EstudianteOracleRepository;

import lombok.Data;

@Profile("oracle")
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
        return estudianteOracleRepository.findByNomCarrera("INGENIERIA DE SISTEMAS");
    }

    @GetMapping("/posgrado")
    public List<EstudianteOracle> obtenerEstudiantesMaestria() {
        return estudianteOracleRepository.findByNomCarrera(
                "MAESTRIA EN TECNOLOGIAS DE LA INFORMACION Y LA COMUNICACION (TIC) APLICADAS A LA EDUCACION");

    }

    @GetMapping("/buscar-por-codigo")
    public List<EstudianteOracle> buscarPorCodigoStarting(@RequestParam String codigo) {
        return estudianteOracleRepository.findByCodigoStartingWith(codigo);
    }

}
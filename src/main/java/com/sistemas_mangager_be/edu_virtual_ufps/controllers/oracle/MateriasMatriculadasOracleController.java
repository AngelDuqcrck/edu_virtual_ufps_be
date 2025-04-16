package com.sistemas_mangager_be.edu_virtual_ufps.controllers.oracle;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.MateriasMatriculadasOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.MateriasMatriculadasOracleRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Profile("oracle")
@RestController
@RequestMapping("/api/oracle/materias-matriculadas")
public class MateriasMatriculadasOracleController {

    private final MateriasMatriculadasOracleRepository materiasMatriculadasOracleRepository;

    public MateriasMatriculadasOracleController(
            MateriasMatriculadasOracleRepository materiasMatriculadasOracleRepository) {
        this.materiasMatriculadasOracleRepository = materiasMatriculadasOracleRepository;
    }

    @GetMapping
    public List<MateriasMatriculadasOracle> obtenerTodas() {
        return materiasMatriculadasOracleRepository.findAll();
    }

    @GetMapping("/sistemas")
    public List<MateriasMatriculadasOracle> obtenerPorCodCarMat() {
        return materiasMatriculadasOracleRepository.findByCodCarMat("115");
    }

    @GetMapping("/alumno-sistemas")
    public List<MateriasMatriculadasOracle> obtenerPorAlumno() {
        return materiasMatriculadasOracleRepository.findByCodAlumno("2042");
    }

    @GetMapping("/alumno-materia-sistemas")
    public List<MateriasMatriculadasOracle> obtenerPorAlumnoYMateria(
            @RequestParam String codAlumno,
            @RequestParam String codMateria) {
        return materiasMatriculadasOracleRepository.findByCodAlumnoAndCodMateria(codAlumno, codMateria);
    }
}

package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MatriculaException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IMatriculaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MatriculaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.MatriculaResponse;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {
    
    @Autowired
    private IMatriculaService matriculaService;

    @PostMapping("/crear")
    public ResponseEntity<HttpResponse> crearMatricula(@RequestBody MatriculaDTO matriculaDTO) throws MatriculaException, EstudianteNotFoundException, GrupoNotFoundException {
        matriculaService.crearMatricula(matriculaDTO);
         return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Matricula registrada con exito"),
                                HttpStatus.OK); 
    }
    
    @DeleteMapping("/{idMatricula}")
    public ResponseEntity<HttpResponse> anularMatricula(@PathVariable Long idMatricula) throws MatriculaException {
        matriculaService.anularMatricula(idMatricula);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Matricula anulada con exito"),
                                HttpStatus.OK);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<MatriculaResponse>> listarMatriculasEnCursoPorEstudiante(@PathVariable Integer estudianteId) throws EstudianteNotFoundException {
        List<MatriculaResponse> matriculas = matriculaService.listarMatriculasEnCursoPorEstudiante(estudianteId);
        return new ResponseEntity<>(matriculas, HttpStatus.OK);
    }
}

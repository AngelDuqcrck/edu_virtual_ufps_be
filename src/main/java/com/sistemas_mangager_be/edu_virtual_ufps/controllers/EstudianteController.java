package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstadoEstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IEstudianteService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.EstudianteDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {
    
    @Autowired
    private IEstudianteService estudianteService;

    @PostMapping("/crear")
    public ResponseEntity<HttpResponse> crearEstudiante(@RequestBody EstudianteDTO estudianteDTO)
     throws PensumNotFoundException, CohorteNotFoundException, EstadoEstudianteNotFoundException, RoleNotFoundException{
    
        estudianteService.crearEstudiante(estudianteDTO);
                return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Estudiante registrado con exito"),
                                HttpStatus.OK);
    }
}

package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ContraprestacionException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IContraprestacionService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ContraprestacionDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/contraprestaciones")
public class ContraprestacionController {
    
    @Autowired
    private IContraprestacionService  contraprestacionService;

    @PostMapping("/crear")
    public ResponseEntity<HttpResponse> crearContraprestacion(@RequestBody ContraprestacionDTO contraprestacionDTO) throws ContraprestacionException, EstudianteNotFoundException{
        contraprestacionService.crearContraprestacion(contraprestacionDTO);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Contraprestacion creada con exito"),
                                HttpStatus.OK);
    }
}

package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ContraprestacionException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IContraprestacionService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ContraprestacionDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.ContraprestacionResponse;
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

    @PutMapping("/actualizar/{idContraprestacion}")
    public ResponseEntity<HttpResponse> actualizarContraprestacion(@PathVariable Integer idContraprestacion, @RequestBody ContraprestacionDTO contraprestacionDTO) throws ContraprestacionException, EstudianteNotFoundException{
        contraprestacionService.actualizarContraprestacion(idContraprestacion, contraprestacionDTO);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Contraprestacion actualizada con exito"),
                                HttpStatus.OK);
    }

    @GetMapping("/{idContraprestacion}")
    public ResponseEntity<ContraprestacionResponse> listarContraprestacion(@PathVariable Integer idContraprestacion) throws ContraprestacionException {
        ContraprestacionResponse contraprestacionResponse = contraprestacionService.listarContraprestacion(idContraprestacion);
        return new ResponseEntity<>(contraprestacionResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ContraprestacionResponse>> listarContraprestaciones(){
        List<ContraprestacionResponse> contraprestaciones = contraprestacionService.listarContraprestaciones();
        return new ResponseEntity<>(contraprestaciones, HttpStatus.OK);
    }

    @GetMapping("/tipo/{tipoContraprestacionId}")
    public ResponseEntity<List<ContraprestacionResponse>> listarContraprestacionesPorTipoContraprestacion(@PathVariable Integer tipoContraprestacionId) throws ContraprestacionException {
        List<ContraprestacionResponse> contraprestaciones = contraprestacionService.listarContraprestacionesPorTipoContraprestacion(tipoContraprestacionId);
        return new ResponseEntity<>(contraprestaciones, HttpStatus.OK);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<ContraprestacionResponse>> listarContraprestacionesPorEstudiante(@PathVariable Integer estudianteId) throws EstudianteNotFoundException {
        List<ContraprestacionResponse> contraprestaciones = contraprestacionService.listarContraprestacionesPorEstudiante(estudianteId);
        return new ResponseEntity<>(contraprestaciones, HttpStatus.OK);
    }
}

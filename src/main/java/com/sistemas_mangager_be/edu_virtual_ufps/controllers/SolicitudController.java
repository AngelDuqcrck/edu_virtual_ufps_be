package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SolicitudException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.ISolicitudService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.SolicitudDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping
public class SolicitudController {
    
    @Autowired
    private ISolicitudService solicitudService;

    @PostMapping("/cancelacion/crear")
    public ResponseEntity<HttpResponse> crearSolicitudCancelacion(@RequestBody SolicitudDTO solicitudDTO) throws SolicitudException, EstudianteNotFoundException {
        
        solicitudService.crearSolicitud(solicitudDTO, 1);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Cancelacion creada con exito"),
                                HttpStatus.OK);
    }

    @PostMapping("/aplazamiento/crear")
    public ResponseEntity<HttpResponse> crearSolicitudAplazamiento(@RequestBody SolicitudDTO solicitudDTO) throws SolicitudException, EstudianteNotFoundException {
        
        solicitudService.crearSolicitud(solicitudDTO, 2);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Solicitud de aplazamiento creada con exito"),
                                HttpStatus.OK);
    }

    @PostMapping("/reintegro/crear")
    public ResponseEntity<HttpResponse> crearSolicitudReintegro(@RequestBody SolicitudDTO solicitudDTO) throws SolicitudException, EstudianteNotFoundException {
        
        solicitudService.crearSolicitud(solicitudDTO, 3);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Solicitud de reintegro creada con exito"),
                                HttpStatus.OK);
    }
}

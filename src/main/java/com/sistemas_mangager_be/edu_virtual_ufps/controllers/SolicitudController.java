package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SolicitudException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.ISolicitudService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.SolicitudDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.SolicitudResponse;

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

    @PutMapping("cancelacion/actualizar/{id}")
    public ResponseEntity<HttpResponse> actualizarSolicitudCancelacion(@PathVariable Long id, @RequestBody SolicitudDTO solicitudDTO) throws SolicitudException, EstudianteNotFoundException {
        solicitudService.actualizarSolicitud(id, 1, solicitudDTO);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Solicitud de cancelación actualizada con exito"),
                                HttpStatus.OK);
    }

    @PutMapping("aplazamiento/actualizar/{id}")
    public ResponseEntity<HttpResponse> actualizarSolicitudAplazamiento(@PathVariable Long id, @RequestBody SolicitudDTO solicitudDTO) throws SolicitudException, EstudianteNotFoundException {
        solicitudService.actualizarSolicitud(id, 2, solicitudDTO);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Solicitud de aplazamiento actualizada con exito"),
                                HttpStatus.OK);
    }

    @PutMapping("reintegro/actualizar/{id}")
    public ResponseEntity<HttpResponse> actualizarSolicitudReintegro(@PathVariable Long id, @RequestBody SolicitudDTO solicitudDTO) throws SolicitudException, EstudianteNotFoundException {
        solicitudService.actualizarSolicitud(id, 3, solicitudDTO);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Solicitud de reintegro actualizada con exito"),
                                HttpStatus.OK);
    }

    
    @GetMapping("solicitud/{id}")
    public ResponseEntity<SolicitudResponse> listarSolicitud(@PathVariable Long id) throws SolicitudException {
        SolicitudResponse solicitudResponse = solicitudService.listarSolicitudPorId(id);
        return new ResponseEntity<>(
                                solicitudResponse,
                                HttpStatus.OK);
    }

    @GetMapping("/cancelacion")
    public ResponseEntity<List<SolicitudResponse>> listarSolicitudesCancelaciones() throws SolicitudException {
        return new ResponseEntity<>(
                                solicitudService.listarSolicitudesPorTipo(1),
                                HttpStatus.OK);
    }

    @GetMapping("/aplazamiento")
    public ResponseEntity<List<SolicitudResponse>> listarSolicitudesAplazamientos() throws SolicitudException {
        return new ResponseEntity<>(
                                solicitudService.listarSolicitudesPorTipo(2),
                                HttpStatus.OK);
    }

    @GetMapping("/reintegro")
    public ResponseEntity<List<SolicitudResponse>> listarSolicitudesReintegros() throws SolicitudException {
        return new ResponseEntity<>(
                                solicitudService.listarSolicitudesPorTipo(3),
                                HttpStatus.OK);
    }

    @PostMapping("/cancelacion/aprobar/{id}")
    public ResponseEntity<HttpResponse> aprobarCancelacion(@PathVariable Long id) throws SolicitudException, IOException {
        solicitudService.aprobarSolicitud(id, 1, null);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Solicitud de cancelación aprobada con exito"),
                                HttpStatus.OK);
    }

    @PostMapping("/aplazamiento/aprobar/{id}")
    public ResponseEntity<HttpResponse> aprobarAplazamiento(@PathVariable Long id, @RequestParam("informe") MultipartFile file) throws SolicitudException, IOException {
        solicitudService.aprobarSolicitud(id, 2, file);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Solicitud de aplazamiento aprobada con exito"),
                                HttpStatus.OK);
    }

    @PostMapping("/reintegro/aprobar/{id}")
    public ResponseEntity<HttpResponse> aprobarReintegro(@PathVariable Long id, @RequestParam("informe") MultipartFile file) throws SolicitudException, IOException {
        solicitudService.aprobarSolicitud(id, 3, file);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Solicitud de reintegro aprobada con exito"),
                                HttpStatus.OK);
    }
    
    
}

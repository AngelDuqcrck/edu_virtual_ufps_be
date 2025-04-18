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

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.ICohorteService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.CohorteDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/cohortes")
public class CohorteController {
    
    @Autowired
    private ICohorteService cohorteService;

    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping("/crear")
    public ResponseEntity<HttpResponse> crearCohorte(@RequestBody CohorteDTO cohorteDTO) {
        cohorteService.crearCohorte(cohorteDTO);

        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Cohorte creada con exito"),
                                HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CohorteDTO> listarCohorte(@PathVariable Integer id) throws CohorteNotFoundException {
        CohorteDTO cohorte = cohorteService.listarCohorte(id);
        return new ResponseEntity<>(cohorte, HttpStatus.OK);
    }


    @GetMapping("/listar")
    public ResponseEntity<List<CohorteDTO>> listarCohortes() {
        List<CohorteDTO> cohortes = cohorteService.listarCohortes();
        return new ResponseEntity<>(cohortes, HttpStatus.OK);
    }

    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> actualizarCohorte(@PathVariable Integer id, @RequestBody CohorteDTO cohorteDTO) throws CohorteNotFoundException {
        cohorteService.actualizarCohorte(cohorteDTO, id);

        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Cohorte actualizada con exito"),
                                HttpStatus.OK);
    }

    
}

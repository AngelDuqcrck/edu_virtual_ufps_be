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
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IGrupoService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.GrupoDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RequestMapping("/grupos")
@RestController
public class GrupoController {
    
    @Autowired
    private IGrupoService iGrupoService;


    @PostMapping("/crear")
    public ResponseEntity<HttpResponse> crearGrupo(@RequestBody GrupoDTO grupoDTO) throws MateriaNotFoundException, CohorteNotFoundException, UserNotFoundException, RoleNotFoundException {
        iGrupoService.crearGrupo(grupoDTO);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Grupo creado con exito"),
                HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> actualizarGrupo(@RequestBody GrupoDTO grupoDTO, @PathVariable Integer id) throws MateriaNotFoundException, CohorteNotFoundException, UserNotFoundException, RoleNotFoundException, GrupoNotFoundException {
        iGrupoService.actualizarGrupo(grupoDTO, id);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Grupo actualizado con exito"),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponse> listarGrupo(@PathVariable Integer id) throws GrupoNotFoundException {
        GrupoResponse grupoResponse = iGrupoService.listarGrupo(id);
        return new ResponseEntity<>(grupoResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<GrupoResponse>> listarGrupos() {
        List<GrupoResponse> grupoResponse = iGrupoService.listarGrupos();
        return new ResponseEntity<>(grupoResponse, HttpStatus.OK);
    }

    @GetMapping("/docente/{docenteId}")
    public ResponseEntity<List<GrupoResponse>> listarGruposPorDocente(@PathVariable Integer docenteId) throws UserNotFoundException, RoleNotFoundException {
        List<GrupoResponse> grupoResponse = iGrupoService.listarGruposPorDocente(docenteId);
        return new ResponseEntity<>(grupoResponse, HttpStatus.OK);
    }

    @GetMapping("/cohorte/{cohorteId}")
    public ResponseEntity<List<GrupoResponse>> listarGruposPorCohorte(@PathVariable Integer cohorteId) throws CohorteNotFoundException {
        List<GrupoResponse> grupoResponse = iGrupoService.listarGruposPorCohorte(cohorteId);
        return new ResponseEntity<>(grupoResponse, HttpStatus.OK);
    }

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<GrupoResponse>> listarGruposPorMateria(@PathVariable Integer materiaId) throws MateriaNotFoundException {
        List<GrupoResponse> grupoResponse = iGrupoService.listarGruposPorMateria(materiaId);
        return new ResponseEntity<>(grupoResponse, HttpStatus.OK);
    }

    @PostMapping("/{id}/activar")
    public ResponseEntity<HttpResponse> activarGrupo(@PathVariable Integer id) throws GrupoNotFoundException {
        iGrupoService.activarGrupo(id);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Grupo activado con exito"),
                HttpStatus.OK);
    }

    @PostMapping("/{id}/desactivar")
    public ResponseEntity<HttpResponse> desactivarGrupo(@PathVariable Integer id) throws GrupoNotFoundException {
        iGrupoService.desactivarGrupo(id);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Grupo desactivado con exito"),
                HttpStatus.OK);
    }

}

package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MatriculaException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.NotasException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.INotaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.NotasPosgradoRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/notas")
public class NotasController {
    

    @Autowired
    private INotaService notaService;

    @PostMapping("/registrar")
    public ResponseEntity<HttpResponse> registrarNotaPosgrado(@RequestBody NotasPosgradoRequest request) throws NotasException, MatriculaException{
        notaService.guardaroActualizarNotaPosgrado(request);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Nota registrada con exito"),
                HttpStatus.OK);
    }

    @PostMapping("/cerrar/grupo/{idGrupo}")
    public ResponseEntity<HttpResponse> cerrarNotasGrupoPosgrado(@PathVariable Long idGrupo, @RequestHeader(value = "X-Usuario") String usuario) throws GrupoNotFoundException, NotasException{
        notaService.cerrarNotasGrupoPosgrado(idGrupo, usuario);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        "Notas cerradas con exito"),
                HttpStatus.OK);
    }

    @PostMapping("/abrir/grupo/{idGrupo}")
    public ResponseEntity<HttpResponse> abrirNotasGrupoPosgrado(@PathVariable Long idGrupo, @RequestHeader(value = "X-Usuario") String usuario) throws GrupoNotFoundException, NotasException{
        notaService.abrirNotasGrupoPosgrado(idGrupo, usuario);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        "Notas abiertas con exito"),
                HttpStatus.OK);
    }

}

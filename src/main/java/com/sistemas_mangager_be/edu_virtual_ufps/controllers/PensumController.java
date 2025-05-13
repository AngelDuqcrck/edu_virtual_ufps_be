package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;
import java.util.Map;

import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.PensumSemestreResponse;
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

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IPensumService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.PensumDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/pensums")
public class PensumController {

    @Autowired
    private IPensumService pensumService;

    @PostMapping("/crear")
    public ResponseEntity<Map<HttpResponse, PensumDTO>> crearPensum(@RequestBody PensumDTO pensumDTO)
            throws ProgramaNotFoundException {
        PensumDTO pensum = pensumService.crearPensum(pensumDTO);

        return new ResponseEntity<>(
                Map.of(new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Pensum creado con exito"), pensum),
                HttpStatus.OK);
    }

    @GetMapping("/listar")
    public List<PensumSemestreResponse> listarPensums() {
        return pensumService.listarPensums();

    }

    @GetMapping("/{id}")
    public PensumSemestreResponse listarPensum(@PathVariable Integer id) throws PensumNotFoundException {
        return pensumService.listarPensum(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> actualizarPensum(@PathVariable Integer id, @RequestBody PensumDTO pensumDTO)
            throws PensumNotFoundException, ProgramaNotFoundException {
        pensumService.actualizarPensum(pensumDTO, id);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Pensum actualizado con exito"),
                HttpStatus.OK);
    }

    @GetMapping("/programa/{id}")
    public List<PensumSemestreResponse> listarPensumsPorPrograma(@PathVariable Integer id) throws ProgramaNotFoundException {
        return pensumService.listarPensumsPorPrograma(id);
    }

    @PostMapping("/semestre/moodle")
    public ResponseEntity<HttpResponse> vincularSemestreMoodleId(
            @RequestBody MoodleRequest moodleRequest) throws PensumNotFoundException {

        pensumService.vincularSemestreMoodleId(moodleRequest);

        return new ResponseEntity<>(
                new HttpResponse(
                        HttpStatus.OK.value(),
                        HttpStatus.OK,
                        HttpStatus.OK.getReasonPhrase(),
                        "ID de Moodle vinculado al semestre correctamente"),
                HttpStatus.OK);
    }
}

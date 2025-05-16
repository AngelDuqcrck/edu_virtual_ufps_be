package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;
import java.util.Map;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IProgramaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.ProgramaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.SemestreProgramaResponse;

@RestController
@RequestMapping("/programas")
public class ProgramaController {
    
    @Autowired
    private IProgramaService programaService;
    
    /*
     * Crea un nuevo programa
     * 
     * @param programaDTO objeto que contiene los datos del programa
     * 
     * @return la respuesta del programa creado
     */
    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
     @PostMapping("/crear")
    public ResponseEntity<ProgramaDTO> crearPrograma(@RequestBody ProgramaDTO programaDTO) throws ProgramaExistsException {
        ProgramaDTO programa =programaService.crearPrograma(programaDTO);

        return new ResponseEntity<>(programa, HttpStatus.OK);

    }
    @PostMapping("/moodle")
    public ResponseEntity<HttpResponse> vincularMoodle(@RequestBody MoodleRequest moodleRequest) throws ProgramaNotFoundException, ProgramaExistsException {
        programaService.vincularMoodleId(moodleRequest);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                "Vinculacion con moodle realizada con exito"),
                                HttpStatus.OK);
    }
    /*
     * Lista todos los programas
     * 
     * @return la respuesta con la lista de programas
     */
    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/listar")
    public ResponseEntity<List<ProgramaDTO>> listarProgramas() {
        return new ResponseEntity<>(programaService.listarProgramas(), HttpStatus.OK);
    }

    /*
     * Lista un programa
     * 
     * @param id identificador del programa
     * 
     * @return la respuesta con el programa
     */
    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ProgramaDTO> listarPrograma(@PathVariable Integer id) throws ProgramaNotFoundException{
        ProgramaDTO programaDTO = programaService.listarPrograma(id);
        return new ResponseEntity<>(programaDTO, HttpStatus.OK);
    }
    
    @GetMapping("/{id}/semestres")
    public ResponseEntity<SemestreProgramaResponse> listarSemestresPorPrograma(@PathVariable Integer id) throws ProgramaNotFoundException {
        SemestreProgramaResponse semestreProgramaResponse = programaService.listarSemestresPorPrograma(id);
        return new ResponseEntity<>(semestreProgramaResponse, HttpStatus.OK);
    }
    /*
     * Actualiza un programa
     * 
     * @param id identificador del programa
     * @param programaDTO objeto que contiene los datos del programa
     * 
     * @return la respuesta del programa actualizado
     */
    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> actualizarPrograma(@PathVariable Integer id, @RequestBody ProgramaDTO programaDTO) throws ProgramaNotFoundException, ProgramaExistsException{
        programaService.actualizarPrograma(programaDTO, id);
        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Programa actualizado con exito"),
                                HttpStatus.OK);
    }
}

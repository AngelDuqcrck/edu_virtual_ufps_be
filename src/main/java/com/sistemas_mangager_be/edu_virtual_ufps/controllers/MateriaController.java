package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;
import java.util.Map;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaExistsException;
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

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IMateriaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MateriaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MateriaSemestreRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/materias")
public class MateriaController {
    
    @Autowired
    private IMateriaService materiaService;

    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PostMapping("/crear")
    public ResponseEntity<Map<HttpResponse, MateriaDTO>> crearMateria(@RequestBody MateriaDTO materiaDTO) throws PensumNotFoundException, MateriaExistsException {
        MateriaDTO materia = materiaService.crearMateria(materiaDTO);
         return new ResponseEntity<>(
                                Map.of(new HttpResponse(HttpStatus.CREATED.value(), HttpStatus.CREATED, HttpStatus.CREATED.getReasonPhrase(),
                                                " Materia creada con exito"), materia),
                                HttpStatus.CREATED);
    }

    //@PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> actualizarMateria(@PathVariable Integer id, @RequestBody MateriaDTO materiaDTO) throws MateriaExistsException,PensumNotFoundException, MateriaNotFoundException {
        materiaService.actualizarMateria(id, materiaDTO);

        return new ResponseEntity<>(
                                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                                                " Materia actualizada con exito"),
                                HttpStatus.OK);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<MateriaDTO>> listarMaterias() {
        List<MateriaDTO> materias = materiaService.listarMaterias();
        return new ResponseEntity<>(materias, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaDTO> listarMateria(@PathVariable Integer id) throws MateriaNotFoundException {
        MateriaDTO materia = materiaService.listarMateria(id);
        return new ResponseEntity<>(materia, HttpStatus.OK);
    }
    
    @GetMapping("/pensum/{pensumId}")
    public ResponseEntity<List<MateriaDTO>> listarMateriasPorPensum(@PathVariable Integer pensumId) throws PensumNotFoundException {
        List<MateriaDTO> materias = materiaService.listarMateriasPorPensum(pensumId);
        return new ResponseEntity<>(materias, HttpStatus.OK);
    }
    
    @GetMapping("/semestre")
    public ResponseEntity<List<MateriaDTO>> listarMateriasPorPensumPorSemestre(@RequestBody MateriaSemestreRequest materiaSemestreRequest) throws PensumNotFoundException {
        List<MateriaDTO> materias = materiaService.listarMateriasPorPensumPorSemestre(materiaSemestreRequest);
        return new ResponseEntity<>(materias, HttpStatus.OK);
    }
        

}

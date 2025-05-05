package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sistemas_mangager_be.edu_virtual_ufps.services.implementations.MigracionEstudiantesService;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IEstudianteService;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IGrupoService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.EstudianteDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.MoodleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.EstudianteGrupoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.EstudianteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {

    @Autowired
    private IEstudianteService estudianteService;

    @Autowired
    private IGrupoService iGrupoService;

    @Autowired
    private MigracionEstudiantesService migracionEstudiantesService;

    @PostMapping("/crear")
    public ResponseEntity<HttpResponse> crearEstudiante(@RequestBody EstudianteDTO estudianteDTO)
            throws PensumNotFoundException, CohorteNotFoundException, EstadoEstudianteNotFoundException,
            RoleNotFoundException, UserExistException {

        estudianteService.crearEstudiante(estudianteDTO);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Estudiante registrado con exito"),
                HttpStatus.OK);
    }

    @PostMapping("/moodle")
    public ResponseEntity<HttpResponse> vincularEstudianteMoodle(@RequestBody MoodleRequest moodleRequest) throws EstudianteNotFoundException, UserExistException {
        estudianteService.vincularMoodleId(moodleRequest);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Vinculacion con moodle realizada con exito"),
                HttpStatus.OK);
    }


    @PutMapping("{id}")
    public ResponseEntity<HttpResponse> actualizarEstudiante(@RequestBody EstudianteDTO estudianteDTO,
            @PathVariable Integer id)
            throws UserNotFoundException, PensumNotFoundException, CohorteNotFoundException,
            EstadoEstudianteNotFoundException, EstudianteNotFoundException, EmailExistException, UserExistException {
        estudianteService.actualizarEstudiante(id, estudianteDTO);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Estudiante actualizado con exito"),
                HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<EstudianteResponse> listarEstudiante(@PathVariable Integer id)
            throws EstudianteNotFoundException {
        EstudianteResponse estudianteResponse = estudianteService.listarEstudiante(id);
        return new ResponseEntity<>(estudianteResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<EstudianteResponse>> listarEstudiantes() {
        List<EstudianteResponse> estudianteResponse = estudianteService.listarEstudiantes();
        return new ResponseEntity<>(estudianteResponse, HttpStatus.OK);
    }

    @GetMapping("listar/pensum/{pensumId}")
    public ResponseEntity<List<EstudianteResponse>> listarEstudiantesPorPensum(@PathVariable Integer pensumId)
            throws PensumNotFoundException {
        List<EstudianteResponse> estudianteResponse = estudianteService.listarEstudiantesPorPensum(pensumId);
        return new ResponseEntity<>(estudianteResponse, HttpStatus.OK);
    }

    @GetMapping("listar/cohorte/{cohorteId}")
    public ResponseEntity<List<EstudianteResponse>> listarEstudiantesPorCohorte(@PathVariable Integer cohorteId)
            throws CohorteNotFoundException {
        List<EstudianteResponse> estudianteResponse = estudianteService.listarEstudiantesPorCohorte(cohorteId);
        return new ResponseEntity<>(estudianteResponse, HttpStatus.OK);
    }

    @GetMapping("listar/programa/{programaId}")
    public ResponseEntity<List<EstudianteResponse>> listarEstudiantesPorPrograma(@PathVariable Integer programaId)
            throws ProgramaNotFoundException {
        List<EstudianteResponse> estudianteResponse = estudianteService.listarEstudiantesPorPrograma(programaId);
        return new ResponseEntity<>(estudianteResponse, HttpStatus.OK);
    }

    @GetMapping("listar/estado/{estadoEstudianteId}")
    public ResponseEntity<List<EstudianteResponse>> listarEstudiantesPorEstado(@PathVariable Integer estadoEstudianteId)
            throws EstadoEstudianteNotFoundException {
        List<EstudianteResponse> estudianteResponse = estudianteService.listarEstudiantesPorEstado(estadoEstudianteId);
        return new ResponseEntity<>(estudianteResponse, HttpStatus.OK);
    }

    @GetMapping("/grupo-cohorte/{grupoCohorteId}")
    public ResponseEntity<EstudianteGrupoResponse> listarEstudiantesPorGrupoCohorte(@PathVariable Long grupoCohorteId) {
        EstudianteGrupoResponse estudianteGrupoResponse = iGrupoService.listarEstudiantesPorGrupoCohorte(grupoCohorteId);
        return new ResponseEntity<>(estudianteGrupoResponse, HttpStatus.OK);
    }

    // @PostMapping("/migrar")
    // public ResponseEntity<HttpResponse> migrarEstudiantesTIC() {
    //     migracionEstudiantesService.migrarEstudiantesTIC();
    //     return new ResponseEntity<>(
    //             new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
    //                     " Estudiantes migrados con exito"),
    //             HttpStatus.OK);
    // }
}

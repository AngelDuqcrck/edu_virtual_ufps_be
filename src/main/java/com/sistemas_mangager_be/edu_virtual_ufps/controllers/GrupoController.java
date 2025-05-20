package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.VinculacionNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IGrupoService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.GrupoDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.GrupoRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.EstudianteGrupoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteDocenteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

@RequestMapping("/grupos")
@RestController
public class GrupoController {

    @Autowired
    private IGrupoService iGrupoService;

    @PostMapping("/crear")
    public ResponseEntity<GrupoDTO> crearGrupo(@RequestBody GrupoDTO grupoDTO)
            throws MateriaNotFoundException, CohorteNotFoundException, UserNotFoundException, RoleNotFoundException {
        // Guarda el grupo y obtén el grupo creado con su ID asignada
        GrupoDTO grupoCreado = iGrupoService.crearGrupo(grupoDTO);

        return new ResponseEntity<>(grupoCreado, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> actualizarGrupo(@RequestBody GrupoDTO grupoDTO, @PathVariable Integer id)
            throws MateriaNotFoundException, CohorteNotFoundException, UserNotFoundException, RoleNotFoundException,
            GrupoNotFoundException {
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

    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<GrupoCohorteDocenteResponse>> listarGruposPorMateria(@PathVariable Integer materiaId)
            throws MateriaNotFoundException {
        List<GrupoCohorteDocenteResponse> grupoResponse = iGrupoService.listarGruposCohortePorMateria(materiaId);
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

    @PostMapping("/vincular")
    public ResponseEntity<GrupoCohorteDocenteResponse> vincularCohorteDocente(@RequestBody GrupoRequest grupoRequest)
            throws CohorteNotFoundException, GrupoNotFoundException, UserNotFoundException {
        GrupoCohorteDocenteResponse grupoCohorteDocenteResponse =iGrupoService.vincularCohorteDocente(grupoRequest);
        return new ResponseEntity<>(grupoCohorteDocenteResponse, HttpStatus.OK);
    }

    @PostMapping("/moodle/{grupoId}")
    public ResponseEntity<HttpResponse> vincularGrupoMoodle(@PathVariable Long grupoId,
            @RequestParam String moodleId) throws GrupoNotFoundException, GrupoExistException{
        iGrupoService.vincularGrupoMoodle(grupoId, moodleId);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Vinculacion con moodle realizada con exito"),
                HttpStatus.OK);
    }

    @PutMapping("/vincular/{id}")
    public ResponseEntity<HttpResponse> actualizarVinculacionCohorteDocente(@PathVariable Long id,
            @RequestBody GrupoRequest grupoRequest) throws CohorteNotFoundException, GrupoNotFoundException,
            UserNotFoundException, VinculacionNotFoundException {
        iGrupoService.actualizarVinculacionCohorteDocente(id, grupoRequest);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Grupo vinculado con exito"),
                HttpStatus.OK);
    }

    @GetMapping("/vinculado/{id}")
    public ResponseEntity<GrupoCohorteDocenteResponse> listarGrupoCohorteDocente(@PathVariable Long id)
            throws VinculacionNotFoundException {
        GrupoCohorteDocenteResponse grupoCohorteDocenteResponse = iGrupoService.listarGrupoCohorteDocente(id);
        return new ResponseEntity<>(grupoCohorteDocenteResponse, HttpStatus.OK);
    }

    @GetMapping("/vinculados")
    public ResponseEntity<List<GrupoCohorteDocenteResponse>> listarGrupoCohorteDocentes() {
        List<GrupoCohorteDocenteResponse> grupoCohorteDocenteResponse = iGrupoService.listarGrupoCohorteDocentes();
        return new ResponseEntity<>(grupoCohorteDocenteResponse, HttpStatus.OK);
    }

    @GetMapping("/cohorte/{cohorteId}")
    public ResponseEntity<List<GrupoCohorteDocenteResponse>> listarGruposPorCohorte(@PathVariable Integer cohorteId)
            throws CohorteNotFoundException {
        List<GrupoCohorteDocenteResponse> grupoCohorteDocenteResponse = iGrupoService.listarGruposPorCohorte(cohorteId);
        return new ResponseEntity<>(grupoCohorteDocenteResponse, HttpStatus.OK);
    }

    @GetMapping("/programa/{programaId}")
    public ResponseEntity<List<GrupoCohorteDocenteResponse>> listarGruposPorPrograma(@PathVariable Integer programaId)
            throws ProgramaNotFoundException {
        List<GrupoCohorteDocenteResponse> grupoCohorteDocenteResponse = iGrupoService.listarGruposPorPrograma(programaId);
        return new ResponseEntity<>(grupoCohorteDocenteResponse, HttpStatus.OK);
    }
    
    @GetMapping("/grupo/{grupoId}")
    public ResponseEntity<List<GrupoCohorteDocenteResponse>> listarGruposPorGrupo(@PathVariable Integer grupoId)
            throws GrupoNotFoundException {
        List<GrupoCohorteDocenteResponse> grupoCohorteDocenteResponse = iGrupoService.listarGruposPorGrupo(grupoId);
        return new ResponseEntity<>(grupoCohorteDocenteResponse, HttpStatus.OK);
    }

    @GetMapping("/docente/{docenteId}")
    public ResponseEntity<List<GrupoCohorteDocenteResponse>> listarGruposPorDocente(@PathVariable Integer docenteId)
            throws UserNotFoundException {
        List<GrupoCohorteDocenteResponse> grupoCohorteDocenteResponse = iGrupoService.listarGruposPorDocente(docenteId);
        return new ResponseEntity<>(grupoCohorteDocenteResponse, HttpStatus.OK);
    }

    @GetMapping("/cohortegrupo/{cohorteGrupoId}")
    public ResponseEntity<List<GrupoCohorteResponse>> listarGruposPorCohorteGrupo(@PathVariable Integer cohorteGrupoId)
            throws CohorteNotFoundException {
        List<GrupoCohorteResponse> grupoCohorteResponse = iGrupoService.listarGruposPorCohorteGrupo(cohorteGrupoId);
        return new ResponseEntity<>(grupoCohorteResponse, HttpStatus.OK);
    }

    @GetMapping("/estudiantes/grupocohorte/{grupoCohorteId}")
    public ResponseEntity<EstudianteGrupoResponse> listarEstudiantesPorGrupoCohorte(@PathVariable Long grupoCohorteId) {
        EstudianteGrupoResponse estudianteGrupoResponse = iGrupoService
                .listarEstudiantesPorGrupoCohorte(grupoCohorteId);
        return new ResponseEntity<>(estudianteGrupoResponse, HttpStatus.OK);
    }
}

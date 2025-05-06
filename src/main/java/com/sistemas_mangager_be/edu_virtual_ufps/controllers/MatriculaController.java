package com.sistemas_mangager_be.edu_virtual_ufps.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MatriculaException;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IMatriculaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MateriaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MatriculaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.CambioEstadoMatriculaResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteDocenteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.MatriculaResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.PensumResponse;

@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

    @Autowired
    private IMatriculaService matriculaService;

    @PostMapping("/crear")
    public ResponseEntity<HttpResponse> crearMatricula(@RequestBody MatriculaDTO matriculaDTO, @RequestHeader(value = "X-Usuario") String usuario)
            throws MatriculaException, EstudianteNotFoundException, GrupoNotFoundException {
        matriculaService.crearMatricula(matriculaDTO, usuario);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Matricula registrada con exito"),
                HttpStatus.OK);
    }

    @DeleteMapping("/{idMatricula}")
    public ResponseEntity<HttpResponse> anularMatricula(@PathVariable Long idMatricula, @RequestHeader(value = "X-Usuario") String usuario) throws MatriculaException {
        matriculaService.anularMatricula(idMatricula, usuario);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Matricula anulada con exito"),
                HttpStatus.OK);
    }

    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<List<MatriculaResponse>> listarMatriculasEnCursoPorEstudiante(
            @PathVariable Integer estudianteId) throws EstudianteNotFoundException {
        List<MatriculaResponse> matriculas = matriculaService.listarMatriculasEnCursoPorEstudiante(estudianteId);
        return new ResponseEntity<>(matriculas, HttpStatus.OK);
    }

    @GetMapping("/materias/nomatriculadas/{estudianteId}")
    public ResponseEntity<List<MateriaDTO>> listarMateriasNoMatriculadasPorEstudiante(
            @PathVariable Integer estudianteId) throws EstudianteNotFoundException {
        List<MateriaDTO> materias = matriculaService.listarMateriasNoMatriculadasPorEstudiante(estudianteId);
        return new ResponseEntity<>(materias, HttpStatus.OK);
    }

    @GetMapping("/pensum/estudiante/{estudianteId}")
    public ResponseEntity<List<PensumResponse>> listarPensumPorEstudiante(@PathVariable Integer estudianteId)
            throws EstudianteNotFoundException {
        List<PensumResponse> pensums = matriculaService.listarPensumPorEstudiante(estudianteId);
        return new ResponseEntity<>(pensums, HttpStatus.OK);
    }

    @PostMapping("/correo/estudiante/{estudianteId}")
    public ResponseEntity<HttpResponse> enviarCorreo(@PathVariable Integer estudianteId, @RequestHeader(value = "X-Usuario", defaultValue = "sistema") String usuario)
            throws EstudianteNotFoundException, MatriculaException {
        matriculaService.enviarCorreo(estudianteId, usuario);
        return new ResponseEntity<>(
                new HttpResponse(HttpStatus.OK.value(), HttpStatus.OK, HttpStatus.OK.getReasonPhrase(),
                        " Correo enviado con exito"),
                HttpStatus.OK);
    }

    @GetMapping("/correo-enviado/{estudianteId}")
    public ResponseEntity<Boolean> verificarCorreoEnviado(@PathVariable Integer estudianteId)
            throws EstudianteNotFoundException {
        boolean correoEnviado = matriculaService.verificarCorreoEnviado(estudianteId);
        return new ResponseEntity<>(correoEnviado, HttpStatus.OK);
    }

    @GetMapping("/grupos/materia/{materiaId}")
    public ResponseEntity<List<GrupoCohorteDocenteResponse>> listarGruposPorMateria(@PathVariable String materiaId)
            throws MateriaNotFoundException {
        List<GrupoCohorteDocenteResponse> grupoCohorteDocenteResponses = matriculaService
                .listarGrupoCohorteDocentePorMateria(materiaId);
        return new ResponseEntity<>(grupoCohorteDocenteResponses, HttpStatus.OK);
    }

    @GetMapping("/cambio/estudiante/{estudianteId}")
    public ResponseEntity<List<CambioEstadoMatriculaResponse>> listarCambioEstadoMatriculaPorEstudiante(@PathVariable Integer estudianteId) throws EstudianteNotFoundException, MatriculaException {
        List<CambioEstadoMatriculaResponse> cambios = matriculaService.listarCambiosdeEstadoMatriculaPorEstudiante(estudianteId);
        return new ResponseEntity<>(cambios, HttpStatus.OK);
    }
}

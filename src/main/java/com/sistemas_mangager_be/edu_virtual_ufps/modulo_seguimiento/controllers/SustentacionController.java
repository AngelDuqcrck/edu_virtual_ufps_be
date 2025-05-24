package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.controllers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.CriterioEvaluacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.TipoSustentacion;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services.SustentacionService;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.SustentacionDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.SustentacionEvaluadorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sustentaciones")
public class SustentacionController {

    private final SustentacionService sustentacionService;

    public SustentacionController(SustentacionService sustentacionService) {
        this.sustentacionService = sustentacionService;
    }

    @PostMapping
    public ResponseEntity<SustentacionDto> crearSustentacion(@RequestBody SustentacionDto dto) {
        return ResponseEntity.ok(sustentacionService.crearSustentacion(dto));
    }

    @GetMapping("/seleccionar")
    public ResponseEntity<SustentacionDto> obtenerSustentacion(@RequestParam Integer idProyecto,
                                                               @RequestParam TipoSustentacion tipoSustentacion) {
        return ResponseEntity.ok(sustentacionService.obtenerSustentacion(idProyecto, tipoSustentacion));
    }

    @GetMapping
    public ResponseEntity<List<SustentacionDto>> listarSustentaciones(@RequestParam(required = false) Integer idProyecto) {
        return ResponseEntity.ok(sustentacionService.listarSustentaciones(idProyecto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SustentacionDto> actualizarSustentacion(@PathVariable Integer id, @RequestBody SustentacionDto dto) {
        return ResponseEntity.ok(sustentacionService.actualizarSustentacion(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSustentacion(@PathVariable Integer id) {
        sustentacionService.eliminarSustentacion(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/evaluador")
    public ResponseEntity<Void> asignarEvaluadorASustentacion(@RequestBody SustentacionEvaluadorDto dto) {
        sustentacionService.asignarEvaluadorASustentacion(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idSustentacion}/evaluador/{idEvaluador}")
    public ResponseEntity<Void> eliminarEvaluadorDeSustentacion(@PathVariable Integer idSustentacion, @PathVariable Integer idEvaluador) {
        sustentacionService.eliminarEvaluadorDeSustentacion(idSustentacion, idEvaluador);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/evaluar-sustentacion")
    public ResponseEntity<Void> evaluarSustentacion(@RequestBody SustentacionEvaluadorDto dto) {
        sustentacionService.evaluarSustentacion(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/agregar-criterio")
    public ResponseEntity<Void> agregarCriterio(@RequestBody CriterioEvaluacionDto criterioEvaluacionDto) {
        sustentacionService.agregarCriterioEvaluacion(criterioEvaluacionDto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/actualizar-criterio/{idCriterio}")
    public ResponseEntity<Void> actualizarCriterio(@PathVariable Integer idCriterio, @RequestBody CriterioEvaluacionDto criterioEvaluacionDto) {
        sustentacionService.actualizarCriterioEvaluacion(idCriterio, criterioEvaluacionDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/eliminar-criterio/{idCriterio}")
    public ResponseEntity<Void> eliminarCriterio(@PathVariable Integer idCriterio) {
        sustentacionService.eliminarCriterioEvaluacion(idCriterio);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/realizada/{idSustentacion}")
    public ResponseEntity<Void> marcarSustentacionComoRealizada(@PathVariable Integer idSustentacion) {
        sustentacionService.marcarSustentacionRealizada(idSustentacion);
        return ResponseEntity.ok().build();
    }

}

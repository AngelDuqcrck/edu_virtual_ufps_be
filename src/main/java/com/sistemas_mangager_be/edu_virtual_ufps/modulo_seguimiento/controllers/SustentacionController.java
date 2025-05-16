package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.controllers;

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

    @GetMapping("/{id}")
    public ResponseEntity<SustentacionDto> obtenerSustentacion(@PathVariable Integer id) {
        return ResponseEntity.ok(sustentacionService.obtenerSustentacion(id));
    }

    @GetMapping
    public ResponseEntity<List<SustentacionDto>> listarSustentaciones() {
        return ResponseEntity.ok(sustentacionService.listarSustentaciones());
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

}

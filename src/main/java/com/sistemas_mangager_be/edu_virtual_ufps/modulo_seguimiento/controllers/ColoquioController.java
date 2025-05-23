package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.controllers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ColoquioDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services.ColoquioService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteDocenteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coloquios")
public class ColoquioController {

    private final ColoquioService coloquioService;

    public ColoquioController(ColoquioService coloquioService) {
        this.coloquioService = coloquioService;
    }

    @PostMapping
    public ResponseEntity<ColoquioDto> crearColoquio(@RequestBody ColoquioDto coloquioDto) {
        return ResponseEntity.ok(coloquioService.crearColoquio(coloquioDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColoquioDto> obtenerColoquioPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(coloquioService.obtenerColoquioPorId(id));
    }

    @GetMapping("/grupo-cohorte/{grupoCohorteId}")
    public ResponseEntity<List<ColoquioDto>> obtenerColoquiosPorGrupoCohorteId(@PathVariable Long grupoCohorteId) {
        return ResponseEntity.ok(coloquioService.obtenerColoquiosPorGrupoCohorteId(grupoCohorteId));
    }

    @GetMapping("/estudiante")
    public ResponseEntity<List<ColoquioDto>> obtenerColoquiosPorUsuarioId() {
        return ResponseEntity.ok(coloquioService.obtenerColoquiosPorUsuarioId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ColoquioDto> actualizarColoquio(@PathVariable Integer id, @RequestBody ColoquioDto coloquioDto) {
        return ResponseEntity.ok(coloquioService.actualizarColoquio(id, coloquioDto));
    }

    @GetMapping("/docente")
    public ResponseEntity<List<GrupoCohorteDocenteResponse>> listarGruposPorDocente() {
        List<GrupoCohorteDocenteResponse> grupoCohorteDocenteResponse = coloquioService.listarGruposPorDocente();
        return ResponseEntity.ok(grupoCohorteDocenteResponse);
    }

}

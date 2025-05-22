package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.controllers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ColoquioDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services.ColoquioService;
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
    public void crearColoquio(@RequestBody ColoquioDto coloquioDto) {
        coloquioService.crearColoquio(coloquioDto);
    }

    @GetMapping("/{id}")
    public ColoquioDto obtenerColoquioPorId(@PathVariable Integer id) {
        return coloquioService.obtenerColoquioPorId(id);
    }

    @GetMapping("/grupo-cohorte/{grupoCohorteId}")
    public List<ColoquioDto> obtenerColoquiosPorGrupoCohorteId(@PathVariable Long grupoCohorteId) {
        return coloquioService.obtenerColoquiosPorGrupoCohorteId(grupoCohorteId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ColoquioDto> obtenerColoquiosPorUsuarioId(@PathVariable Integer usuarioId) {
        return coloquioService.obtenerColoquiosPorUsuarioId(usuarioId);
    }

    @PutMapping("/{id}")
    public ColoquioDto actualizarColoquio(@PathVariable Integer id, @RequestBody ColoquioDto coloquioDto) {
        return coloquioService.actualizarColoquio(id, coloquioDto);
    }

}

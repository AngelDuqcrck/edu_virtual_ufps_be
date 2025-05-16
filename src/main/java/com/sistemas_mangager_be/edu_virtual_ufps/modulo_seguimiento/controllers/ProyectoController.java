package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.controllers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.ProyectoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.UsuarioProyectoDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services.ProyectoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    @PostMapping
    public ResponseEntity<ProyectoDto> crearProyecto(@RequestBody ProyectoDto dto) {
        return ResponseEntity.ok(proyectoService.crearProyecto(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoDto> obtenerProyecto(@PathVariable Integer id) {
        return ResponseEntity.ok(proyectoService.obtenerProyecto(id));
    }

    @GetMapping
    public ResponseEntity<List<ProyectoDto>> listarProyectos() {
        return ResponseEntity.ok(proyectoService.listarProyectos());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoDto> actualizarProyecto(@PathVariable Integer id, @RequestBody ProyectoDto dto) {
        return ResponseEntity.ok(proyectoService.actualizarProyecto(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProyecto(@PathVariable Integer id) {
        proyectoService.eliminarProyecto(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/asignar-usuario")
    public ResponseEntity<Void> asignarUsuario(@RequestBody UsuarioProyectoDto dto) {
        proyectoService.asignarUsuarioAProyecto(dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{idProyecto}/usuario/{idUsuario}")
    public ResponseEntity<Void> desasignarUsuario(@PathVariable Integer idProyecto, @PathVariable Integer idUsuario) {
        proyectoService.desasignarUsuarioDeProyecto(idUsuario, idProyecto);
        return ResponseEntity.noContent().build();
    }
}


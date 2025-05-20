package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.controllers;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos.DashboardDto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{idProyecto}/{idUsuario}")
    public ResponseEntity<DashboardDto> getDashboard(@PathVariable Integer idProyecto, @PathVariable Long idUsuario) {
        return ResponseEntity.ok(dashboardService.obtenerDashboard(idProyecto, idUsuario));
    }
}

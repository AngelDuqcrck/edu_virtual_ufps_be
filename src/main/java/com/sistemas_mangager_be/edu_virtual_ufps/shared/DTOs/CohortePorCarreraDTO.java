package com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Cohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteGrupoRepository;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CohortePorCarreraDTO {
    private Integer programaId;
    private String programaNombre;
    private List<CohorteConGruposDTO> cohortes;

    @Data
    public static class CohorteConGruposDTO {
        private Integer id;
        private String nombre;
        private Date fechaCreacion;
        private List<CohorteGrupoDTO> grupos;

        public CohorteConGruposDTO(Cohorte cohorte, List<CohorteGrupo> grupos) {
            this.id = cohorte.getId();
            this.nombre = cohorte.getNombre();
            this.fechaCreacion = cohorte.getFechaCreacion();
            this.grupos = grupos.stream()
                    .map(CohorteGrupoDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class CohorteGrupoDTO {
        private Integer id;
        private String nombre;

        public CohorteGrupoDTO(CohorteGrupo grupo) {
            this.id = grupo.getId();
            this.nombre = grupo.getNombre();
        }
    }

    public CohortePorCarreraDTO(Programa programa, List<Cohorte> cohortes, CohorteGrupoRepository cohorteGrupoRepository) {
        this.programaId = programa.getId();
        this.programaNombre = programa.getNombre();
        this.cohortes = cohortes.stream()
                .map(cohorte -> {
                    List<CohorteGrupo> grupos = cohorteGrupoRepository.findByCohorteId(cohorte);
                    return new CohorteConGruposDTO(cohorte, grupos);
                })
                .collect(Collectors.toList());
    }
}
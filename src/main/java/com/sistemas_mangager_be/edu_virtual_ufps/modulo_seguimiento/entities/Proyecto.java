package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.enums.EstadoProyecto;
import com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities.intermedias.UsuarioProyecto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "proyecto")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;
    private String pregunta;
    private String problema;
    private String objetivoGeneral;

    @Enumerated(EnumType.STRING)
    private EstadoProyecto estadoActual;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @ManyToOne()
    @JoinColumn(name = "id_linea_investigacion", referencedColumnName = "id")
    private LineaInvestigacion lineaInvestigacion;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ObjetivoEspecifico> objetivosEspecificos;

    @OneToOne(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private Definitiva definitiva;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioProyecto> usuariosAsignados;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Documento> documentos;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sustentacion> sustentaciones;

    private String recomendacionDirectores;
}

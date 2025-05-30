package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.entities;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "retroalimentacion")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Retroalimentacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    String descripcion;

    @ManyToOne
    @JoinColumn(name = "idDocumento")
    private Documento documento;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private Usuario usuario;
}

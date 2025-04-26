package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;

import java.util.Date;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "estudiantes")
public class Estudiante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable =  true)
    private String nombre2;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = true)
    private String apellido2;

    @Column(unique = true)
    private String moodleId;

    @NotEmpty(message = "El correo no puede estar vacío")
    @Email(message = "Debe proporcionar un correo electrónico válido")
    @Column(nullable = false, unique = true)
    private String email;

    @Size(min = 10, message = "El número de teléfono debe tener 10 digitos")
    @Pattern(regexp = "^[3-6]\\d{9}$", message = "El número de teléfono debe tener 10 digitos y comenzar con 3 o 6")
    private String telefono;

    private String cedula;

    private Date fechaNacimiento;

    private Date fechaIngreso;

    private Boolean esPosgrado;

    @ManyToOne
    @JoinColumn(name = "pensum_id")
    private Pensum pensumId;

    @ManyToOne
    @JoinColumn(name = "programa_id")
    private Programa programaId;

    @ManyToOne
    @JoinColumn(name = "cohorte_id")
    private CohorteGrupo cohorteId;

    @ManyToOne
    @JoinColumn(name = "estado_estudiante_id")
    private EstadoEstudiante estadoEstudianteId;

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuarioId;

    private Boolean migrado;
}

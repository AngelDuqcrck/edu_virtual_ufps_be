package com.sistemas_mangager_be.edu_virtual_ufps.entities;

import lombok.*;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "soportes")
public class Soporte {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String url_s3;          // URL completa del objeto en S3
    private Date fecha_subida;      // Fecha de subida
    private String ruta;            // Ruta completa en S3 (incluyendo carpeta)
    private String nombre_archivo;  // Nombre original del archivo
    private String peso;            // Tamaño formateado (ej. "2.5 MB")
    private Long tamano_bytes;      // Tamaño en bytes para ordenamiento
    private String tipo;            // Tipo de documento (aplazamiento, cancelacion, etc.)
    private String mime_type;       // Tipo MIME del archivo
    private String extension;       // Extensión del archivo
}

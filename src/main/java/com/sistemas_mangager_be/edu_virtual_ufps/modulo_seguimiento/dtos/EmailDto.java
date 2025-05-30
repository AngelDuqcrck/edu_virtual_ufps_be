package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class EmailDto {
    private String destinatario;
    private String asunto;
    private String cuerpo;
    private List<MultipartFile> adjuntos;
}

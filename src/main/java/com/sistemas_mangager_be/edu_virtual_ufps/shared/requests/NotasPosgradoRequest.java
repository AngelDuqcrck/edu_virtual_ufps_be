package com.sistemas_mangager_be.edu_virtual_ufps.shared.requests;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotasPosgradoRequest {
    
    private Long matriculaId;
    private Double nota;
    private String realizadoPor;

}

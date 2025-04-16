package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PensumResponse {
    
    public String pensumNombre;
    public String semestrePensum;

    public List<MateriaPensumResponse> materias;
}

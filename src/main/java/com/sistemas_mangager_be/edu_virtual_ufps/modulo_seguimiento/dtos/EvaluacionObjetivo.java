package com.sistemas_mangager_be.edu_virtual_ufps.modulo_seguimiento.dtos;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvaluacionObjetivo implements Serializable {
    private boolean director;
    private boolean codirector;
}

package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.Id;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MateriaOracleId implements Serializable {

    private String codDpto;
    private String codCarrera;
    private String codMateria;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof MateriaOracleId))
            return false;
        MateriaOracleId that = (MateriaOracleId) o;
        return Objects.equals(codDpto, that.codDpto) &&
                Objects.equals(codCarrera, that.codCarrera) &&
                Objects.equals(codMateria, that.codMateria);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codDpto, codCarrera, codMateria);
    }
}

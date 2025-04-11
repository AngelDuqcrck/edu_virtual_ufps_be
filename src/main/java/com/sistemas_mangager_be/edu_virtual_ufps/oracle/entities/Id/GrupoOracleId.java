package com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.Id;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoOracleId implements Serializable {

    private String codCarrera;
    private String codMateria;
    private String grupo;
    private String ciclo;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof GrupoOracleId))
            return false;
        GrupoOracleId that = (GrupoOracleId) o;
        return Objects.equals(codCarrera, that.codCarrera) &&
                Objects.equals(codMateria, that.codMateria) &&
                Objects.equals(grupo, that.grupo) &&
                Objects.equals(ciclo, that.ciclo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codCarrera, codMateria, grupo, ciclo);
    }
}

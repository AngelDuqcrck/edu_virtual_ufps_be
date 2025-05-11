package com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.MateriasMatriculadasOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.Id.MateriasMatriculadasId;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MateriasMatriculadasOracleRepository
                extends JpaRepository<MateriasMatriculadasOracle, MateriasMatriculadasId> {

        // Puedes crear métodos derivados como:
        List<MateriasMatriculadasOracle> findByCodCarMat(String codCarMat);

        List<MateriasMatriculadasOracle> findByCodAlumno(String codAlumno);

        List<MateriasMatriculadasOracle> findByCodAlumnoAndCodMateria(String codAlumno, String codMateria);

        List<MateriasMatriculadasOracle> findByCodCarMatAndCodMatMatAndGrupo(String codCarMat, String codMatMat,
                        String grupo);

        List<MateriasMatriculadasOracle> findByCodMateria(String codMateria);

        List<MateriasMatriculadasOracle> findByCodCarreraAndCodAlumno(String codCarrera, String codAlumno);
}

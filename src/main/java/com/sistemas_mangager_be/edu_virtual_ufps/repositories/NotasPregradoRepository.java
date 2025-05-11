package com.sistemas_mangager_be.edu_virtual_ufps.repositories;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.NotasPregrado;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotasPregradoRepository extends JpaRepository<NotasPregrado, Long> {
    
    // Buscar por grupo-cohorte
    List<NotasPregrado> findByGrupoCohorteId(GrupoCohorte grupoCohorte);
    
    // Buscar nota específica por identificadores de Oracle
    Optional<NotasPregrado> findByOracleCodAlumnoAndOracleCodMateriaAndOracleCodCarreraAndOracleGrupoAndOracleCiclo(
        String codAlumno, String codMateria, String codCarrera, String grupo, String ciclo);
    
    // Buscar notas de un estudiante por su código
    List<NotasPregrado> findByEstudianteCodigo(String estudianteCodigo);
    
    // Buscar por código de estudiante y grupo-cohorte
    Optional<NotasPregrado> findByEstudianteCodigoAndGrupoCohorteId(String estudianteCodigo, GrupoCohorte grupoCohorte);
    
    // Buscar notas no sincronizadas con Moodle
    List<NotasPregrado> findByMoodleSyncStatus(Boolean syncStatus);
    
    // Buscar por IDs de Moodle
    Optional<NotasPregrado> findByMoodleStudentIdAndMoodleCourseId(String studentId, String courseId);
    
    // Consulta personalizada para estadísticas
    @Query("SELECT AVG(n.notaDefinitiva) FROM NotasPregrado n WHERE n.grupoCohorteId = :grupoCohorte")
    Double obtenerPromedioPorGrupo(@Param("grupoCohorte") GrupoCohorte grupoCohorte);
    
    @Query("SELECT COUNT(n) FROM NotasPregrado n WHERE n.grupoCohorteId = :grupoCohorte AND n.notaDefinitiva >= 3.0")
    Long contarAprobadosPorGrupo(@Param("grupoCohorte") GrupoCohorte grupoCohorte);
    
    @Query("SELECT COUNT(n) FROM NotasPregrado n WHERE n.grupoCohorteId = :grupoCohorte AND n.notaDefinitiva < 3.0")
    Long contarReprobadosPorGrupo(@Param("grupoCohorte") GrupoCohorte grupoCohorte);
}
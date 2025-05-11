package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.NotasPregrado;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.NotasPregradoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.MateriasMatriculadasOracleRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.EstudianteOracleRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.MateriasMatriculadasOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.EstudianteOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.NotasPregradoDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.NotasException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class NotasPregradoServiceImplementation {
    
    @Autowired
    private NotasPregradoRepository notasPregradoRepository;
    
    @Autowired
    private GrupoCohorteRepository grupoCohorteRepository;
    
    @Autowired
    private MateriasMatriculadasOracleRepository materiasMatriculadasRepository;
    
    @Autowired
    private EstudianteOracleRepository estudianteOracleRepository;
    
    /**
     * Sincroniza estudiantes matriculados en Oracle con el sistema MySQL
     * y crea registros de notas vacíos para cada estudiante
     * 
     * @param grupoCohorteId ID del grupo en MySQL
     * @param ciclo Ciclo académico (ej. "2023-1")
     * @param usuario Usuario que realiza la operación
     * @return Número de registros creados
     */
    @Transactional
    public int sincronizarEstudiantesMatriculadosOracle(Long grupoCohorteId, String ciclo, String usuario)
            throws GrupoNotFoundException {
        
        // 1. Obtener información del grupo-cohorte
        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(grupoCohorteId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo cohorte no encontrado"));
        
        // 2. Verificar que tenga moodleId configurado
        if (grupoCohorte.getMoodleId() == null || grupoCohorte.getMoodleId().isEmpty()) {
            throw new GrupoNotFoundException("El grupo no tiene ID de Moodle configurado");
        }
        
        // 3. Obtener información de la materia
        String codigoMateria = grupoCohorte.getGrupoId().getMateriaId().getCodigo();
        String codigoGrupo = grupoCohorte.getGrupoId().getCodigo();
        
        // 4. Buscar estudiantes matriculados en Oracle para esta materia
        List<MateriasMatriculadasOracle> matriculas = 
                materiasMatriculadasRepository.findByCodMateria(codigoMateria);
        
        // 5. Filtrar por grupo si es necesario
        List<MateriasMatriculadasOracle> matriculasGrupo = matriculas.stream()
                .filter(m -> m.getGrupo().equals(codigoGrupo))
                .collect(Collectors.toList());
        
        // 6. Crear registros de notas
        int registrosCreados = 0;
        Date ahora = new Date();
        
        for (MateriasMatriculadasOracle matricula : matriculasGrupo) {
            String codAlumno = matricula.getCodAlumno();
            
            // Verificar si ya existe registro para este estudiante en este grupo
            Optional<NotasPregrado> notaExistente = 
                    notasPregradoRepository.findByEstudianteCodigoAndGrupoCohorteId(codAlumno, grupoCohorte);
            
            if (!notaExistente.isPresent()) {
                NotasPregrado nota = new NotasPregrado();
                
                // Datos de Oracle
                nota.setOracleCodAlumno(matricula.getCodAlumno());
                nota.setOracleCodCarrera(matricula.getCodCarrera());
                nota.setOracleCodMateria(matricula.getCodMateria());
                nota.setOracleGrupo(matricula.getGrupo());
                nota.setOracleCiclo(ciclo);
                
                // Datos de MySQL
                nota.setGrupoCohorteId(grupoCohorte);
                nota.setEstudianteCodigo(codAlumno);
                nota.setMoodleCourseId(grupoCohorte.getMoodleId());
                
                // Buscar información adicional del estudiante en Oracle
                Optional<EstudianteOracle> estudianteOracle = 
                        estudianteOracleRepository.findById(codAlumno);
                
                if (estudianteOracle.isPresent()) {
                    // Aquí podrías buscar el estudiante en MySQL por documento para obtener su moodleId
                    // y establecerlo en nota.setMoodleStudentId()
                }
                
                // Metadatos
                nota.setFechaRegistro(ahora);
                nota.setRealizadoPor(usuario);
                nota.setEsModificable(true);
                nota.setMoodleSyncStatus(false);
                
                notasPregradoRepository.save(nota);
                registrosCreados++;
            }
        }
        
        return registrosCreados;
    }
    
    /**
     * Registra o actualiza una nota
     */
    @Transactional
    public NotasPregrado registrarNota(NotasPregradoDTO notaDTO, String usuario) throws NotasException {
        
        // 1. Buscar la nota existente
        NotasPregrado nota = notasPregradoRepository.findById(notaDTO.getId())
                .orElseThrow(() -> new NotasException("Nota no encontrada"));
        
        // 2. Verificar si es modificable
        if (nota.getEsModificable() != null && !nota.getEsModificable()) {
            throw new NotasException("Esta nota ya no es modificable");
        }
        
        // 3. Actualizar calificaciones
        nota.setPrimerPrevio(notaDTO.getPrimerPrevio());
        nota.setSegundoPrevio(notaDTO.getSegundoPrevio());
        nota.setTerceraNota(notaDTO.getTerceraNota());
        nota.setExamenFinal(notaDTO.getExamenFinal());
        nota.setHabilitacion(notaDTO.getHabilitacion());
        nota.setObservaciones(notaDTO.getObservaciones());
        
        // 4. Calcular nota definitiva
        calcularNotaDefinitiva(nota);
        
        // 5. Actualizar metadatos
        nota.setFechaModificacion(new Date());
        nota.setModificadoPor(usuario);
        nota.setMoodleSyncStatus(false); // Marcar para sincronización con Moodle
        
        return notasPregradoRepository.save(nota);
    }
    
    /**
     * Calcula la nota definitiva según los parciales
     */
    private void calcularNotaDefinitiva(NotasPregrado nota) {
        // Verificar que existan todas las notas necesarias
        if (nota.getPrimerPrevio() == null || nota.getSegundoPrevio() == null || 
            nota.getTerceraNota() == null || (nota.getExamenFinal() == null && nota.getHabilitacion() == null)) {
            // No calcular si faltan notas
            return;
        }
        
        double definitiva = 0.0;
        
        // Si hay habilitación, esta reemplaza al examen final
        if (nota.getHabilitacion() != null && nota.getHabilitacion() > 0) {
            definitiva = (nota.getPrimerPrevio() * 0.25) + 
                       (nota.getSegundoPrevio() * 0.25) + 
                       (nota.getTerceraNota() * 0.20) + 
                       (nota.getHabilitacion() * 0.30);
        } else {
            definitiva = (nota.getPrimerPrevio() * 0.25) + 
                       (nota.getSegundoPrevio() * 0.25) + 
                       (nota.getTerceraNota() * 0.20) + 
                       (nota.getExamenFinal() * 0.30);
        }
        
        // Redondear a dos decimales
        definitiva = Math.round(definitiva * 100.0) / 100.0;
        nota.setNotaDefinitiva(definitiva);
    }
    
    /**
     * Sincroniza notas con Moodle
     */
    @Transactional
    public void sincronizarNotasConMoodle() {
        List<NotasPregrado> notasNoSincronizadas = 
                notasPregradoRepository.findByMoodleSyncStatus(false);
        
        for (NotasPregrado nota : notasNoSincronizadas) {
            // Verificar que tenga los IDs de Moodle necesarios
            if (nota.getMoodleCourseId() != null && nota.getMoodleStudentId() != null && 
                nota.getNotaDefinitiva() != null) {
                
                // Aquí iría el código para sincronizar con Moodle
                // Por ejemplo, llamar a un servicio web o actualizar la base de datos de Moodle
                
                // Marcar como sincronizada
                nota.setMoodleSyncStatus(true);
                nota.setMoodleLastSync(new Date());
                notasPregradoRepository.save(nota);
            }
        }
    }
    
    /**
     * Bloquea la edición de notas para un grupo
     */
    @Transactional
    public void cerrarNotasGrupo(Long grupoCohorteId, String usuario) throws GrupoNotFoundException {
        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(grupoCohorteId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo cohorte no encontrado"));
        
        List<NotasPregrado> notasGrupo = notasPregradoRepository.findByGrupoCohorteId(grupoCohorte);
        
        Date ahora = new Date();
        
        for (NotasPregrado nota : notasGrupo) {
            nota.setEsModificable(false);
            nota.setFechaModificacion(ahora);
            nota.setModificadoPor(usuario);
            notasPregradoRepository.save(nota);
        }
    }
    
    /**
     * Obtiene todas las notas de un grupo-cohorte
     */
    public List<NotasPregradoDTO> obtenerNotasPorGrupo(Long grupoCohorteId) throws GrupoNotFoundException {
        GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(grupoCohorteId)
                .orElseThrow(() -> new GrupoNotFoundException("Grupo cohorte no encontrado"));
        
        List<NotasPregrado> notas = notasPregradoRepository.findByGrupoCohorteId(grupoCohorte);
        
        return notas.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    /**
     * Convierte entidad a DTO
     */
    private NotasPregradoDTO convertToDTO(NotasPregrado nota) {
        return NotasPregradoDTO.builder()
                .id(nota.getId())
                .estudianteCodigo(nota.getEstudianteCodigo())
                .primerPrevio(nota.getPrimerPrevio())
                .segundoPrevio(nota.getSegundoPrevio())
                .terceraNota(nota.getTerceraNota())
                .examenFinal(nota.getExamenFinal())
                .habilitacion(nota.getHabilitacion())
                .notaDefinitiva(nota.getNotaDefinitiva())
                .esModificable(nota.getEsModificable())
                .observaciones(nota.getObservaciones())
                .build();
    }
}

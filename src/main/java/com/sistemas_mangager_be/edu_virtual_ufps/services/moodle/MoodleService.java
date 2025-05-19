package com.sistemas_mangager_be.edu_virtual_ufps.services.moodle;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.HistoricoGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.HistoricoSemestre;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Matricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Semestre;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.SemestrePrograma;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.NotasException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SemestreException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.HistoricoGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.HistoricoSemestreRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ProgramaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.implementations.NotasServiceImplementation;
import com.sistemas_mangager_be.edu_virtual_ufps.services.moodle.MoodleApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoodleService {

    private final MoodleApiClient moodleApiClient;
    private final HistoricoSemestreRepository historicoSemestreRepository;
    private final HistoricoGrupoRepository historicoGrupoRepository;
    private final GrupoCohorteRepository grupoCohorteRepository;
    private final MatriculaRepository matriculaRepository;
    private final ProgramaRepository programaRepository;

    @Autowired
    private NotasServiceImplementation notasServiceImplementation;

    /**
 * Cierra las notas para todos los grupos de un semestre en un programa
 * 
 * @param programa Programa académico
 * @param usuario  Usuario que realiza la acción
 * @throws NotasException Si hay un error crítico al cerrar las notas
 */
@Transactional
public void cerrarNotasPorSemestre(Programa programa, String usuario, String semestre) throws NotasException {
    log.info("Iniciando cierre de notas para el semestre {} del programa {}", 
             semestre, programa.getNombre());
    
    List<GrupoCohorte> grupos = grupoCohorteRepository
            .findByGrupoId_MateriaId_PensumId_ProgramaIdAndSemestre(programa, semestre);
    
    if (grupos.isEmpty()) {
        log.warn("No hay grupos para el semestre {} en el programa {}", 
                 programa.getSemestreActual(), programa.getNombre());
        return;
    }
    
    log.info("Se encontraron {} grupos para cerrar notas", grupos.size());
    
    int gruposExitosos = 0;
    List<String> gruposConError = new ArrayList<>();
    
    for (GrupoCohorte grupo : grupos) {
        try {
            log.info("Procesando cierre de notas para grupo {}", grupo.getId());
            notasServiceImplementation.cerrarNotasGrupoPosgrado(grupo.getId(), usuario);
            gruposExitosos++;
            log.info("Notas cerradas exitosamente para el grupo {}", grupo.getId());
        } catch (Exception e) {
            log.error("Error al cerrar notas del grupo {}: {}", grupo.getId(), e.getMessage(), e);
            gruposConError.add("Grupo " + grupo.getId() + ": " + e.getMessage());
        }
    }
    
    log.info("Cierre de notas completado. Grupos exitosos: {}/{}, Grupos con error: {}",
            gruposExitosos, grupos.size(), gruposConError.size());
    
    if (gruposExitosos == 0 && !grupos.isEmpty()) {
        throw new NotasException("No se pudo cerrar las notas de ningún grupo del programa");
    }
}

    /**
     * Realiza el proceso completo de terminación de semestre para un programa
     * 
     * @param programaId ID del programa académico
     * @param semestre   Semestre actual a terminar (formato "YYYY-I" o "YYYY-II")
     * @param usuario    Usuario que realiza la acción
     * @return Resultado del proceso con estadísticas
     * @throws SemestreException      Si hay errores en el proceso
     * @throws GrupoNotFoundException Si no se encuentra algún grupo
     * @throws NotasException         Si hay error al cerrar las notas
     */
    @Transactional
    public Map<String, Object> terminarSemestre(Integer programaId, String semestre, String usuario)
            throws SemestreException, GrupoNotFoundException, NotasException {

        // 1. Verificar y obtener el programa
        Programa programa = programaRepository.findById(programaId)
                .orElseThrow(() -> new SemestreException("Programa no encontrado con ID: " + programaId));

        String semestrePrograma = programa.getSemestreActual();
        // 2. Verificar si ya existe un histórico para este semestre (para evitar
        // duplicados)
        Optional<HistoricoSemestre> existenteHistorico = historicoSemestreRepository
                .findByProgramaAndSemestre(programa, semestre);

        if (existenteHistorico.isPresent()) {
            // Si ya existe un histórico, podemos continuar pero advertir
            log.warn("Ya existe un registro histórico para el semestre {} en el programa {}. " +
                    "Se utilizará el existente.", semestre, programa.getNombre());
        }

        // Resto del código igual...

        // 7. Cambiar al siguiente semestre (solo si no se había terminado previamente)
        if (!existenteHistorico.isPresent()) {
            String siguienteSemestre = calcularSiguienteSemestre(semestre);
            programa.setSemestreActual(siguienteSemestre);
            programaRepository.save(programa);
            log.info("Semestre actualizado: {} → {}", semestre, siguienteSemestre);
        } else {
            log.info("No se actualizó el semestre del programa ya que ya había sido terminado previamente");
        }

        // Resto del código igual...

        // 3. Cerrar las notas de todos los grupos del semestre
        cerrarNotasPorSemestre(programa, usuario, semestrePrograma);
        log.info("Notas cerradas correctamente para todos los grupos del programa");

        // 4. Crear histórico de semestre en la base de datos
        HistoricoSemestre historicoSemestre = crearHistoricoSemestre(programa, semestre);

        // 5. Crear categoría histórica en Moodle y obtener su ID
        String historicoSemestreMoodleId = crearCategoriaHistoricaSemestre(programa, semestre);
        historicoSemestre.setMoodleCategoriaId(historicoSemestreMoodleId);
        historicoSemestreRepository.save(historicoSemestre);

        // 6. Procesar cada grupo del semestre
        List<GrupoCohorte> grupos = grupoCohorteRepository.findByGrupoId_MateriaId_PensumId_ProgramaIdAndSemestre(
                programa, semestre);

        int totalGrupos = grupos.size();
        int gruposExitosos = 0;
        List<String> errores = new ArrayList<>();

        // Estadísticas para el reporte
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("programaId", programaId);
        resultado.put("nombrePrograma", programa.getNombre());
        resultado.put("semestre", semestre);
        resultado.put("totalGrupos", totalGrupos);

        for (GrupoCohorte grupo : grupos) {
            try {
                procesarGrupoParaHistorico(grupo, programa, historicoSemestre);
                gruposExitosos++;
            } catch (Exception e) {
                log.error("Error al procesar grupo {}: {}", grupo.getId(), e.getMessage(), e);
                errores.add("Error al procesar grupo " + grupo.getId() + ": " + e.getMessage());
            }
        }

        // 7. Cambiar al siguiente semestre
        String siguienteSemestre = calcularSiguienteSemestre(semestre);
        programa.setSemestreActual(siguienteSemestre);
        programaRepository.save(programa);

        log.info("Semestre finalizado: {} → {}", semestre, siguienteSemestre);

        // 8. Completar estadísticas
        resultado.put("gruposExitosos", gruposExitosos);
        resultado.put("errores", errores);
        resultado.put("nuevoSemestre", siguienteSemestre);

        return resultado;
    }

    /**
     * Crea un registro histórico del semestre en la base de datos
     */
    private HistoricoSemestre crearHistoricoSemestre(Programa programa, String semestre) {
        // Verificar si ya existe un histórico para este semestre y programa
        Optional<HistoricoSemestre> existente = historicoSemestreRepository
                .findByProgramaAndSemestre(programa, semestre);

        if (existente.isPresent()) {
            return existente.get();
        }

        String[] partes = semestre.split("-");
        int año = Integer.parseInt(partes[0]);
        String periodo = partes[1];

        Date fechaInicio = "I".equals(periodo) ? crearFecha(2, 1, año) : crearFecha(1, 7, año);
        HistoricoSemestre historicoSemestre = new HistoricoSemestre();
        historicoSemestre.setPrograma(programa);
        historicoSemestre.setSemestre(semestre);
        historicoSemestre.setFechaInicio(fechaInicio);
        historicoSemestre.setFechaFin(new Date());

        return historicoSemestreRepository.save(historicoSemestre);
    }

    /**
     * Crea o recupera una categoría en Moodle para el semestre histórico
     */
    private String crearCategoriaHistoricaSemestre(Programa programa, String semestre) {
        // 1. Verificar que el programa tenga un ID de categoría históricos en Moodle
        if (programa.getHistoricoMoodleId() == null || programa.getHistoricoMoodleId().isEmpty()) {
            throw new RuntimeException("El programa no tiene configurada una categoría de históricos en Moodle");
        }

        // 2. Nombre de la categoría para este semestre
        String nombreCategoria = semestre;

        // 3. Intentar crear la categoría o recuperar existente
        String categoriaSemestreId = moodleApiClient.crearCategoria(
                nombreCategoria,
                programa.getHistoricoMoodleId());

        // 4. Verificar si se pudo crear u obtener
        if (categoriaSemestreId == null) {
            throw new RuntimeException("No se pudo crear o encontrar la categoría para el semestre " +
                    semestre + " bajo la categoría históricos");
        }

        log.info("Categoría para semestre histórico: {} (ID: {})", nombreCategoria, categoriaSemestreId);

        return categoriaSemestreId;
    }

    /**
     * Procesa un grupo para el histórico: duplica en Moodle, crea registro
     * histórico y reinicia para siguiente semestre
     */
    @Transactional
    public void procesarGrupoParaHistorico(GrupoCohorte grupo, Programa programa, HistoricoSemestre historicoSemestre) {
        if (grupo.getMoodleId() == null || grupo.getMoodleId().isEmpty()) {
            log.warn("El grupo {} no tiene ID de Moodle. Omitiendo...", grupo.getId());
            return;
        }

        // 1. Determinar el semestre del grupo en números romanos (I, II, III, etc.)
        String semestreRomano = determinarSemestreRomanoDelGrupo(grupo);

        // 2. Crear o verificar la categoría del semestre (I, II, etc.) bajo la
        // categoría del semestre histórico
        String categoriaSemestreRomanoId = crearCategoriaSemestreRomano(
                historicoSemestre.getMoodleCategoriaId(),
                semestreRomano);

        // 3. Duplicar el curso a la categoría histórica
        String nombreCursoHistorico = grupo.getGrupoId().getMateriaId().getNombre() +
                " - " + grupo.getGrupoId().getNombre() + " (" + historicoSemestre.getSemestre() + ")";

        String cursoDuplicadoId = moodleApiClient.copiarCurso(
                grupo.getMoodleId(),
                categoriaSemestreRomanoId,
                nombreCursoHistorico);

        if (cursoDuplicadoId == null) {
            throw new RuntimeException("No se pudo crear el curso histórico para el grupo " + grupo.getId());
        }

        log.info("Curso duplicado en Moodle con ID: {}", cursoDuplicadoId);

        // 4. Crear registro de grupo histórico
        HistoricoGrupo historicoGrupo = new HistoricoGrupo();
        historicoGrupo.setGrupoCohorte(grupo);
        historicoGrupo.setHistoricoSemestre(historicoSemestre);
        historicoGrupo.setMoodleCursoOriginalId(grupo.getMoodleId());
        historicoGrupo.setMoodleCursoHistoricoId(cursoDuplicadoId);
        historicoGrupo.setFechaCreacion(new Date());

        historicoGrupoRepository.save(historicoGrupo);

        String semestreActual = calcularSemestre(new Date());
        // 5. Desmatricular estudiantes del curso original
        List<Matricula> matriculas = matriculaRepository.findBySemestreAndGrupoCohorteIdAndEstados(semestreActual,
                grupo);

        for (Matricula matricula : matriculas) {
            Estudiante estudiante = matricula.getEstudianteId();

            if (estudiante.getMoodleId() != null && !estudiante.getMoodleId().isEmpty()) {
                try {
                    moodleApiClient.desmatricularEstudiante(
                            estudiante.getMoodleId(),
                            grupo.getMoodleId());
                    log.debug("Estudiante {} desmatriculado del curso {}",
                            estudiante.getId(), grupo.getMoodleId());
                } catch (Exception e) {
                    log.warn("Error al desmatricular estudiante {} del curso {}: {}",
                            estudiante.getId(), grupo.getMoodleId(), e.getMessage());
                    // Continuamos con el siguiente estudiante
                }
            }
        }

        // 6. Actualizar el semestre del grupo para el siguiente periodo
        String siguienteSemestre = calcularSiguienteSemestre(grupo.getSemestre());
        grupo.setSemestre(siguienteSemestre);
        grupoCohorteRepository.save(grupo);
    }

    /**
     * Crear categoría para semestre romano bajo la categoría del semestre histórico
     */
    private String crearCategoriaSemestreRomano(String categoriaPadreId, String semestreRomano) {
        String nombreCategoria = "Semestre " + semestreRomano;

        // Usar el método crearCategoria de MoodleApiClient
        return moodleApiClient.crearCategoria(nombreCategoria, categoriaPadreId);
    }

    /**
     * Determina el semestre romano (I, II, III) al que pertenece un grupo
     */
    private String determinarSemestreRomanoDelGrupo(GrupoCohorte grupo) {
        // Determinar el semestre romano basado en la materia del grupo
        if (grupo.getGrupoId() != null &&
                grupo.getGrupoId().getMateriaId() != null &&
                grupo.getGrupoId().getMateriaId().getSemestrePensum() != null &&
                grupo.getGrupoId().getMateriaId().getSemestrePensum().getSemestreId() != null) {

            return grupo.getGrupoId().getMateriaId().getSemestrePensum().getSemestreId().getNumeroRomano();
        }

        // Si no se puede determinar, usar un valor predeterminado
        return "I";
    }

    private Date crearFecha(int dia, int mes, int año) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, año);
        cal.set(Calendar.MONTH, mes - 1); // Enero es 0
        cal.set(Calendar.DAY_OF_MONTH, dia);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private String calcularSemestre(Date fechaMatriculacion) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaMatriculacion);

        int mes = cal.get(Calendar.MONTH) + 1; // Enero = 0
        int anio = cal.get(Calendar.YEAR);

        return anio + "-" + (mes <= 6 ? "I" : "II");
    }

    /**
     * Calcula el siguiente semestre académico basado en el formato "YYYY-I" o
     * "YYYY-II"
     */
    private String calcularSiguienteSemestre(String semestreActual) {
        String[] partes = semestreActual.split("-");
        int anio = Integer.parseInt(partes[0]);
        String periodo = partes[1];

        if (periodo.equals("I")) {
            return anio + "-II";
        } else {
            return (anio + 1) + "-I";
        }
    }
}

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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.HistoricoGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.HistoricoSemestre;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.NotasException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.SemestreException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.HistoricoGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.HistoricoSemestreRepository;

import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ProgramaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.implementations.NotasServiceImplementation;

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
    private final ProgramaRepository programaRepository;

    @Autowired
    private NotasServiceImplementation notasServiceImplementation;

    private Programa programaActualEnContexto;
    private Materia materiaActualEnContexto;

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
     * Valida que el semestre que se va a terminar pueda cerrarse según la fecha
     * actual:
     * - Para terminar 2025-I: debe ser después del 1° de Julio del 2025
     * - Para terminar 2025-II: debe ser después del 1° de Enero del 2026
     * 
     * @param semestre Semestre a terminar (formato "YYYY-I" o "YYYY-II")
     * @throws SemestreException Si no se cumple con la validación de fechas
     */
    private void validarFechaCierreSemestre(String semestre) throws SemestreException {
        // Validar formato del semestre (YYYY-I o YYYY-II)
        if (!semestre.matches("\\d{4}-[I|II]")) {
            throw new SemestreException("Formato de semestre inválido. Debe ser YYYY-I o YYYY-II");
        }

        String[] partes = semestre.split("-");
        int anio = Integer.parseInt(partes[0]);
        String periodo = partes[1];

        Calendar fechaActual = Calendar.getInstance();
        int anioActual = fechaActual.get(Calendar.YEAR);
        int mesActual = fechaActual.get(Calendar.MONTH) + 1; // +1 porque enero es 0

        log.info("Validando cierre de semestre {}: Año actual={}, Mes actual={}",
                semestre, anioActual, mesActual);

        // Para semestre I (Enero-Junio), debe ser después del 1° de Julio del mismo año
        if ("I".equals(periodo)) {
            if (anioActual < anio || (anioActual == anio && mesActual < 7)) {
                throw new SemestreException("El semestre " + semestre +
                        " solo puede terminarse después del 1° de Julio de " + anio);
            }
        }
        // Para semestre II (Julio-Diciembre), debe ser después del 1° de Enero del año
        // siguiente
        else {
            if (anioActual < anio + 1) {
                throw new SemestreException("El semestre " + semestre +
                        " solo puede terminarse después del 1° de Enero de " + (anio + 1));
            }
        }

        log.info("Validación de fecha para terminar semestre {} superada", semestre);
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

        log.info("Iniciando proceso de terminar semestre {} para programa {}",
                semestre, programa.getNombre());

        // 2. Validar fecha de cierre del semestre
        // validarFechaCierreSemestre(semestre);

        // 3. Verificar si ya existe un histórico para este semestre (para evitar
        // duplicados)
        Optional<HistoricoSemestre> existenteHistorico = historicoSemestreRepository
                .findByProgramaAndSemestre(programa, semestre);

        if (existenteHistorico.isPresent()) {
            throw new SemestreException("Este semestre ya ha sido terminado para este programa. " +
                    "No se puede ejecutar el proceso nuevamente.");
        }

        // 4. Cargar los grupos del semestre actual
        List<GrupoCohorte> grupos = grupoCohorteRepository
                .findByGrupoId_MateriaId_PensumId_ProgramaIdAndSemestre(programa, semestre);

        if (grupos.isEmpty()) {
            throw new SemestreException("No se encontraron grupos para el semestre " +
                    semestre + " en el programa " + programa.getNombre());
        }

        log.info("Se encontraron {} grupos para procesar", grupos.size());

        // 5. Cerrar las notas de todos los grupos del semestre
        log.info("Iniciando cierre de notas para el semestre {} del programa {}",
                semestre, programa.getNombre());
        cerrarNotasPorSemestre(programa, usuario, semestre);
        log.info("Notas cerradas correctamente para todos los grupos");

        // 6. Crear histórico de semestre en la base de datos
        HistoricoSemestre historicoSemestre = crearHistoricoSemestre(programa, semestre);
        log.info("Histórico de semestre creado con ID: {}", historicoSemestre.getId());

        // 7. Crear categoría histórica en Moodle con la nueva estructura
        String categoriaHistoricaId = crearEstructuraHistoricaSemestre(programa, semestre);
        historicoSemestre.setMoodleCategoriaId(categoriaHistoricaId);
        historicoSemestreRepository.save(historicoSemestre);
        log.info("Estructura jerárquica creada en Moodle con ID: {}", categoriaHistoricaId);

        // 8. Calcular el siguiente semestre (para nuevos grupos)
        String siguienteSemestre = calcularSiguienteSemestre(semestre);

        // 9. Procesar cada grupo del semestre con la nueva lógica
        int totalGrupos = grupos.size();
        int gruposExitosos = 0;
        List<String> errores = new ArrayList<>();

        for (GrupoCohorte grupo : grupos) {
            try {
                log.info("Procesando grupo {} para histórico", grupo.getId());
                procesarGrupoParaHistoricoNuevo(grupo, programa, historicoSemestre, semestre, siguienteSemestre);
                gruposExitosos++;
                log.info("Grupo {} procesado exitosamente", grupo.getId());
            } catch (Exception e) {
                log.error("Error al procesar grupo {}: {}", grupo.getId(), e.getMessage(), e);
                errores.add("Error al procesar grupo " + grupo.getId() + ": " + e.getMessage());
            }
        }

        // 10. Cambiar al siguiente semestre en el programa
        programa.setSemestreActual(siguienteSemestre);
        programaRepository.save(programa);
        log.info("Semestre del programa actualizado a: {}", siguienteSemestre);

        // 11. Completar estadísticas
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("programaId", programaId);
        resultado.put("nombrePrograma", programa.getNombre());
        resultado.put("semestre", semestre);
        resultado.put("totalGrupos", totalGrupos);
        resultado.put("gruposExitosos", gruposExitosos);
        resultado.put("errores", errores);
        resultado.put("nuevoSemestre", siguienteSemestre);

        log.info("Proceso de terminar semestre completado. Grupos exitosos: {}/{}",
                gruposExitosos, totalGrupos);

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
     * Crea la estructura jerárquica completa en Moodle para el semestre histórico
     */
    private String crearEstructuraHistoricaSemestre(Programa programa, String semestre) {
        // Guardar programa en contexto para que esté disponible para otros métodos
        this.programaActualEnContexto = programa;

        // Verificar que el programa tenga categoría de históricos
        if (programa.getHistoricoMoodleId() == null || programa.getHistoricoMoodleId().isEmpty()) {
            throw new RuntimeException("El programa no tiene configurada una categoría de históricos en Moodle");
        }

        String categoriaHistoricosId = programa.getHistoricoMoodleId();
        String codigoPrograma = programa.getCodigo();

        log.info("Usando categoría de históricos del programa: {} con código: {}",
                categoriaHistoricosId, codigoPrograma);

        // Crear categoría para este semestre bajo históricos usando el idNumber con
        // formato codigoPrograma-periodo
        String categoriaSemestreId = moodleApiClient.crearCategoria(semestre, categoriaHistoricosId, semestre,
                codigoPrograma);
        log.info("Categoría para semestre histórico creada: {} (ID: {})", semestre, categoriaSemestreId);

        return categoriaSemestreId;
    }

    /**
     * Procesa un grupo para el histórico con la nueva lógica:
     * - El curso duplicado queda activo para el próximo semestre
     * - El curso original se mueve a histórico
     */
    @Transactional
    public void procesarGrupoParaHistoricoNuevo(GrupoCohorte grupo, Programa programa,
            HistoricoSemestre historicoSemestre, String semestreCerrado, String siguienteSemestre) {

        if (grupo.getMoodleId() == null || grupo.getMoodleId().isEmpty()) {
            log.warn("El grupo {} no tiene ID de Moodle. Omitiendo...", grupo.getId());
            return;
        }

        try {
            // Guardar referencias al programa y materia para contexto
            this.programaActualEnContexto = programa;
            this.materiaActualEnContexto = grupo.getGrupoId().getMateriaId();

            // 1. Determinar el semestre del grupo en números romanos (I, II, III, etc.)
            String semestreRomano = determinarSemestreRomanoDelGrupo(grupo);
            log.info("Procesando grupo {} para histórico. Semestre romano: {}", grupo.getId(), semestreRomano);

            // 2. Crear categoría del semestre romano bajo la categoría del semestre
            // histórico
            String categoriaSemestreRomanoId = crearCategoriaSemestreRomano(
                    historicoSemestre.getMoodleCategoriaId(),
                    semestreRomano,
                    semestreCerrado);

            log.info("Categoría para semestre romano creada: {}", categoriaSemestreRomanoId);

            // 3. Crear la categoría de la materia en el histórico con el FORMATO
            // ESPECIFICADO
            // Nombre: Nombre Materia - Periodo, idNumber: Código Materia - Periodo
            String nombreMateria = grupo.getGrupoId().getMateriaId().getNombre();
            String codigoMateria = grupo.getGrupoId().getMateriaId().getCodigo();

            // Usar el nuevo método específico para categorías de materia en histórico
            String categoriaMateriaHistoricaId = moodleApiClient.crearCategoriaMateriaHistorico(
                    nombreMateria, // Nombre de la materia
                    codigoMateria, // Código de la materia
                    semestreCerrado, // Periodo académico
                    categoriaSemestreRomanoId // Categoría padre (semestre romano)
            );

            log.info("Categoría histórica para materia creada: {}", categoriaMateriaHistoricaId);

            // 4. Generar nombres para el curso histórico según el formato
            String nombreCursoHistorico = construirNombreCursoHistorico(grupo, semestreCerrado);
            String shortNameCursoHistorico = construirShortNameCursoHistorico(grupo, semestreCerrado);

            log.info("Curso histórico: nombre={}, shortname={}", nombreCursoHistorico, shortNameCursoHistorico);

            // 5. Obtener la categoría de la materia actual donde se encuentra el curso
            String categoriaCursoActualId = obtenerCategoriaCursoActual(grupo.getMoodleId());
            if (categoriaCursoActualId == null) {
                // Si no se puede obtener, intentar obtener la categoría del semestre al menos
                categoriaCursoActualId = obtenerCategoriaProgramaParaSemestre(programa, semestreRomano);
                log.warn("No se pudo obtener la categoría actual del curso, usando la del semestre: {}",
                        categoriaCursoActualId);
            } else {
                log.info("Categoría actual del curso obtenida: {}", categoriaCursoActualId);
            }

            // 6. Configuración para el nuevo curso activo
            String nombreCursoNuevo = construirNombreCursoActivo(grupo, siguienteSemestre);
            String shortNameCursoNuevo = construirShortNameCursoActivo(grupo, siguienteSemestre);

            log.info("Curso nuevo: nombre={}, shortname={}", nombreCursoNuevo, shortNameCursoNuevo);

            // 7. Crear curso duplicado SIN ESTUDIANTES en la MISMA categoría donde está el
            // curso actual
            String cursoDuplicadoId = moodleApiClient.copiarCursoSinEstudiantes(
                    grupo.getMoodleId(),
                    categoriaCursoActualId, // Usar la misma categoría del curso actual
                    nombreCursoNuevo,
                    shortNameCursoNuevo);

            log.info("Curso duplicado exitosamente con ID: {}", cursoDuplicadoId);

            // 8. Mover el curso original a la categoría histórica de la materia
            moodleApiClient.actualizarCurso(
                    grupo.getMoodleId(),
                    nombreCursoHistorico,
                    shortNameCursoHistorico,
                    categoriaMateriaHistoricaId, // Usar la categoría de la materia histórica
                    "0", // Oculto
                    shortNameCursoHistorico); // idnumber igual al shortname

            log.info("Curso original movido a histórico con ID: {}", grupo.getMoodleId());

            // 9. Crear registro de grupo histórico
            HistoricoGrupo historicoGrupo = HistoricoGrupo.builder()
                    .grupoCohorte(grupo)
                    .historicoSemestre(historicoSemestre)
                    .moodleCursoOriginalId(grupo.getMoodleId())
                    .moodleCursoHistoricoId(grupo.getMoodleId()) // El curso histórico es el original
                    .fechaCreacion(new Date())
                    .build();

            historicoGrupoRepository.save(historicoGrupo);
            log.info("Registro histórico creado para el grupo {}", grupo.getId());

            // 10. Actualizar el grupo cohorte con el nuevo ID de Moodle y semestre
            grupo.setMoodleId(cursoDuplicadoId);
            grupo.setSemestre(siguienteSemestre);
            grupo.setSemestreTerminado(true);
            grupoCohorteRepository.save(grupo);

            log.info("Grupo {} procesado exitosamente. Nuevo semestre: {}, nuevo Moodle ID: {}",
                    grupo.getId(), siguienteSemestre, cursoDuplicadoId);

            // Limpiar variables de contexto
            this.programaActualEnContexto = null;
            this.materiaActualEnContexto = null;

        } catch (Exception e) {
            // Limpiar variables de contexto en caso de error
            this.programaActualEnContexto = null;
            this.materiaActualEnContexto = null;

            log.error("Error al procesar grupo {}: {}", grupo.getId(), e.getMessage(), e);
            throw new RuntimeException("Error al procesar grupo " + grupo.getId() + ": " + e.getMessage(), e);
        }
    }

    /**
     * Crea una categoría para la materia dentro del histórico
     * 
     * @param categoriaSemestreId Categoría del semestre romano donde se creará
     * @param nombreMateria       Nombre de la materia
     * @param periodoAcademico    Periodo académico (2025-I, etc.)
     * @return ID de la categoría creada
     */
    private String crearCategoriaMateriaEnHistorico(String categoriaSemestreId, String nombreMateria,
            String periodoAcademico) {

        // Obtener el código de la materia y programa del contexto
        String codigoMateria = "";
        String codigoPrograma = "";

        try {
            // Intentar obtener de la materia actual en contexto
            if (materiaActualEnContexto != null) {
                codigoMateria = materiaActualEnContexto.getCodigo();
            }

            // Intentar obtener el código del programa
            if (programaActualEnContexto != null) {
                codigoPrograma = programaActualEnContexto.getCodigo();
            }
        } catch (Exception e) {
            log.warn("Error al obtener códigos para categoría de materia: {}", e.getMessage());
        }

        // Si no tenemos código de materia, usar el nombre para extraer el código si es
        // posible
        if (codigoMateria == null || codigoMateria.isEmpty()) {
            // Intentar extraer el código si el nombre comienza con números (ej: "1155101 -
            // Cálculo")
            if (nombreMateria.matches("^\\d+.*")) {
                String[] parts = nombreMateria.split(" ", 2);
                codigoMateria = parts[0].trim();
            } else {
                // Usar un código genérico basado en el nombre de materia
                codigoMateria = nombreMateria.replaceAll("\\s+", "").substring(0,
                        Math.min(nombreMateria.length(), 10));
            }
        }

        // Nombre visible de la categoría: usar el código de la materia y periodo
        String nombreCategoriaVisible = codigoMateria + " - " + periodoAcademico;

        log.info("Creando categoría para materia '{} - {}' en histórico bajo semestre {} con código programa {}",
                codigoMateria, periodoAcademico, categoriaSemestreId, codigoPrograma);

        // El idNumber incluirá el código del programa, de la materia y el periodo
        // académico
        return moodleApiClient.crearCategoria(nombreCategoriaVisible, categoriaSemestreId, periodoAcademico,
                codigoPrograma);
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

    /**
     * Determina el semestre romano (I, II, III, etc.) al que pertenece el grupo
     * basándose en la información de la materia y su relación con el plan de
     * estudios.
     * 
     * @param grupo GrupoCohorte para el cual se quiere determinar el semestre
     *              romano
     * @return Número romano del semestre (I, II, III, etc.)
     */
    private String determinarSemestreRomanoDelGrupo(GrupoCohorte grupo) {
        try {
            log.info("Determinando semestre romano para el grupo {}", grupo.getId());

            // 1. Intenta obtener el semestre desde la relación materia -> semestrePensum ->
            // semestre
            if (grupo.getGrupoId() != null &&
                    grupo.getGrupoId().getMateriaId() != null &&
                    grupo.getGrupoId().getMateriaId().getSemestrePensum() != null &&
                    grupo.getGrupoId().getMateriaId().getSemestrePensum().getSemestreId() != null) {

                String semestreRomano = grupo.getGrupoId().getMateriaId().getSemestrePensum().getSemestreId()
                        .getNumeroRomano();
                log.info("Semestre romano obtenido de la relación materia-semestrePensum: {}", semestreRomano);
                return semestreRomano;
            }

            // 2. Intenta obtener el semestre desde el campo semestre de la materia
            if (grupo.getGrupoId() != null &&
                    grupo.getGrupoId().getMateriaId() != null &&
                    grupo.getGrupoId().getMateriaId().getSemestre() != null) {

                String semestreStr = grupo.getGrupoId().getMateriaId().getSemestre();
                log.info("Semestre obtenido del campo semestre de la materia: {}", semestreStr);

                // Si el semestre está almacenado como número, convertirlo a romano
                if (semestreStr.matches("\\d+")) {
                    int semestreNum = Integer.parseInt(semestreStr);
                    return convertirANumeroRomano(semestreNum);
                }
                // Si ya está en formato romano, devolverlo directamente
                else if (esNumeroRomano(semestreStr)) {
                    return semestreStr.toUpperCase();
                }
            }

            // 3. Intenta obtener información del semestre desde el código de la materia
            if (grupo.getGrupoId() != null &&
                    grupo.getGrupoId().getMateriaId() != null &&
                    grupo.getGrupoId().getMateriaId().getCodigo() != null) {

                String codigo = grupo.getGrupoId().getMateriaId().getCodigo();
                log.info("Intentando extraer semestre del código de materia: {}", codigo);

                // Muchos códigos de materia incluyen el semestre como un dígito (ej: MAT105 ->
                // semestre 1)
                // Esta es una heurística que busca patrones comunes
                for (int i = 0; i < codigo.length(); i++) {
                    if (Character.isDigit(codigo.charAt(i))) {
                        // Si encontramos un dígito que podría ser un semestre (entre 1 y 10)
                        char digitChar = codigo.charAt(i);
                        int digit = Character.getNumericValue(digitChar);

                        if (digit >= 1 && digit <= 10) {
                            log.info("Semestre extraído del código de materia: {}", digit);
                            return convertirANumeroRomano(digit);
                        }
                    }
                }
            }

            // 4. Si el grupo pertenece a una cohorte que tiene un semestre definido
            if (grupo.getCohorteId() != null && grupo.getSemestre() != null) {
                String semestreCohorte = grupo.getSemestre();
                log.info("Usando semestre de la cohorte: {}", semestreCohorte);

                // Si tiene formato numérico, convertir a romano
                if (semestreCohorte.matches("\\d+")) {
                    int semestreNum = Integer.parseInt(semestreCohorte);
                    return convertirANumeroRomano(semestreNum);
                }
                // Si ya tiene formato romano, usar directamente
                else if (esNumeroRomano(semestreCohorte)) {
                    return semestreCohorte.toUpperCase();
                }
            }

            // 5. Si todo falla, usar semestre I como predeterminado
            log.warn("No se pudo determinar el semestre romano para el grupo {}. Se usará el semestre I por defecto.",
                    grupo.getId());
            return "I";

        } catch (Exception e) {
            log.error("Error al determinar el semestre romano del grupo {}: {}", grupo.getId(), e.getMessage(), e);
            return "I"; // Valor predeterminado en caso de error
        }
    }

    /**
     * Convierte un número entero a su representación en números romanos
     * 
     * @param numero Número entero a convertir (1-12)
     * @return Representación en números romanos
     */
    private String convertirANumeroRomano(int numero) {
        switch (numero) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            case 6:
                return "VI";
            case 7:
                return "VII";
            case 8:
                return "VIII";
            case 9:
                return "IX";
            case 10:
                return "X";
            case 11:
                return "XI";
            case 12:
                return "XII";
            default:
                return "I";
        }
    }

    /**
     * Verifica si una cadena es un número romano válido
     * 
     * @param str Cadena a verificar
     * @return true si es un número romano válido, false en caso contrario
     */
    private boolean esNumeroRomano(String str) {
        return str.matches("^(?i)(?=[MDCLXVI])M*(C[MD]|D?C*)(X[CL]|L?X*)(I[XV]|V?I*)$");
    }

    /**
     * Crea categoría para semestre romano bajo la categoría del semestre histórico
     * 
     * @param categoriaPadreId ID de la categoría padre (semestre histórico)
     * @param semestreRomano   Número romano del semestre (I, II, III, etc.)
     * @param periodoAcademico Período académico (2025-I, 2025-II, etc.)
     * @return ID de la categoría creada
     */
    private String crearCategoriaSemestreRomano(String categoriaPadreId, String semestreRomano,
            String periodoAcademico) {

        // Obtener el código del programa para incluirlo en el idNumber
        String codigoPrograma = "";

        if (programaActualEnContexto != null) {
            codigoPrograma = programaActualEnContexto.getCodigo();
        }

        // Nombre de categoría con solo la primera letra de "Semestre" en mayúscula
        String nombreCategoria = "Semestre " + semestreRomano;

        log.info("Creando categoría de semestre romano '{}' con periodo académico '{}' y código programa '{}'",
                nombreCategoria, periodoAcademico, codigoPrograma);

        // El idNumber ahora incluirá el código de programa, seguido de
        // Semestre+Romano+Periodo
        return moodleApiClient.crearCategoria(nombreCategoria, categoriaPadreId, periodoAcademico, codigoPrograma);
    }

    /**
     * Obtiene el ID de la categoría del semestre correspondiente en la estructura
     * del programa
     * 
     * @param programa       Programa académico
     * @param semestreRomano Número romano del semestre (I, II, III, etc.)
     * @return ID de la categoría del semestre en el programa
     */
    private String obtenerCategoriaProgramaParaSemestre(Programa programa, String semestreRomano) {
        String nombreCategoriaABuscar = "Semestre " + semestreRomano;
        log.info("Buscando categoría para {} en programa {}", nombreCategoriaABuscar, programa.getId());

        String categoriaProgramaId = programa.getMoodleId();
        if (categoriaProgramaId == null || categoriaProgramaId.isEmpty()) {
            throw new RuntimeException("El programa no tiene ID de Moodle configurado");
        }

        String categoriaSemestreId = moodleApiClient.buscarCategoria(nombreCategoriaABuscar, categoriaProgramaId);

        if (categoriaSemestreId == null) {
            log.warn("No se encontró la categoría {} en el programa. Intentando crearla.", nombreCategoriaABuscar);
            // Si no existe la categoría del semestre, intentamos crearla
            categoriaSemestreId = moodleApiClient.crearCategoria(nombreCategoriaABuscar, categoriaProgramaId, null);

            if (categoriaSemestreId == null) {
                throw new RuntimeException("No se pudo encontrar ni crear la categoría del semestre " + semestreRomano +
                        " en el programa. Verifique que existe en Moodle o que tiene permisos para crearla.");
            }
        }

        log.info("Encontrada/creada categoría para semestre {} en programa: {}", semestreRomano, categoriaSemestreId);
        return categoriaSemestreId;
    }

    /**
     * Construye el nombre completo del curso que irá a histórico
     * 
     * @param grupo   Grupo cohorte
     * @param periodo Período académico (2025-I, 2025-II, etc.)
     * @return Nombre completo del curso histórico
     */
    private String construirNombreCursoHistorico(GrupoCohorte grupo, String periodo) {
        String nombreMateria = grupo.getGrupoId().getMateriaId().getNombre();
        String letraGrupo = extraerLetraGrupo(grupo.getGrupoId().getCodigo());

        // Formato: [Nombre de la materia] - Grupo [Letra] [Periodo académico]
        return nombreMateria + " - Grupo " + letraGrupo + " " + periodo;
    }

    /**
     * Construye el shortname/idnumber del curso que irá a histórico
     * 
     * @param grupo   Grupo cohorte
     * @param periodo Período académico (2025-I, 2025-II, etc.)
     * @return Shortname/idnumber del curso histórico
     */
    private String construirShortNameCursoHistorico(GrupoCohorte grupo, String periodo) {
        String codigoMateria = grupo.getGrupoId().getMateriaId().getCodigo();
        String letraGrupo = extraerLetraGrupo(grupo.getGrupoId().getCodigo());

        // Formato: [Código de materia sin espacios][Letra del grupo]-[Periodo
        // académico]
        return codigoMateria.replaceAll("\\s+", "") + letraGrupo + "-" + periodo;
    }

    /**
     * Construye el nombre completo del curso activo para el nuevo semestre
     * 
     * @param grupo   Grupo cohorte
     * @param periodo Período académico para el nuevo semestre (2025-II, 2026-I,
     *                etc.)
     * @return Nombre completo del curso activo
     */
    private String construirNombreCursoActivo(GrupoCohorte grupo, String periodo) {
        String nombreMateria = grupo.getGrupoId().getMateriaId().getNombre();
        String letraGrupo = extraerLetraGrupo(grupo.getGrupoId().getCodigo());

        // Formato: [Nombre de la materia] - Grupo [Letra] [Periodo académico]
        return nombreMateria + " - Grupo " + letraGrupo + " " + periodo;
    }

    /**
     * Construye el shortname/idnumber del curso activo para el nuevo semestre
     * 
     * @param grupo   Grupo cohorte
     * @param periodo Período académico para el nuevo semestre (2025-II, 2026-I,
     *                etc.)
     * @return Shortname/idnumber del curso activo
     */
    private String construirShortNameCursoActivo(GrupoCohorte grupo, String periodo) {
        String codigoMateria = grupo.getGrupoId().getMateriaId().getCodigo();
        String letraGrupo = extraerLetraGrupo(grupo.getGrupoId().getCodigo());

        // Formato: [Código de materia sin espacios][Letra del grupo]-[Periodo
        // académico]
        return codigoMateria.replaceAll("\\s+", "") + letraGrupo + "-" + periodo;
    }

    /**
     * Extrae la letra del grupo desde el código (ejemplo: "Grupo A" -> "A",
     * "1155101-A" -> "A")
     * 
     * @param codigo Código del grupo
     * @return Letra del grupo
     */
    private String extraerLetraGrupo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            return "A";
        }

        // Si el código termina con una letra después de un guion (1155101-A), tomar esa
        // letra
        if (codigo.contains("-") && codigo.length() > codigo.indexOf("-") + 1) {
            return codigo.substring(codigo.indexOf("-") + 1);
        }

        // Si el código contiene "Grupo X", extraer la X
        if (codigo.toLowerCase().contains("grupo") && codigo.length() > codigo.toLowerCase().indexOf("grupo") + 6) {
            return codigo.substring(codigo.toLowerCase().indexOf("grupo") + 6).trim();
        }

        // Si el código tiene una letra al final (por ejemplo MTIC101A), extraer esa
        // letra
        if (codigo.length() > 0 && Character.isLetter(codigo.charAt(codigo.length() - 1))) {
            return String.valueOf(codigo.charAt(codigo.length() - 1));
        }

        // Por defecto retornar la última letra/número si existe
        if (!codigo.isEmpty()) {
            return codigo.substring(codigo.length() - 1);
        }

        return "A";
    }

    /**
     * Obtiene la categoría a la que pertenece un curso
     * 
     * @param cursoId ID del curso en Moodle
     * @return ID de la categoría si se puede obtener, null en caso contrario
     */
    private String obtenerCategoriaCursoActual(String cursoId) {
        try {
            log.info("Obteniendo categoría del curso {}", cursoId);

            // Llamar a MoodleApiClient para obtener datos del curso
            String categoriaCursoId = moodleApiClient.obtenerCategoriaCurso(cursoId);

            if (categoriaCursoId != null && !categoriaCursoId.isEmpty()) {
                log.info("Categoría del curso {} obtenida: {}", cursoId, categoriaCursoId);
                return categoriaCursoId;
            } else {
                log.warn("No se pudo obtener la categoría del curso {}", cursoId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error al obtener categoría del curso {}: {}", cursoId, e.getMessage());
            return null;
        }
    }
}
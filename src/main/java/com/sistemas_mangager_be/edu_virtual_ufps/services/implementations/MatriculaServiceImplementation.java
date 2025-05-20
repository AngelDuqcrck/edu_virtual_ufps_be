package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.CambioEstadoMatricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoMatricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Materia;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Matricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MateriaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MatriculaException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CambioEstadoMatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstadoMatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MateriaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IMatriculaService;
import com.sistemas_mangager_be.edu_virtual_ufps.services.moodle.MoodleMatriculaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MateriaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MatriculaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.CambioEstadoMatriculaResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.CorreoResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.GrupoCohorteDocenteResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.MateriaPensumResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.MatriculaResponse;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.PensumResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MatriculaServiceImplementation implements IMatriculaService {

        public static final String IS_ALREADY_USE = "%s ya esta en uso";
        public static final String IS_NOT_FOUND = "%s no fue encontrado";
        public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
        public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
        public static final String IS_NOT_VALID = "%s no es valido";
        public static final String ARE_NOT_EQUALS = "%s no son iguales";
        public static final String IS_NOT_CORRECT = "%s no es correcta";

        @Autowired
        private MoodleMatriculaService moodleMatriculaService;

        @Autowired
        private CambioEstadoMatriculaRepository cambioEstadoMatriculaRepository;

        @Autowired
        private EstadoMatriculaRepository estadoMatriculaRepository;

        @Autowired
        private EstudianteRepository estudianteRepository;

        @Autowired
        private GrupoCohorteRepository grupoCohorteRepository;

        @Autowired
        private MatriculaRepository matriculaRepository;

        @Autowired
        private MateriaRepository materiaRepository;

        @Autowired
        private EmailService emailService;

        public MatriculaDTO crearMatricula(MatriculaDTO matriculaDTO, String usuario)
                        throws EstudianteNotFoundException, GrupoNotFoundException, MatriculaException {

                // Validar que el estudiante existe
                Estudiante estudiante = estudianteRepository.findById(matriculaDTO.getEstudianteId())
                                .orElseThrow(() -> new EstudianteNotFoundException(
                                                String.format(IS_NOT_FOUND, "Estudiante con ID: "
                                                                + matriculaDTO.getEstudianteId())));

                // Validar que el grupo cohorte existe
                GrupoCohorte grupoCohorte = grupoCohorteRepository.findById(matriculaDTO.getGrupoCohorteId())
                                .orElseThrow(() -> new GrupoNotFoundException(
                                                String.format(IS_NOT_FOUND, "Grupo cohorte con ID: "
                                                                + matriculaDTO.getGrupoCohorteId())));

                // Obtener la materia del grupo
                Materia materia = grupoCohorte.getGrupoId().getMateriaId();
                if (materia == null) {
                        throw new MatriculaException("El grupo no tiene una materia asignada");
                }

                // Validar que el estudiante no tiene una matrícula activa en esta materia
                validarMatriculaPorMateria(estudiante, materia);

                // Obtener el estado por defecto (En curso)
                EstadoMatricula estadoMatricula = estadoMatriculaRepository.findById(2) // ID 2 = "En curso"
                                .orElseThrow(() -> new MatriculaException(
                                                String.format(IS_NOT_FOUND, "Estado de matrícula por defecto")));

                if (matriculaDTO.isNuevaMatricula()) {
                        matriculaDTO.setFechaMatriculacion(new Date());
                }

                // Calcular el semestre basado en el semestre actual del estudiante
                String semestre = grupoCohorte.getGrupoId().getMateriaId().getPensumId().getProgramaId().getSemestreActual();

                // Crear la entidad Matricula
                Matricula matricula = Matricula.builder()
                                .estudianteId(estudiante)
                                .grupoCohorteId(grupoCohorte)
                                .fechaMatriculacion(matriculaDTO.getFechaMatriculacion() != null
                                                ? matriculaDTO.getFechaMatriculacion()
                                                : new Date())
                                .nuevaMatricula(matriculaDTO.isNuevaMatricula())
                                .estadoMatriculaId(estadoMatricula)
                                .semestre(semestre)
                                .notaAbierta(true)
                                .build();

                // Integración con Moodle - Solo si tanto el estudiante como el grupo tienen
                // moodleId
                if (estudiante.getMoodleId() != null && !estudiante.getMoodleId().isEmpty() &&
                                grupoCohorte.getMoodleId() != null && !grupoCohorte.getMoodleId().isEmpty()) {
                        try {
                                moodleMatriculaService.matricularEstudianteEnMoodle(estudiante, grupoCohorte);
                        } catch (Exception e) {
                                // Log del error pero permitir que la transacción continúe
                                log.error("Error al matricular en Moodle: {}", e.getMessage());
                        }
                }
                
                // Guardar la matrícula
                matricula = matriculaRepository.save(matricula);

                crearCambioEstadoMatricula(matricula, estadoMatricula, usuario);

                

                return convertirAmatriculaDTO(matricula);
        }

        /**
         * Valida si el estudiante ya tiene una matrícula activa o aprobada en la
         * materia
         * 
         * @param estudiante El estudiante a validar
         * @param materia    La materia a validar
         * @throws MatriculaException Si el estudiante ya tiene una matrícula
         *                            activa/aprobada en la materia
         */
        private void validarMatriculaPorMateria(Estudiante estudiante, Materia materia) throws MatriculaException {
                // Estados bloqueantes: 1=Aprobado, 2=En curso
                boolean tieneMatriculaInvalida = matriculaRepository.existsByEstudianteAndMateriaWithActiveStatus(
                                estudiante, materia);

                if (tieneMatriculaInvalida) {
                        // Opcional: Obtener detalles para el mensaje de error
                        List<Matricula> matriculas = matriculaRepository.findByEstudianteAndMateriaAndEstados(
                                        estudiante, materia, List.of(1, 2));

                        String estadoActual = matriculas.stream()
                                        .findFirst()
                                        .map(m -> m.getEstadoMatriculaId().getNombre())
                                        .orElse("");

                        throw new MatriculaException(
                                        String.format("El estudiante ya tiene esta materia %s (Estado: %s)",
                                                        materia.getNombre(), estadoActual));
                }
        }

        public void anularMatricula(Long idMatricula, String usuario) throws MatriculaException {

                // Obtener la matrícula
                Matricula matricula = matriculaRepository.findById(idMatricula)
                                .orElseThrow(() -> new MatriculaException(
                                                String.format(IS_NOT_FOUND, "Matricula con ID: " + idMatricula)));

                if (matricula.getEstadoMatriculaId().getId() != 2) {
                        throw new MatriculaException("La matrícula no se encuentra en estado 'En curso'");
                }

                EstadoMatricula estadoMatricula = estadoMatriculaRepository.findById(5)
                                .orElseThrow(() -> new MatriculaException(String
                                                .format(IS_NOT_ALLOWED,
                                                                "El estado de matricula " + matricula
                                                                                .getEstadoMatriculaId().getId())
                                                .toLowerCase()));

                // Integración con Moodle - Desmatricualción
                Estudiante estudiante = matricula.getEstudianteId();
                GrupoCohorte grupoCohorte = matricula.getGrupoCohorteId();

                if (estudiante.getMoodleId() != null && !estudiante.getMoodleId().isEmpty() &&
                                grupoCohorte.getMoodleId() != null && !grupoCohorte.getMoodleId().isEmpty()) {
                        try {
                                moodleMatriculaService.desmatricularEstudianteEnMoodle(estudiante, grupoCohorte);
                        } catch (Exception e) {
                                // Log del error pero permitir que la transacción continúe
                                log.error("Error al desmatricular en Moodle: {}", e.getMessage());
                        }
                }
                matricula.setEstadoMatriculaId(estadoMatricula);
                crearCambioEstadoMatricula(matricula, estadoMatricula, usuario);
                matriculaRepository.save(matricula);

        }

        public List<GrupoCohorteDocenteResponse> listarGrupoCohorteDocentePorMateria(String codigoMateria)
                        throws MateriaNotFoundException {

                Materia materia = materiaRepository.findByCodigo(codigoMateria)
                                .orElseThrow(() -> new MateriaNotFoundException(
                                                String.format(IS_NOT_FOUND_F, "La Materia con ID: " + codigoMateria)
                                                                .toLowerCase()));

                List<GrupoCohorte> grupos = grupoCohorteRepository.findByMateriaIdWithRelations(materia.getId());

                return grupos.stream()
                                .map(this::convertirAResponse)
                                .collect(Collectors.toList());
        }

        public List<MatriculaResponse> listarMatriculasEnCursoPorEstudiante(Integer estudianteId)
                        throws EstudianteNotFoundException {
                Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
                if (estudiante == null) {
                        throw new EstudianteNotFoundException(
                                        String.format(IS_NOT_FOUND, "El estudiante con ID: " + estudianteId));
                }
                String semestreActual = estudiante.getProgramaId().getSemestreActual();
                List<Matricula> matriculas = matriculaRepository.findByEstudianteAndSemestreAndEstados(estudiante,
                                semestreActual);
                return matriculas.stream().map(matricula -> {
                        MatriculaResponse matriculaResponse = new MatriculaResponse();
                        BeanUtils.copyProperties(matricula, matriculaResponse);
                        matriculaResponse.setEstadoMatriculaId(matricula.getEstadoMatriculaId().getId());
                        matriculaResponse.setEstadoMatriculaNombre(matricula.getEstadoMatriculaId().getNombre());
                        matriculaResponse.setGrupoId(matricula.getGrupoCohorteId().getGrupoId().getId());
                        matriculaResponse.setGrupoNombre(matricula.getGrupoCohorteId().getGrupoId().getNombre());
                        matriculaResponse.setNombreMateria(
                                        matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getNombre());
                        matriculaResponse.setCodigoMateria(
                                        matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getCodigo());
                        matriculaResponse.setEstudianteId(matricula.getEstudianteId().getId());
                        matriculaResponse.setEstudianteNombre(matricula.getEstudianteId().getNombre());
                        matriculaResponse.setFechaMatriculacion(matricula.getFechaMatriculacion());
                        matriculaResponse.setSemestreMateria(
                                        matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getSemestre());
                        matriculaResponse.setCreditos(
                                        matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getCreditos());
                        matriculaResponse.setNota(matricula.getNota());
                        matriculaResponse.setFechaNota(matricula.getFechaNota());
                        return matriculaResponse;
                }).toList();
        }

        @Override
        public List<MateriaDTO> listarMateriasNoMatriculadasPorEstudiante(Integer estudianteId)
                        throws EstudianteNotFoundException {

                Estudiante estudiante = estudianteRepository.findById(estudianteId)
                                .orElseThrow(() -> new EstudianteNotFoundException(
                                                "Estudiante con ID " + estudianteId + " no encontrado"));

                Pensum pensum = estudiante.getPensumId();

                // Obtener todas las materias del pensum ordenadas por código ascendente
                List<Materia> materiasDelPensum = materiaRepository.findByPensumIdOrderBySemestreAscCodigoAsc(pensum);

                List<MateriaDTO> materiasNoMatriculadas = new ArrayList<>();

                for (Materia materia : materiasDelPensum) {
                        boolean yaMatriculada = matriculaRepository
                                        .existsByEstudianteAndMateriaWithActiveStatus(estudiante, materia);
                        if (!yaMatriculada) {
                                MateriaDTO dto = new MateriaDTO();
                                BeanUtils.copyProperties(materia, dto);
                                dto.setPensumId(pensum.getId());
                                materiasNoMatriculadas.add(dto);
                        }
                }

                return materiasNoMatriculadas;
        }

        @Override
        public List<PensumResponse> listarPensumPorEstudiante(Integer estudianteId)
                        throws EstudianteNotFoundException {

                // 1. Obtener el estudiante con su programa y pensum
                Estudiante estudiante = estudianteRepository.findById(estudianteId)
                                .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado"));

                Pensum pensum = estudiante.getPensumId();
                if (pensum == null) {
                        throw new RuntimeException("El estudiante no tiene un pensum asignado");
                }

                // 2. Obtener todas las matrículas del estudiante
                List<Matricula> matriculasEstudiante = matriculaRepository.findByEstudianteId(estudiante);

                // 3. Obtener todas las materias del pensum agrupadas por semestre
                Map<String, List<Materia>> materiasPorSemestre = materiaRepository
                                .findByPensumIdOrderBySemestreAscCodigoAsc(pensum)
                                .stream()
                                .collect(Collectors.groupingBy(Materia::getSemestre));

                // 4. Construir la respuesta
                return materiasPorSemestre.entrySet().stream()
                                .map(entry -> construirPensumResponse(
                                                pensum.getNombre(),
                                                entry.getKey(),
                                                entry.getValue(),
                                                matriculasEstudiante))
                                .sorted(Comparator.comparing(PensumResponse::getSemestrePensum))
                                .collect(Collectors.toList());
        }

        @Override
        public CorreoResponse enviarCorreo(Integer estudianteId, String usuario)
                        throws EstudianteNotFoundException, MatriculaException {
                // 1. Obtener el estudiante
                Estudiante estudiante = estudianteRepository.findById(estudianteId)
                                .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado"));

                // 2. Obtener las matrículas en curso del estudiante
                List<Matricula> matriculas = matriculaRepository.findByEstudianteIdAndEstadoMatriculaId_Id(
                                estudiante, 2); // 2 = En curso

                // Verificar si hay matrículas para notificar
                if (matriculas.isEmpty()) {
                        throw new MatriculaException("El estudiante no tiene matrículas en curso");
                }

                // 3. Mapear a MatriculaResponse
                List<MatriculaResponse> matriculasResponse = matriculas.stream()
                                .map(this::convertirAMatriculaResponse)
                                .collect(Collectors.toList());

                // 4. Construir el CorreoResponse
                CorreoResponse correoResponse = CorreoResponse.builder()
                                .nombreEstudiante(estudiante.getNombre() + " " + estudiante.getApellido())
                                .correo(estudiante.getEmail())
                                .semestre(estudiante.getProgramaId().getSemestreActual())
                                .fecha(new Date())
                                .matriculas(matriculasResponse)
                                .build();

                // 5. Enviar el correo
                emailService.sendEmail(
                                estudiante.getEmail(),
                                "Matricula Académica - " + correoResponse.getSemestre(),
                                correoResponse);

                // 6. Actualizar las matrículas para indicar que se envió el correo
                EstadoMatricula estadoCorreoEnviado = estadoMatriculaRepository.findById(6)
                                .orElseThrow(() -> new MatriculaException("Estado 'Correo enviado' no configurado"));
                crearCambioEstadoMatricula(null, estadoCorreoEnviado, usuario);
                Date ahora = new Date();
                matriculas.forEach(matricula -> {
                        matricula.setCorreoEnviado(true);
                        matricula.setFechaCorreoEnviado(ahora);
                        matriculaRepository.save(matricula);
                        crearCambioEstadoMatricula(matricula, estadoCorreoEnviado, usuario);
                });

                return correoResponse;
        }

        public boolean verificarCorreoEnviado(Integer estudianteId) throws EstudianteNotFoundException {
                Estudiante estudiante = estudianteRepository.findById(estudianteId)
                                .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado"));

                // Buscar al menos una matrícula en curso que tenga el correo enviado
                return matriculaRepository.existsByEstudianteIdAndEstadoMatriculaId_IdAndCorreoEnviado(
                                estudiante, 2, true);
        }

        public List<CambioEstadoMatriculaResponse> listarCambiosdeEstadoMatriculaPorEstudiante(Integer estudianteId)
                        throws EstudianteNotFoundException, MatriculaException {
                Estudiante estudiante = estudianteRepository.findById(estudianteId)
                                .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado"));

                String semestre = estudiante.getProgramaId().getSemestreActual();
                List<CambioEstadoMatricula> cambiosEstado = cambioEstadoMatriculaRepository
                                .findByMatriculaId_EstudianteIdAndSemestre(estudiante, semestre);
                if (cambiosEstado.isEmpty()) {
                        throw new MatriculaException("No se encontraron cambios de estado para el estudiante");
                }

                return cambiosEstado.stream()
                                .map(cambio -> {
                                        CambioEstadoMatriculaResponse response = new CambioEstadoMatriculaResponse();
                                        BeanUtils.copyProperties(cambio, response);
                                        response.setEstadoMatriculaId(cambio.getEstadoMatriculaId().getId());
                                        response.setEstadoMatriculaNombre(cambio.getEstadoMatriculaId().getNombre());
                                        response.setMateriaId(cambio.getMatriculaId().getGrupoCohorteId().getGrupoId()
                                                        .getMateriaId().getId());
                                        response.setMateriaNombre(
                                                        cambio.getMatriculaId().getGrupoCohorteId().getGrupoId()
                                                                        .getMateriaId().getNombre());
                                        response.setMateriaCodigo(cambio.getMatriculaId().getGrupoCohorteId()
                                                        .getGrupoId().getMateriaId()
                                                        .getCodigo());
                                        response.setGrupoId(cambio.getMatriculaId().getGrupoCohorteId().getGrupoId()
                                                        .getId());
                                        response.setGrupoNombre(cambio.getMatriculaId().getGrupoCohorteId().getGrupoId()
                                                        .getNombre());
                                        response.setGrupoCodigo(cambio.getMatriculaId().getGrupoCohorteId().getGrupoId()
                                                        .getCodigo());
                                        response.setFechaCambioEstadoMatricula(cambio.getFechaCambioEstado());
                                        response.setUsuarioCambioEstadoMatricula(cambio.getUsuarioCambioEstado());
                                        return response;
                                })
                                .collect(Collectors.toList());
        }
        // <-----------------------------------------------METODOS
        // AUXILIARES------------------------------------------------>

        private MatriculaResponse convertirAMatriculaResponse(Matricula matricula) {
                return MatriculaResponse.builder()
                                .id(matricula.getId())
                                .estadoMatriculaId(matricula.getEstadoMatriculaId().getId())
                                .estadoMatriculaNombre(matricula.getEstadoMatriculaId().getNombre())
                                .estudianteId(matricula.getEstudianteId().getId())
                                .estudianteNombre(matricula.getEstudianteId().getNombre())
                                .fechaMatriculacion(matricula.getFechaMatriculacion())
                                .nota(matricula.getNota())
                                .grupoId(matricula.getGrupoCohorteId().getGrupoId().getId())
                                .grupoNombre(matricula.getGrupoCohorteId().getGrupoId().getNombre())
                                .nombreMateria(matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getNombre())
                                .codigoMateria(matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getCodigo())
                                .semestreMateria(
                                                matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getSemestre())
                                .creditos(matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getCreditos())
                                .correoEnviado(matricula.isCorreoEnviado())
                                .fechaCorreoEnviado(matricula.getFechaCorreoEnviado())
                                .build();
        }

        private PensumResponse construirPensumResponse(
                        String pensumNombre,
                        String semestre,
                        List<Materia> materias,
                        List<Matricula> matriculas) {

                List<MateriaPensumResponse> materiasResponse = materias.stream()
                                .map(materia -> mapearMateriaResponse(materia, matriculas))
                                .collect(Collectors.toList());

                return PensumResponse.builder()
                                .pensumNombre(pensumNombre)
                                .semestrePensum(semestre)
                                .materias(materiasResponse)
                                .build();
        }

        private MateriaPensumResponse mapearMateriaResponse(Materia materia, List<Matricula> matriculas) {
                // Buscar matrícula más reciente para esta materia
                Optional<Matricula> matriculaOpt = matriculas.stream()
                                .filter(m -> m.getGrupoCohorteId() != null
                                                && m.getGrupoCohorteId().getGrupoId() != null
                                                && m.getGrupoCohorteId().getGrupoId().getMateriaId() != null
                                                && m.getGrupoCohorteId().getGrupoId().getMateriaId().equals(materia))
                                .max(Comparator.comparing(Matricula::getFechaMatriculacion));

                Integer estadoId = null;
                String estadoNombre = "No matriculada";
                String semestreAprobado = null;

                if (matriculaOpt.isPresent()) {
                        Matricula matricula = matriculaOpt.get();
                        if (matricula.getEstadoMatriculaId() != null) {
                                estadoId = matricula.getEstadoMatriculaId().getId();
                                estadoNombre = matricula.getEstadoMatriculaId().getNombre();
                                semestreAprobado = estadoId == 1 ? matricula.getSemestre() : null;
                        }
                }

                return MateriaPensumResponse.builder()
                                .codigo(materia.getCodigo())
                                .nombre(materia.getNombre())
                                .creditos(materia.getCreditos())
                                .semestreAprobado(semestreAprobado)
                                .estadoId(estadoId)
                                .estadoNombre(estadoNombre)
                                .colorCard(asignarColorPorEstado(estadoId) + "66") // Agrega opacidad 40% (66 en hex)
                                .build();
        }

        private String asignarColorPorEstado(Integer estadoId) {
                if (estadoId == null) {
                        return "#BC0017"; // Color por defecto para no matriculadas
                }
                return switch (estadoId) {
                        case 1 -> "#17C964"; // Aprobado
                        case 2 -> "#F5A524"; // En curso
                        default -> "#BC0017"; // Reprobado u otros estados
                };
        }

        private String calcularSemestre(Date fechaMatriculacion) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaMatriculacion);

                int mes = cal.get(Calendar.MONTH) + 1; // Enero = 0
                int anio = cal.get(Calendar.YEAR);

                return anio + "-" + (mes <= 6 ? "I" : "II");
        }

        private MatriculaDTO convertirAmatriculaDTO(Matricula matricula) {
                return MatriculaDTO.builder()
                                .id(matricula.getId().intValue())
                                .estudianteId(matricula.getEstudianteId().getId())
                                .grupoCohorteId(matricula.getGrupoCohorteId().getId())
                                .nuevaMatricula(matricula.isNuevaMatricula())
                                .fechaMatriculacion(matricula.getFechaMatriculacion())
                                .build();
        }

        private GrupoCohorteDocenteResponse convertirAResponse(GrupoCohorte grupoCohorte) {
                return GrupoCohorteDocenteResponse.builder()
                                .id(grupoCohorte.getId())
                                .grupoCohorteId(grupoCohorte.getId())
                                .grupoId(grupoCohorte.getGrupoId() != null ? grupoCohorte.getGrupoId().getId() : null)
                                .cohorteGrupoId(grupoCohorte.getCohorteGrupoId() != null
                                                ? grupoCohorte.getCohorteGrupoId().getId()
                                                : null)
                                .docenteId(grupoCohorte.getDocenteId() != null ? grupoCohorte.getDocenteId().getId()
                                                : null)
                                .docenteNombre(grupoCohorte.getDocenteId() != null
                                                ? grupoCohorte.getDocenteId().getNombreCompleto()
                                                : "Sin asignar")
                                .cohorteGrupoNombre(grupoCohorte.getCohorteGrupoId() != null
                                                ? grupoCohorte.getCohorteGrupoId().getNombre()
                                                : null)
                                .cohorteId(grupoCohorte.getCohorteId() != null ? grupoCohorte.getCohorteId().getId()
                                                : null)
                                .cohorteNombre(grupoCohorte.getCohorteId() != null
                                                ? grupoCohorte.getCohorteId().getNombre()
                                                : null)
                                .fechaCreacion(grupoCohorte.getFechaCreacion() != null
                                                ? grupoCohorte.getFechaCreacion().toString()
                                                : null)
                                .grupoNombre(grupoCohorte.getGrupoId() != null ? grupoCohorte.getGrupoId().getNombre()
                                                : null)
                                .codigoGrupo(grupoCohorte.getGrupoId().getCodigo())
                                .materia(grupoCohorte.getGrupoId() != null
                                                && grupoCohorte.getGrupoId().getMateriaId() != null
                                                                ? grupoCohorte.getGrupoId().getMateriaId().getNombre()
                                                                : null)
                                .codigoMateria(grupoCohorte.getGrupoId() != null
                                                && grupoCohorte.getGrupoId().getMateriaId() != null
                                                                ? grupoCohorte.getGrupoId().getMateriaId().getCodigo()
                                                                : null)
                                .semestreMateria(grupoCohorte.getGrupoId() != null
                                                && grupoCohorte.getGrupoId().getMateriaId().getSemestre() != null
                                                                ? grupoCohorte.getGrupoId().getMateriaId().getSemestre()
                                                                : null)
                                .build();
        }

        private void crearCambioEstadoMatricula(Matricula matricula, EstadoMatricula estadoMatricula, String usuario) {
                CambioEstadoMatricula cambioEstado = new CambioEstadoMatricula();
                cambioEstado.setMatriculaId(matricula);
                cambioEstado.setEstadoMatriculaId(estadoMatricula);
                cambioEstado.setFechaCambioEstado(new Date());
                cambioEstado.setUsuarioCambioEstado(usuario);
                cambioEstado.setSemestre(matricula.getSemestre());

                cambioEstadoMatriculaRepository.save(cambioEstado);

        }
}

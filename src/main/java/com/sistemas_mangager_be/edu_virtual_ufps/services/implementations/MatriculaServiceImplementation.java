package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoMatricula;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Grupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.GrupoCohorte;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Matricula;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.GrupoNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.MatriculaException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstadoMatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoCohorteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.GrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.MatriculaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IMatriculaService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.MatriculaDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.MatriculaResponse;

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
        private EstadoMatriculaRepository estadoMatriculaRepository;

        @Autowired
        private EstudianteRepository estudianteRepository;

        @Autowired
        private GrupoCohorteRepository grupoCohorteRepository;

        @Autowired
        private MatriculaRepository matriculaRepository;

        public MatriculaDTO crearMatricula(MatriculaDTO matriculaDTO)
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

                // Validar que el estudiante no tiene una matrícula activa en este grupo
                validarMatriculaExistente(estudiante, grupoCohorte);

                // Obtener el estado por defecto (En curso)
                EstadoMatricula estadoMatricula = estadoMatriculaRepository.findById(2) // ID 2 = "En curso"
                                .orElseThrow(() -> new MatriculaException(
                                                String.format(IS_NOT_FOUND, "Estado de matrícula por defecto")));

                if (matriculaDTO.isNuevaMatricula()) {
                        matriculaDTO.setFechaMatriculacion(new Date());
                }
                // Calcular el semestre basado en la fecha actual
                String semestre = calcularSemestre(matriculaDTO.getFechaMatriculacion());

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
                                .build();

                // Guardar la matrícula
                matricula = matriculaRepository.save(matricula);

                // Convertir a DTO para retornar
                return convertirAmatriculaDTO(matricula);
        }

        public void anularMatricula(Long idMatricula) throws MatriculaException {

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
                matricula.setEstadoMatriculaId(estadoMatricula);

                matriculaRepository.save(matricula);

        }

        public List<MatriculaResponse> listarMatriculasEnCursoPorEstudiante(Integer estudianteId) throws EstudianteNotFoundException {
            Estudiante estudiante = estudianteRepository.findById(estudianteId).orElse(null);
            if (estudiante == null) {
                throw new EstudianteNotFoundException(String.format(IS_NOT_FOUND, "El estudiante con ID: " + estudianteId));
            }
            
            List<Matricula> matriculas = matriculaRepository.findByEstudianteIdAndEstadoMatriculaId_Id(estudiante, 2);
            return matriculas.stream().map(matricula -> {
                MatriculaResponse matriculaResponse = new MatriculaResponse();
                BeanUtils.copyProperties(matricula, matriculaResponse);
                matriculaResponse.setEstadoMatriculaId(matricula.getEstadoMatriculaId().getId());
                matriculaResponse.setEstadoMatriculaNombre(matricula.getEstadoMatriculaId().getNombre());
                matriculaResponse.setGrupoId(matricula.getGrupoCohorteId().getGrupoId().getId());
                matriculaResponse.setGrupoNombre(matricula.getGrupoCohorteId().getGrupoId().getNombre());
                matriculaResponse.setNombreMateria(matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getNombre());
                matriculaResponse.setCodigoMateria(matricula.getGrupoCohorteId().getGrupoId().getMateriaId().getCodigo());
                matriculaResponse.setEstudianteId(matricula.getEstudianteId().getId());
                matriculaResponse.setEstudianteNombre(matricula.getEstudianteId().getNombre());
                matriculaResponse.setFechaMatriculacion(matricula.getFechaMatriculacion());

                return matriculaResponse;
            }).toList();
        }
        
        /**
         * Valida si el estudiante ya tiene una matrícula activa en el grupo
         * 
         * @param estudiante   El estudiante a validar
         * @param grupoCohorte El grupo cohorte a validar
         * @throws MatriculaException Si el estudiante ya tiene una matrícula activa en
         *                            el grupo
         */
        private void validarMatriculaExistente(Estudiante estudiante, GrupoCohorte grupoCohorte)
                        throws MatriculaException {
                // Buscar todas las matrículas del estudiante en este grupo
                List<Matricula> matriculas = matriculaRepository.findByEstudianteIdAndGrupoCohorteId(estudiante,
                                grupoCohorte);

                // Estados que consideramos como "matrícula activa o aprobada" (no puede volver
                // a
                // matricularse)
                List<Integer> estadosInvalidos = List.of(1, 2); // 1=Aprobado, 2=En curso

                // Verificar si existe alguna matrícula con estado no permitido
                boolean tieneMatriculaActiva = matriculas.stream()
                                .anyMatch(m -> estadosInvalidos.contains(m.getEstadoMatriculaId().getId()));

                if (tieneMatriculaActiva) {
                        throw new MatriculaException(
                                        String.format("El estudiante ya tiene una matrícula activa en este grupo")
                                                        .toLowerCase());
                }

        }

        private String calcularSemestre(Date fechaMatriculacion) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaMatriculacion);

                int mes = cal.get(Calendar.MONTH) + 1; // Enero = 0
                int anio = cal.get(Calendar.YEAR);

                return (mes <= 6 ? "Primer" : "Segundo") + " semestre de " + anio;
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
}

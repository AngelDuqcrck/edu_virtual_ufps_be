package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoEstudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Programa;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EmailExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstadoEstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.ProgramaNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.EstudianteOracleRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstadoEstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.PensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.ProgramaRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IEstudianteService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.EstudianteDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.EstudianteResponse;

@Service
public class EstudianteServiceImplementation implements IEstudianteService {

        public static final String IS_ALREADY_USE = "%s ya esta en uso";
        public static final String IS_NOT_FOUND = "%s no fue encontrado";
        public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
        public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
        public static final String IS_NOT_VALID = "%s no es valido";
        public static final String ARE_NOT_EQUALS = "%s no son iguales";
        public static final String IS_NOT_CORRECT = "%s no es correcta";

       

        @Autowired
        private PensumRepository pensumRepository;

        @Autowired
        private CohorteGrupoRepository cohorteGrupoRepository;

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private EstadoEstudianteRepository estadoEstudianteRepository;

        @Autowired
        private EstudianteRepository estudianteRepository;

        @Autowired
        private RolRepository rolRepository;

        @Autowired
        private ProgramaRepository programaRepository;

        @Override
        public EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO)
                        throws PensumNotFoundException, CohorteNotFoundException, EstadoEstudianteNotFoundException,
                        RoleNotFoundException {

                Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(estudianteDTO.getEmail());

                Usuario usuario;
                if (usuarioExistente.isPresent()) {
                        usuario = usuarioExistente.get();
                } else {

                        usuario = Usuario.builder()
                                        .nombreCompleto(estudianteDTO.getNombre() + " " + estudianteDTO.getNombre2() + " "
                                                        + estudianteDTO.getApellido() + " "
                                                        + estudianteDTO.getApellido2())
                                        .primerNombre(estudianteDTO.getNombre())
                                        .segundoNombre(estudianteDTO.getNombre2())
                                        .primerApellido(estudianteDTO.getApellido())
                                        .segundoApellido(estudianteDTO.getApellido2())
                                        .email(estudianteDTO.getEmail())
                                        .telefono(estudianteDTO.getTelefono())
                                        .cedula(estudianteDTO.getCedula())
                                        .codigo(estudianteDTO.getCodigo()) // Código único del estudiante
                                        .rolId(rolRepository.findById(1)
                                                        .orElseThrow(() -> new RoleNotFoundException(
                                                                        String.format(IS_NOT_FOUND, "EL ROL ESTUDIANTE")
                                                                                        .toLowerCase()))) // Asigna el
                                                                                                          // rol
                                                                                                          // de
                                                                                                          // estudiante
                                        .googleId(null) // Se llenará cuando inicie sesión con Google
                                        .fotoUrl(null) // Se llenará con la foto de Google si aplica
                                        .build();

                        usuario = usuarioRepository.save(usuario);
                }

                // Crear el estudiante y asignarle el usuario
                Estudiante estudiante = new Estudiante();
                BeanUtils.copyProperties(estudianteDTO, estudiante);

                Pensum pensum = pensumRepository.findById(estudianteDTO.getPensumId())
                                .orElseThrow(() -> new PensumNotFoundException(
                                                String.format(IS_NOT_FOUND,
                                                                "EL PENSUN CON ID " + estudianteDTO.getPensumId())
                                                                .toLowerCase()));

                CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(estudianteDTO.getCohorteId())
                                .orElseThrow(() -> new CohorteNotFoundException(String
                                                .format(IS_NOT_FOUND_F,
                                                                "LA COHORTE CON ID " + estudianteDTO.getCohorteId())
                                                .toLowerCase()));

                EstadoEstudiante estadoEstudiante = estadoEstudianteRepository.findById(1) // Estado en curso
                                .orElseThrow(() -> new EstadoEstudianteNotFoundException(String
                                                .format(IS_NOT_FOUND,
                                                                "EL ESTADO DEL ESTUDIANTE CON ID "
                                                                                + estudianteDTO.getEstadoEstudianteId())
                                                .toLowerCase()));

                Boolean estudiantePosgrado = pensum.getProgramaId().getEsPosgrado();
                estudiante.setProgramaId(pensum.getProgramaId());
                estudiante.setEsPosgrado(estudiantePosgrado);
                estudiante.setPensumId(pensum);
                estudiante.setCohorteId(cohorteGrupo);
                estudiante.setEstadoEstudianteId(estadoEstudiante);
                estudiante.setUsuarioId(usuario);
                estudiante.setMigrado(false);
                estudiante = estudianteRepository.save(estudiante);

                EstudianteDTO estudianteCreado = new EstudianteDTO();
                BeanUtils.copyProperties(estudiante, estudianteCreado);
                estudianteDTO.setId(estudiante.getId());
                estudianteDTO.setUsuarioId(usuario.getId());
                estudianteDTO.setCohorteId(estudiante.getCohorteId().getId());
                estudianteDTO.setPensumId(estudiante.getPensumId().getId());
                estudianteDTO.setEstadoEstudianteId(estudiante.getEstadoEstudianteId().getId());

                return estudianteCreado;
        }

        @Override
        public EstudianteDTO actualizarEstudiante(Integer id, EstudianteDTO estudianteDTO)
                        throws UserNotFoundException, PensumNotFoundException, CohorteNotFoundException,
                        EstadoEstudianteNotFoundException, EstudianteNotFoundException, EmailExistException {
                Estudiante estudiante = estudianteRepository.findById(id)
                                .orElseThrow(() -> new EstudianteNotFoundException(
                                                String.format(IS_NOT_FOUND, "EL ESTUDIANTE CON ID " + id)
                                                                .toLowerCase()));

                Usuario usuario = usuarioRepository.findById(estudiante.getUsuarioId().getId())
                                .orElseThrow(() -> new UserNotFoundException(
                                                String.format(IS_NOT_FOUND, " EL USUARIO ASOCIADO AL ESTUDIANTE")
                                                                .toLowerCase()));

                if (!estudiante.getEmail().equals(estudianteDTO.getEmail()) &&
                                usuarioRepository.existsByEmail(estudianteDTO.getEmail())) {
                        throw new UserNotFoundException(
                                        String.format(IS_NOT_FOUND, "EL USUARIO CON EMAIL " + estudianteDTO.getEmail())
                                                        .toLowerCase());
                }

                BeanUtils.copyProperties(estudianteDTO, estudiante, "id", "usuarioId", "estadoEstudianteId");

                Pensum pensum = pensumRepository.findById(estudianteDTO.getPensumId())
                                .orElseThrow(() -> new PensumNotFoundException(
                                                String.format(IS_NOT_FOUND,
                                                                "EL PENSUN CON ID " + estudianteDTO.getPensumId())
                                                                .toLowerCase()));

                CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(estudianteDTO.getCohorteId())
                                .orElseThrow(() -> new CohorteNotFoundException(String
                                                .format(IS_NOT_FOUND_F,
                                                                "LA COHORTE CON ID " + estudianteDTO.getCohorteId())
                                                .toLowerCase()));
                
                estudiante.setId(id);
                estudiante.setPensumId(pensum);
                estudiante.setProgramaId(pensum.getProgramaId());
                estudiante.setEsPosgrado(pensum.getProgramaId().getEsPosgrado());
                estudiante.setCohorteId(cohorteGrupo);

                usuario.setNombreCompleto(estudianteDTO.getNombre() + " " + estudianteDTO.getNombre2() + " "
                                + estudianteDTO.getApellido() + " " + estudianteDTO.getApellido2());
                usuario.setPrimerNombre(estudianteDTO.getNombre());
                usuario.setSegundoNombre(estudianteDTO.getNombre2());
                usuario.setPrimerApellido(estudianteDTO.getApellido());
                usuario.setSegundoApellido(estudianteDTO.getApellido2());
                usuario.setEmail(estudianteDTO.getEmail());
                usuario.setTelefono(estudianteDTO.getTelefono());
                usuario.setCedula(estudianteDTO.getCedula());
                usuario.setCodigo(estudianteDTO.getCodigo());

                usuarioRepository.save(usuario);
                estudiante = estudianteRepository.save(estudiante);

                EstudianteDTO estudianteActualizado = new EstudianteDTO();
                BeanUtils.copyProperties(estudiante, estudianteActualizado);
                estudianteActualizado.setUsuarioId(usuario.getId());
                estudianteActualizado.setCohorteId(estudiante.getCohorteId().getId());
                estudianteActualizado.setPensumId(estudiante.getPensumId().getId());
                estudianteActualizado.setEstadoEstudianteId(estudiante.getEstadoEstudianteId().getId());

                return estudianteActualizado;
        }

        @Override
        public EstudianteResponse listarEstudiante(Integer id) throws EstudianteNotFoundException {
                Estudiante estudiante = estudianteRepository.findById(id)
                                .orElseThrow(() -> new EstudianteNotFoundException(
                                                String.format(IS_NOT_FOUND, "EL ESTUDIANTE CON ID " + id)
                                                                .toLowerCase()));

                EstudianteResponse estudianteResponse = new EstudianteResponse();
                BeanUtils.copyProperties(estudiante, estudianteResponse);
                estudianteResponse.setUsuarioId(estudiante.getUsuarioId().getId());
                estudianteResponse.setCohorteId(estudiante.getCohorteId().getId());
                estudianteResponse.setCohorteNombre(estudiante.getCohorteId().getNombre());
                estudianteResponse.setPensumId(estudiante.getPensumId().getId());
                estudianteResponse.setPensumNombre(estudiante.getPensumId().getNombre());
                estudianteResponse.setProgramaId(estudiante.getProgramaId().getId());
                estudianteResponse.setProgramaNombre(estudiante.getProgramaId().getNombre());
                estudianteResponse.setEstadoEstudianteId(estudiante.getEstadoEstudianteId().getId());
                estudianteResponse.setEstadoEstudianteNombre(estudiante.getEstadoEstudianteId().getNombre());
                return estudianteResponse;
        }

        public List<EstudianteResponse> listarEstudiantes() {

                return estudianteRepository.findAll().stream().map(estudiante -> {
                        EstudianteResponse estudianteResponse = new EstudianteResponse();
                        BeanUtils.copyProperties(estudiante, estudianteResponse);
                        estudianteResponse.setUsuarioId(estudiante.getUsuarioId().getId());
                        estudianteResponse.setCohorteId(estudiante.getCohorteId().getId());
                        estudianteResponse.setCohorteNombre(estudiante.getCohorteId().getNombre());
                        estudianteResponse.setPensumId(estudiante.getPensumId().getId());
                        estudianteResponse.setPensumNombre(estudiante.getPensumId().getNombre());
                        estudianteResponse.setProgramaId(estudiante.getProgramaId().getId());
                        estudianteResponse.setProgramaNombre(estudiante.getProgramaId().getNombre());
                        estudianteResponse.setEstadoEstudianteId(estudiante.getEstadoEstudianteId().getId());
                        estudianteResponse.setEstadoEstudianteNombre(estudiante.getEstadoEstudianteId().getNombre());
                        return estudianteResponse;
                })
                                .collect(Collectors.toList());
        }

        @Override
        public List<EstudianteResponse> listarEstudiantesPorPensum(Integer pensumId) throws PensumNotFoundException {

                Pensum pensum = pensumRepository.findById(pensumId)
                                .orElseThrow(() -> new PensumNotFoundException(
                                                String.format(IS_NOT_FOUND, "EL PENSUM CON ID " + pensumId)
                                                                .toLowerCase()));

                List<Estudiante> estudiantes = estudianteRepository.findByPensumId(pensum);

                return estudiantes.stream().map(estudiante -> {
                        EstudianteResponse estudianteResponse = new EstudianteResponse();
                        BeanUtils.copyProperties(estudiante, estudianteResponse);
                        estudianteResponse.setUsuarioId(estudiante.getUsuarioId().getId());
                        estudianteResponse.setCohorteId(estudiante.getCohorteId().getId());
                        estudianteResponse.setCohorteNombre(estudiante.getCohorteId().getNombre());
                        estudianteResponse.setPensumId(estudiante.getPensumId().getId());
                        estudianteResponse.setPensumNombre(estudiante.getPensumId().getNombre());
                        estudianteResponse.setProgramaId(estudiante.getProgramaId().getId());
                        estudianteResponse.setProgramaNombre(estudiante.getProgramaId().getNombre());
                        estudianteResponse.setEstadoEstudianteId(estudiante.getEstadoEstudianteId().getId());
                        estudianteResponse.setEstadoEstudianteNombre(estudiante.getEstadoEstudianteId().getNombre());
                        return estudianteResponse;
                })
                                .collect(Collectors.toList());

        }

        @Override
        public List<EstudianteResponse> listarEstudiantesPorCohorte(Integer cohorteId) throws CohorteNotFoundException {

                CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(cohorteId)
                                .orElseThrow(() -> new CohorteNotFoundException(
                                                String.format(IS_NOT_FOUND_F, "LA COHORTE CON ID " + cohorteId)
                                                                .toLowerCase()));

                List<Estudiante> estudiantes = estudianteRepository.findByCohorteId(cohorteGrupo);

                return estudiantes.stream().map(estudiante -> {
                        EstudianteResponse estudianteResponse = new EstudianteResponse();
                        BeanUtils.copyProperties(estudiante, estudianteResponse);
                        estudianteResponse.setUsuarioId(estudiante.getUsuarioId().getId());
                        estudianteResponse.setCohorteId(estudiante.getCohorteId().getId());
                        estudianteResponse.setCohorteNombre(estudiante.getCohorteId().getNombre());
                        estudianteResponse.setPensumId(estudiante.getPensumId().getId());
                        estudianteResponse.setPensumNombre(estudiante.getPensumId().getNombre());
                        estudianteResponse.setProgramaId(estudiante.getProgramaId().getId());
                        estudianteResponse.setProgramaNombre(estudiante.getProgramaId().getNombre());
                        estudianteResponse.setEstadoEstudianteId(estudiante.getEstadoEstudianteId().getId());
                        estudianteResponse.setEstadoEstudianteNombre(estudiante.getEstadoEstudianteId().getNombre());
                        return estudianteResponse;
                })
                                .collect(Collectors.toList());
        }

        @Override
        public List<EstudianteResponse> listarEstudiantesPorPrograma(Integer programaId)
                        throws ProgramaNotFoundException {

                Programa programa = programaRepository.findById(programaId)
                                .orElseThrow(() -> new ProgramaNotFoundException(
                                                String.format(IS_NOT_FOUND, "EL PROGRAMA CON ID " + programaId)
                                                                .toLowerCase()));

                List<Estudiante> estudiantes = estudianteRepository.findByProgramaId(programa);

                return estudiantes.stream().map(estudiante -> {
                        EstudianteResponse estudianteResponse = new EstudianteResponse();
                        BeanUtils.copyProperties(estudiante, estudianteResponse);
                        estudianteResponse.setUsuarioId(estudiante.getUsuarioId().getId());
                        estudianteResponse.setCohorteId(estudiante.getCohorteId().getId());
                        estudianteResponse.setCohorteNombre(estudiante.getCohorteId().getNombre());
                        estudianteResponse.setPensumId(estudiante.getPensumId().getId());
                        estudianteResponse.setPensumNombre(estudiante.getPensumId().getNombre());
                        estudianteResponse.setProgramaId(estudiante.getProgramaId().getId());
                        estudianteResponse.setProgramaNombre(estudiante.getProgramaId().getNombre());
                        estudianteResponse.setEstadoEstudianteId(estudiante.getEstadoEstudianteId().getId());
                        estudianteResponse.setEstadoEstudianteNombre(estudiante.getEstadoEstudianteId().getNombre());
                        return estudianteResponse;
                })
                                .collect(Collectors.toList());
        }

        @Override
        public List<EstudianteResponse> listarEstudiantesPorEstado(Integer estadoEstudianteId)
                        throws EstadoEstudianteNotFoundException {

                EstadoEstudiante estadoEstudiante = estadoEstudianteRepository.findById(estadoEstudianteId)
                                .orElseThrow(() -> new EstadoEstudianteNotFoundException(
                                                String.format(IS_NOT_FOUND,
                                                                "EL ESTADO ESTUDIANTE CON ID " + estadoEstudianteId)
                                                                .toLowerCase()));

                List<Estudiante> estudiantes = estudianteRepository.findByEstadoEstudianteId(estadoEstudiante);

                return estudiantes.stream().map(estudiante -> {
                        EstudianteResponse estudianteResponse = new EstudianteResponse();
                        BeanUtils.copyProperties(estudiante, estudianteResponse);
                        estudianteResponse.setUsuarioId(estudiante.getUsuarioId().getId());
                        estudianteResponse.setCohorteId(estudiante.getCohorteId().getId());
                        estudianteResponse.setCohorteNombre(estudiante.getCohorteId().getNombre());
                        estudianteResponse.setPensumId(estudiante.getPensumId().getId());
                        estudianteResponse.setPensumNombre(estudiante.getPensumId().getNombre());
                        estudianteResponse.setProgramaId(estudiante.getProgramaId().getId());
                        estudianteResponse.setProgramaNombre(estudiante.getProgramaId().getNombre());
                        estudianteResponse.setEstadoEstudianteId(estudiante.getEstadoEstudianteId().getId());
                        estudianteResponse.setEstadoEstudianteNombre(estudiante.getEstadoEstudianteId().getNombre());
                        return estudianteResponse;
                })
                                .collect(Collectors.toList());
        }
}

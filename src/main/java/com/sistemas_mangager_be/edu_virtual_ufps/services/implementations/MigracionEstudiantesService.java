package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.*;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.entities.EstudianteOracle;
import com.sistemas_mangager_be.edu_virtual_ufps.oracle.repositories.EstudianteOracleRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.*;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.EstudianteMigradoDTO;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MigracionEstudiantesService {

    // private final EstudianteOracleRepository estudianteOracleRepository;
    // private final EstudianteRepository estudianteRepository;
    // private final ProgramaRepository programaRepository;
    // private final PensumRepository pensumRepository;
    // private final CohorteGrupoRepository cohorteGrupoRepository;
    // private final EstadoEstudianteRepository estadoEstudianteRepository;
    // private final UsuarioRepository usuarioRepository;
    // private final RolRepository rolRepository;

    // @Autowired
    // public MigracionEstudiantesService(EstudianteOracleRepository estudianteOracleRepository,
    //                                  EstudianteRepository estudianteRepository,
    //                                  ProgramaRepository programaRepository,
    //                                  PensumRepository pensumRepository,
    //                                  CohorteGrupoRepository cohorteGrupoRepository,
    //                                  EstadoEstudianteRepository estadoEstudianteRepository,
    //                                  UsuarioRepository usuarioRepository,
    //                                  RolRepository rolRepository) {
    //     this.estudianteOracleRepository = estudianteOracleRepository;
    //     this.estudianteRepository = estudianteRepository;
    //     this.programaRepository = programaRepository;
    //     this.pensumRepository = pensumRepository;
    //     this.cohorteGrupoRepository = cohorteGrupoRepository;
    //     this.estadoEstudianteRepository = estadoEstudianteRepository;
    //     this.usuarioRepository = usuarioRepository;
    //     this.rolRepository = rolRepository;
    // }

    
    // public void migrarEstudiantesTIC() {
    //     // 1. Obtener todos los estudiantes de la maestría en TIC desde Oracle
    //     List<EstudianteOracle> estudiantesOracle = estudianteOracleRepository.findByCodigoStartingWith("247");

    //     // 2. Obtener el programa de TIC en MySQL
    //     Programa programaTIC = programaRepository.findByCodigo("247")
    //             .orElseThrow(() -> new RuntimeException("Programa TIC no encontrado en MySQL"));

        

    //     // 5. Obtener estado por defecto (asumimos que existe uno con id=1)
    //     EstadoEstudiante estadoDefault = estadoEstudianteRepository.findById(1)
    //             .orElseThrow(() -> new RuntimeException("Estado de estudiante por defecto no encontrado"));

    //     // 6. Migrar cada estudiante
    //     for (EstudianteOracle estudianteOracle : estudiantesOracle) {
    //         // Verificar si el estudiante ya fue migrado
    //         Optional<Estudiante> estudianteExistente = estudianteRepository.findByCodigo(estudianteOracle.getCodigo());
    //         if (estudianteExistente.isPresent()) {
    //             continue; // Saltar si ya existe
    //         }

    //         // Crear DTO para el nuevo estudiante
    //         EstudianteMigradoDTO estudianteDTO = new EstudianteMigradoDTO();
    //         estudianteDTO.setCodigo(estudianteOracle.getCodigo());
    //         estudianteDTO.setNombre(estudianteOracle.getPrimerNombre());
    //         estudianteDTO.setNombre2(estudianteOracle.getSegundoNombre());
    //         estudianteDTO.setApellido(estudianteOracle.getPrimerApellido());
    //         estudianteDTO.setApellido2(estudianteOracle.getSegundoApellido());
    //         estudianteDTO.setEmail(estudianteOracle.getEmail());
    //         estudianteDTO.setCedula(estudianteOracle.getDocumento());
    //         estudianteDTO.setFechaNacimiento(new Date(estudianteOracle.getFechaNacimiento().getTime()));
    //         estudianteDTO.setFechaIngreso(new Date()); // Fecha actual como fecha de ingreso
    //         estudianteDTO.setPensumId(null);
    //         estudianteDTO.setCohorteId(null);
    //         estudianteDTO.setEstadoEstudianteId(estadoDefault.getId());
    //         estudianteDTO.setMigrado(true);

    //         // Verificar si el usuario ya existe
    //         Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(estudianteOracle.getEmail());
    //         Usuario usuario;

    //         if (usuarioExistente.isPresent()) {
    //             usuario = usuarioExistente.get();
    //         } else {
    //             // Crear nuevo usuario
    //             usuario = Usuario.builder()
    //                     .nombreCompleto(estudianteOracle.getPrimerNombre() + " " + 
    //                                    (estudianteOracle.getSegundoNombre() != null ? estudianteOracle.getSegundoNombre() + " " : "") + 
    //                                    estudianteOracle.getPrimerApellido() + " " + 
    //                                    (estudianteOracle.getSegundoApellido() != null ? estudianteOracle.getSegundoApellido() : ""))
    //                     .primerNombre(estudianteOracle.getPrimerNombre())
    //                     .segundoNombre(estudianteOracle.getSegundoNombre())
    //                     .primerApellido(estudianteOracle.getPrimerApellido())
    //                     .segundoApellido(estudianteOracle.getSegundoApellido())
    //                     .email(estudianteOracle.getEmail())
    //                     .cedula(estudianteOracle.getDocumento())
    //                     .codigo(estudianteOracle.getCodigo())
    //                     .rolId(rolRepository.findById(1).orElseThrow(() -> new RuntimeException("Rol Estudiante no encontrado")))
    //                     .build();

    //             usuario = usuarioRepository.save(usuario);
    //         }

    //         // Crear el estudiante en MySQL
    //         Estudiante estudiante = new Estudiante();
    //         BeanUtils.copyProperties(estudianteDTO, estudiante);
    //         estudiante.setPensumId(null);
    //         estudiante.setProgramaId(programaTIC);
    //         estudiante.setCohorteId(null);
    //         estudiante.setEstadoEstudianteId(estadoDefault);
    //         estudiante.setUsuarioId(usuario);
    //         estudiante.setEsPosgrado(true);
    //         estudiante.setMigrado(true);

    //         estudianteRepository.save(estudiante);
    //     }
    // }
}
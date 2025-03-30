package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import java.util.Optional;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.CohorteGrupo;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.EstadoEstudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Estudiante;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Pensum;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.CohorteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.EstadoEstudianteNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.PensumNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.CohorteGrupoRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstadoEstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.EstudianteRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.PensumRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IEstudianteService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.EstudianteDTO;

@Service
public class EstudianteServiceImplementation implements IEstudianteService{
    
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

    @Override
    public EstudianteDTO crearEstudiante(EstudianteDTO estudianteDTO) 
        throws PensumNotFoundException, CohorteNotFoundException, EstadoEstudianteNotFoundException, RoleNotFoundException {

    
    Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(estudianteDTO.getEmail());

    Usuario usuario;
    if (usuarioExistente.isPresent()) {
        usuario = usuarioExistente.get();
    } else {
        
        usuario = Usuario.builder()
                .nombre(estudianteDTO.getNombre() + " " + estudianteDTO.getApellido())
                .email(estudianteDTO.getEmail())
                .telefono(estudianteDTO.getTelefono())
                .cedula(estudianteDTO.getCedula())
                .codigo(estudianteDTO.getCodigo()) // Código único del estudiante
                .rolId(rolRepository.findById(1).orElseThrow(() -> new RoleNotFoundException(String.format(IS_NOT_FOUND, "EL ROL ESTUDIANTE").toLowerCase()))) // Asigna el rol de estudiante
                .googleId(null) // Se llenará cuando inicie sesión con Google
                .fotoUrl(null) // Se llenará con la foto de Google si aplica
                .build();

        usuario = usuarioRepository.save(usuario);
    }

    // Crear el estudiante y asignarle el usuario
    Estudiante estudiante = new Estudiante();
    BeanUtils.copyProperties(estudianteDTO, estudiante);

    Pensum pensum = pensumRepository.findById(estudianteDTO.getPensumId())
            .orElseThrow(() -> new PensumNotFoundException(String.format(IS_NOT_FOUND, "EL PENSUN CON ID " + estudianteDTO.getPensumId()).toLowerCase()));

    CohorteGrupo cohorteGrupo = cohorteGrupoRepository.findById(estudianteDTO.getCohorteId())
            .orElseThrow(() -> new CohorteNotFoundException(String.format(IS_NOT_FOUND_F, "LA COHORTE CON ID " + estudianteDTO.getCohorteId()).toLowerCase()));

    EstadoEstudiante estadoEstudiante = estadoEstudianteRepository.findById(1) //Estado en curso
            .orElseThrow(() -> new EstadoEstudianteNotFoundException(String.format(IS_NOT_FOUND, "EL ESTADO DEL ESTUDIANTE CON ID " + estudianteDTO.getEstadoEstudianteId()).toLowerCase()));

    Boolean estudiantePosgrado = pensum.getProgramaId().getEsPosgrado();
    estudiante.setProgramaId(pensum.getProgramaId());
    estudiante.setEsPosgrado(estudiantePosgrado);
    estudiante.setPensumId(pensum);
    estudiante.setCohorteId(cohorteGrupo);
    estudiante.setEstadoEstudianteId(estadoEstudiante);
    estudiante.setUsuarioId(usuario);

    
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
        


        



}

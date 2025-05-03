package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Rol;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
// import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IUsuarioService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.UsuarioDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.DocenteRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.LoginGoogleRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.UsuarioResponse;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImplementation implements IUsuarioService {

    public static final String IS_ALREADY_USE = "%s ya esta registrado en el sistema";
    public static final String IS_NOT_FOUND = "%s no fue encontrado";
    public static final String IS_NOT_FOUND_F = "%s no fue encontrada";
    public static final String IS_NOT_ALLOWED = "no esta permitido %s ";
    public static final String IS_NOT_VALID = "%s no es valido";
    public static final String ARE_NOT_EQUALS = "%s no son iguales";
    public static final String IS_NOT_CORRECT = "%s no es correcta";

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UsuarioDTO crearProfesor(DocenteRequest docenteRequest) throws RoleNotFoundException, UserExistException {
        // Validar email único
        if (usuarioRepository.existsByEmail(docenteRequest.getEmail())) {
            throw new UserExistException(
                    String.format(IS_ALREADY_USE, "El correo electrónico " + docenteRequest.getEmail()));
        }

        // Validar código único (si no es nulo)
        if (docenteRequest.getCodigo() != null && !docenteRequest.getCodigo().isEmpty() &&
                usuarioRepository.existsByCodigo(docenteRequest.getCodigo())) {
            throw new UserExistException(String.format(IS_ALREADY_USE, "El código " + docenteRequest.getCodigo()));
        }

        // Validar cédula única (si no es nula)
        if (docenteRequest.getCedula() != null && !docenteRequest.getCedula().isEmpty() &&
                usuarioRepository.existsByCedula(docenteRequest.getCedula())) {
            throw new UserExistException(String.format(IS_ALREADY_USE, "La cédula " + docenteRequest.getCedula()));
        }

        // Validar googleId único (si no es nulo)
        if (docenteRequest.getGoogleId() != null && !docenteRequest.getGoogleId().isEmpty() &&
                usuarioRepository.existsByGoogleId(docenteRequest.getGoogleId())) {
            throw new UserExistException(
                    String.format(IS_ALREADY_USE, "El ID de Google " + docenteRequest.getGoogleId()));
        }

        Usuario docente = new Usuario();
        docente.setCodigo(docenteRequest.getCodigo());
        docente.setNombreCompleto(docenteRequest.getPrimerNombre() + " " + docenteRequest.getSegundoNombre() + " " +
                docenteRequest.getPrimerApellido() + " " + docenteRequest.getSegundoApellido());
        BeanUtils.copyProperties(docenteRequest, docente);

        // Asignar rol docente (2 es el ID del rol docente)
        Rol rolDocente = rolRepository.findById(2)
                .orElseThrow(
                        () -> new RoleNotFoundException(String.format(IS_NOT_FOUND, "EL ROL DOCENTE").toLowerCase()));
        docente.setRolId(rolDocente);

        // Guardar el nuevo docente
        usuarioRepository.save(docente);

        return convertirAUsuarioDTO(docente);
    }

    public void registraroActualizarUsuarioGoogle(LoginGoogleRequest loginGoogleRequest) throws UserExistException {
        // Validar googleId único si el usuario no existe por email
        if (!usuarioRepository.existsByEmail(loginGoogleRequest.getEmail()) && 
                loginGoogleRequest.getGoogleId() != null && 
                !loginGoogleRequest.getGoogleId().isEmpty() && 
                usuarioRepository.existsByGoogleId(loginGoogleRequest.getGoogleId())) {
            throw new UserExistException(String.format(IS_ALREADY_USE, "El ID de Google " + loginGoogleRequest.getGoogleId()));
        }
        
        usuarioRepository.findByEmail(loginGoogleRequest.getEmail()).ifPresentOrElse(
                usuario -> {
                    // Actualización de usuario existente
                    if (loginGoogleRequest.getGoogleId() != null && !loginGoogleRequest.getGoogleId().isEmpty() &&
                            !loginGoogleRequest.getGoogleId().equals(usuario.getGoogleId()) && 
                            usuarioRepository.existsByGoogleId(loginGoogleRequest.getGoogleId())) {
                        throw new RuntimeException("El ID de Google ya está en uso por otro usuario");
                    }
                    
                    usuario.setGoogleId(loginGoogleRequest.getGoogleId());
                    usuario.setNombreCompleto(loginGoogleRequest.getNombre().isEmpty() ? usuario.getNombreCompleto()
                            : loginGoogleRequest.getNombre());
                    usuario.setFotoUrl(loginGoogleRequest.getFotoUrl() == null ? usuario.getFotoUrl()
                            : loginGoogleRequest.getFotoUrl());
                    
                    // Si es un estudiante (rol por defecto) pero ya estaba registrado como docente,
                    // mantener rol
                    if (usuario.getRolId() == null || usuario.getRolId().getId() == 1) {
                        Rol rolEstudiante = rolRepository.findById(1).orElseThrow();
                        usuario.setRolId(rolEstudiante);
                    }
                    
                    usuarioRepository.save(usuario);
                },
                () -> {
                    // Creación de nuevo usuario (estudiante por defecto)
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setEmail(loginGoogleRequest.getEmail());
                    nuevoUsuario.setGoogleId(loginGoogleRequest.getGoogleId());
                    nuevoUsuario.setNombreCompleto(loginGoogleRequest.getNombre());
                    nuevoUsuario.setFotoUrl(loginGoogleRequest.getFotoUrl());
                    
                    Rol rolEstudiante = rolRepository.findById(1).orElseThrow();
                    nuevoUsuario.setRolId(rolEstudiante);
                    
                    usuarioRepository.save(nuevoUsuario);
                });
    }

    @Override
    @Transactional
    public void guardarOActualizarUsuario(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");
        String nombre = oAuth2User.getAttribute("name");
        String fotoUrl = oAuth2User.getAttribute("picture");

        usuarioRepository.findByEmail(email).ifPresentOrElse(
                usuario -> {
                    // Actualización de usuario existente
                    usuario.setGoogleId(googleId);
                    usuario.setNombreCompleto(nombre.isEmpty() ? usuario.getNombreCompleto() : nombre);
                    usuario.setFotoUrl(fotoUrl == null ? usuario.getFotoUrl() : fotoUrl);

                    // Si es un estudiante (rol por defecto) pero ya estaba registrado como docente,
                    // mantener rol
                    if (usuario.getRolId() == null || usuario.getRolId().getId() == 1) {
                        Rol rolEstudiante = rolRepository.findById(1).orElseThrow();
                        usuario.setRolId(rolEstudiante);
                    }

                    usuarioRepository.save(usuario);
                },
                () -> {
                    // Creación de nuevo usuario (estudiante por defecto)
                    Usuario nuevoUsuario = new Usuario();
                    nuevoUsuario.setEmail(email);
                    nuevoUsuario.setGoogleId(googleId);
                    nuevoUsuario.setNombreCompleto(nombre);
                    nuevoUsuario.setFotoUrl(fotoUrl);

                    Rol rolEstudiante = rolRepository.findById(1).orElseThrow();
                    nuevoUsuario.setRolId(rolEstudiante);

                    usuarioRepository.save(nuevoUsuario);
                });
    }

    public UsuarioDTO actualizarProfesor(DocenteRequest docenteRequest, Integer id)
            throws RoleNotFoundException, UserExistException, UserNotFoundException {

        Usuario profesor = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(IS_NOT_FOUND, "EL PROFESOR CON ID " + id).toLowerCase()));

        if (profesor.getRolId().getId() != 2) {
            throw new UserNotFoundException("El usuario no tiene rol de profesor");
        }

        // Validar email único (si cambió)
        if (!profesor.getEmail().equals(docenteRequest.getEmail()) &&
                usuarioRepository.existsByEmail(docenteRequest.getEmail())) {
            throw new UserExistException(
                    String.format(IS_ALREADY_USE, "El correo electrónico " + docenteRequest.getEmail()));
        }

        // Validar código único (si cambió y no es nulo)
        if (docenteRequest.getCodigo() != null && !docenteRequest.getCodigo().isEmpty() &&
                !docenteRequest.getCodigo().equals(profesor.getCodigo()) &&
                usuarioRepository.existsByCodigo(docenteRequest.getCodigo())) {
            throw new UserExistException(String.format(IS_ALREADY_USE, "El código " + docenteRequest.getCodigo()));
        }

        // Validar cédula única (si cambió y no es nula)
        if (docenteRequest.getCedula() != null && !docenteRequest.getCedula().isEmpty() &&
                !docenteRequest.getCedula().equals(profesor.getCedula()) &&
                usuarioRepository.existsByCedula(docenteRequest.getCedula())) {
            throw new UserExistException(String.format(IS_ALREADY_USE, "La cédula " + docenteRequest.getCedula()));
        }

        // Validar googleId único (si cambió y no es nulo)
        if (docenteRequest.getGoogleId() != null && !docenteRequest.getGoogleId().isEmpty() &&
                !docenteRequest.getGoogleId().equals(profesor.getGoogleId()) &&
                usuarioRepository.existsByGoogleId(docenteRequest.getGoogleId())) {
            throw new UserExistException(
                    String.format(IS_ALREADY_USE, "El ID de Google " + docenteRequest.getGoogleId()));
        }

        profesor.setNombreCompleto(docenteRequest.getPrimerNombre() + " " + docenteRequest.getSegundoNombre() + " " +
                docenteRequest.getPrimerApellido() + " " + docenteRequest.getSegundoApellido());
        profesor.setPrimerNombre(docenteRequest.getPrimerNombre());
        profesor.setSegundoNombre(docenteRequest.getSegundoNombre());
        profesor.setPrimerApellido(docenteRequest.getPrimerApellido());
        profesor.setSegundoApellido(docenteRequest.getSegundoApellido());
        profesor.setEmail(docenteRequest.getEmail());
        profesor.setTelefono(docenteRequest.getTelefono());
        profesor.setCedula(docenteRequest.getCedula());
        profesor.setCodigo(docenteRequest.getCodigo());

        Rol rolDocente = rolRepository.findById(2)
                .orElseThrow(
                        () -> new RoleNotFoundException(String.format(IS_NOT_FOUND, "EL ROL DOCENTE").toLowerCase()));
        profesor.setRolId(rolDocente);

        usuarioRepository.save(profesor);

        return convertirAUsuarioDTO(profesor);
    }

    @Override
    public UsuarioResponse listarUsuario(Integer id) throws UserNotFoundException {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format(IS_NOT_FOUND, "EL USUARIO CON ID " + id).toLowerCase()));

        UsuarioResponse usuarioResponse = new UsuarioResponse();
        BeanUtils.copyProperties(usuario, usuarioResponse);
        usuarioResponse.setRol(usuario.getRolId().getNombre());
        return usuarioResponse;
    }

    @Override
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream().map(usuario -> {
            UsuarioResponse usuarioResponse = new UsuarioResponse();
            BeanUtils.copyProperties(usuario, usuarioResponse);
            usuarioResponse.setRol(usuario.getRolId().getNombre());
            return usuarioResponse;
        }).collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponse> listarUsuariosPorRol(Integer rolId) throws RoleNotFoundException {
        rolRepository.findById(rolId)
                .orElseThrow(() -> new RoleNotFoundException(
                        String.format(IS_NOT_FOUND, "EL ROL CON ID " + rolId).toLowerCase()));

        return usuarioRepository.findAll().stream().filter(usuario -> usuario.getRolId().getId() == rolId)
                .map(usuario -> {
                    UsuarioResponse usuarioResponse = new UsuarioResponse();
                    BeanUtils.copyProperties(usuario, usuarioResponse);
                    usuarioResponse.setRol(usuario.getRolId().getNombre());
                    return usuarioResponse;
                }).collect(Collectors.toList());
    }

    private UsuarioDTO actualizarEstudianteADocente(Usuario usuario, DocenteRequest docenteRequest) {
        // Actualizar datos básicos
        usuario.setNombreCompleto(docenteRequest.getNombre());
        usuario.setTelefono(docenteRequest.getTelefono());
        usuario.setCedula(docenteRequest.getCedula());
        usuario.setCodigo(docenteRequest.getCodigo());

        // Asignar rol docente
        Rol rolDocente = rolRepository.findById(2).orElseThrow();
        usuario.setRolId(rolDocente);

        usuarioRepository.save(usuario);
        return convertirAUsuarioDTO(usuario);
    }

    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        BeanUtils.copyProperties(usuario, dto);
        dto.setRolId(usuario.getRolId().getId());
        return dto;
    }
}

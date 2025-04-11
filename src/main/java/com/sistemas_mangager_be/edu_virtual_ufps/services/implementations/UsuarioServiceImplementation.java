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

    public static final String IS_ALREADY_USE = "%s ya esta en uso";
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
        // 1. Verificar si el usuario ya existe por email
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(docenteRequest.getEmail());

        if (usuarioExistente.isPresent()) {
            Usuario usuario = usuarioExistente.get();

            // 2. Si existe pero es un estudiante (rol por defecto), actualizarlo a docente
            if (usuario.getRolId().getId() == 1) { // 1 es el ID del rol estudiante
                return actualizarEstudianteADocente(usuario, docenteRequest);
            }
            // 3. Si ya es docente, lanzar excepción
            throw new UserExistException(String.format(IS_ALREADY_USE, "ESTE CORREO DE DOCENTE").toLowerCase());
        }

        Usuario docente = new Usuario();
        docente.setCodigo(docenteRequest.getCodigo());
        BeanUtils.copyProperties(docenteRequest, docente);

        // 5. Asignar rol docente (2 es el ID del rol docente)
        Rol rolDocente = rolRepository.findById(2)
                .orElseThrow(
                        () -> new RoleNotFoundException(String.format(IS_NOT_FOUND, "EL ROL DOCENTE").toLowerCase()));
        docente.setRolId(rolDocente);

        // 6. Guardar el nuevo docente
        usuarioRepository.save(docente);

        return convertirAUsuarioDTO(docente);
    }

    public void registraroActualizarUsuarioGoogle(LoginGoogleRequest loginGoogleRequest) {
        usuarioRepository.findByEmail(loginGoogleRequest.getEmail()).ifPresentOrElse(
                usuario -> {
                    // Actualización de usuario existente
                    usuario.setGoogleId(loginGoogleRequest.getGoogleId());
                    usuario.setNombre(loginGoogleRequest.getNombre().isEmpty() ? usuario.getNombre() : loginGoogleRequest.getNombre());
                    usuario.setFotoUrl(loginGoogleRequest.getFotoUrl() == null ? usuario.getFotoUrl() : loginGoogleRequest.getFotoUrl());

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
                    nuevoUsuario.setNombre(loginGoogleRequest.getNombre()); // si no se le pasa, se usa el nombre del usuario
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
                    usuario.setNombre(nombre.isEmpty() ? usuario.getNombre() : nombre);
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
                    nuevoUsuario.setNombre(nombre);
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

        if (!profesor.getEmail().equals(docenteRequest.getEmail()) &&
                usuarioRepository.existsByEmail(docenteRequest.getEmail())) {
            throw new UserExistException(String.format(IS_ALREADY_USE, "ESTE CORREO").toLowerCase());
        }

        profesor.setNombre(docenteRequest.getNombre());
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
        usuario.setNombre(docenteRequest.getNombre());
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

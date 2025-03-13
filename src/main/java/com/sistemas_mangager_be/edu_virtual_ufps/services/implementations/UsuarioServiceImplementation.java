package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;


import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Rol;
import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IUsuarioService;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.UsuarioDTO;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) throws RoleNotFoundException, UserNotFoundException {
        Usuario usuario = new Usuario();
        if(usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new UserNotFoundException(String.format(IS_ALREADY_USE, usuarioDTO.getEmail()));
        }
        Rol rol = rolRepository.findById(usuarioDTO.getRolId()).orElse(null);


        if (rol == null) {
            throw new RoleNotFoundException(String.format(IS_NOT_FOUND, "EL ROL"). toLowerCase());
        }

        BeanUtils.copyProperties(usuarioDTO, usuario);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRolId(rol);
        usuarioRepository.save(usuario);
        UsuarioDTO usuarioResponse = UsuarioDTO.builder()
                .primerNombre(usuario.getPrimerNombre())
                .segundoNombre(usuario.getSegundoNombre())
                .primerApellido(usuario.getPrimerApellido())
                .segundoApellido(usuario.getSegundoApellido())
                .email(usuario.getEmail())
                .password(usuario.getPassword())
                .rolId(usuario.getRolId().getId())
                .build();

        return usuarioResponse;
        
    }

}

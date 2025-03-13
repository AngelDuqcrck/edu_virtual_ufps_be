package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;


import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.UsuarioDTO;

public interface IUsuarioService {

    public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) throws RoleNotFoundException, UserNotFoundException;
} 
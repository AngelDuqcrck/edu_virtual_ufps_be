package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;


import org.springframework.security.oauth2.core.user.OAuth2User;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.UsuarioDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.DocenteRequest;



// import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
// import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
// import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.UsuarioDTO;

public interface IUsuarioService {

    //public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) throws RoleNotFoundException, UserNotFoundException;

    public void guardarOActualizarUsuario(OAuth2User oAuth2User);

    public UsuarioDTO crearProfesor(DocenteRequest docenteRequest) throws RoleNotFoundException, UserExistException;
} 
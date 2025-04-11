package com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces;


import java.util.List;

import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.LoginGoogleRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserExistException;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.UsuarioDTO;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.requests.DocenteRequest;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.UsuarioResponse;



// import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
// import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.UserNotFoundException;
// import com.sistemas_mangager_be.edu_virtual_ufps.shared.DTOs.UsuarioDTO;

public interface IUsuarioService {

    //public UsuarioDTO registrarUsuario(UsuarioDTO usuarioDTO) throws RoleNotFoundException, UserNotFoundException;

    public void registraroActualizarUsuarioGoogle(LoginGoogleRequest loginGoogleRequest);

    public void guardarOActualizarUsuario(OAuth2User oAuth2User);

    public UsuarioDTO crearProfesor(DocenteRequest docenteRequest) throws RoleNotFoundException, UserExistException;

    public UsuarioDTO actualizarProfesor(DocenteRequest docenteRequest, Integer id)
            throws RoleNotFoundException, UserExistException, UserNotFoundException;

    public List<UsuarioResponse> listarUsuariosPorRol(Integer rolId) throws RoleNotFoundException;

    public UsuarioResponse listarUsuario(Integer id) throws UserNotFoundException;

    public List<UsuarioResponse> listarUsuarios();
} 
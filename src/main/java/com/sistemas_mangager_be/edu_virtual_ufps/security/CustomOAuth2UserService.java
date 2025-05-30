package com.sistemas_mangager_be.edu_virtual_ufps.security;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IUsuarioService;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Autowired
    private IUsuarioService iUsuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String email = oauth2User.getAttribute("email");

        if (email == null) {
            throw new OAuth2AuthenticationException("El proveedor de OAuth2 no proporcionó un email válido.");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        if (usuarioOptional.isEmpty()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("unauthorized_user"),
                    "El usuario con email " + email + " no existe en el sistema.");
        }

        iUsuarioService.guardarOActualizarUsuario(oauth2User);

        return oauth2User;
    }
    
}

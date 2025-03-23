package com.sistemas_mangager_be.edu_virtual_ufps.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.services.interfaces.IUsuarioService;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Autowired
    private IUsuarioService iUsuarioService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Guardar o actualizar la información del usuario en la base de datos
        iUsuarioService.guardarOActualizarUsuario(oauth2User);

        return oauth2User;
    }
    
}

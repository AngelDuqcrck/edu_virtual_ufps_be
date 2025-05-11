package com.sistemas_mangager_be.edu_virtual_ufps.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Admin;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.InactiveAccountException;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.AdminRepository;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        // Primero verificar si el usuario existe
        Admin admin = adminRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        // Luego verificar si la cuenta está activa
        if (admin.getActivo() == null || !admin.getActivo()) {
            throw new InactiveAccountException("La cuenta está inactiva. Contacte al administrador.");
        }
        
        // Finalmente verificar la contraseña
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }
        
        // Si todo está bien, cargar los detalles del usuario
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        return new UsernamePasswordAuthenticationToken(
                userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
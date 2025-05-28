package com.sistemas_mangager_be.edu_virtual_ufps.security;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

import com.sistemas_mangager_be.edu_virtual_ufps.security.CustomUserDetailsService.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public class JwtTokenGenerator {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Metodo para generar el token por medio de la autenticación
    public String generarToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String correo = userDetails.getUsername();
        String rol = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        Date fechaActual = new Date();
        Date expiracionToken = new Date(fechaActual.getTime() + SecurityConstants.JWT_EXPIRATION_TIME_TOKEN);

        // Aqui generamos el token con la información adicional
        String token = Jwts.builder()
                .setSubject(correo)
                .claim("role", rol)
                .claim("primerNombre", userDetails.getPrimerNombre())
                .claim("primerApellido", userDetails.getPrimerApellido())
                .setIssuedAt(new Date())
                .setExpiration(expiracionToken)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_FIRMA)
                .compact();

        // Registrar sesión activa
        sessionManager.registerUserSession(correo, token);

        return token;
    }

    public String generarTokenGlobal(Authentication authentication) {
        Date fechaActual = new Date();
        Date expiracionToken = new Date(fechaActual.getTime() + SecurityConstants.JWT_EXPIRATION_TIME_TOKEN);

        String email;
        String nombre = "";
        String apellido = "";
        String rol = "";

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetailsService.CustomUserDetails customUser) {
            email = customUser.getUsername();
            nombre = customUser.getPrimerNombre();
            apellido = customUser.getPrimerApellido();
            rol = authentication.getAuthorities().stream().findFirst().get().getAuthority();
        } else if (principal instanceof OAuth2User oauthUser) {
            email = oauthUser.getAttribute("email");
            nombre = oauthUser.getAttribute("given_name");
            apellido = oauthUser.getAttribute("family_name");

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (usuarioOpt.isPresent()) {
                rol = usuarioOpt.get().getRolId().getNombre();
            } else {
                rol = "";
            }
        } else {
            throw new RuntimeException("Tipo de usuario no soportado: " + principal.getClass().getName());
        }

        Map<String, Object> claims = new HashMap<>();
        //claims.put("email", email);
        claims.put("nombre", nombre);
        claims.put("apellido", apellido);
        claims.put("rol", rol);

        String token = Jwts.builder()
                .setSubject(email)
                .claim("role", rol)
                .claim("nombre", nombre)
                .claim("apellido", apellido)
                .setIssuedAt(new Date())
                .setExpiration(expiracionToken)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_FIRMA)
                .compact();

        // Registrar sesión activa
        sessionManager.registerUserSession(email, token);

        return token;
    }

    // Método para generar un Refresh Token
    public String generarRefreshToken(Authentication authentication) {
        String correo = authentication.getName();
        Date tiempoActual = new Date();
        Date expiracionRefreshToken = new Date(
                tiempoActual.getTime() + SecurityConstants.JWT_EXPIRATION_TIME_REFRESH_TOKEN);

        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(tiempoActual)
                .setExpiration(expiracionRefreshToken)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_FIRMA)
                .compact();
    }

    // Metodo para extaer un email a partir de un token
    public String obtenerCorreoDeJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SecurityConstants.JWT_FIRMA)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Metodo para validar el token
    public Boolean validarToken(String token) {
        try {
            Jwts.parser().setSigningKey(SecurityConstants.JWT_FIRMA).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("JWT ha expirado o es incorrecto");
        }
    }

    // Validar si el Refresh Token ha expirado
    public Boolean validarRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SecurityConstants.JWT_FIRMA).parseClaimsJws(token).getBody();
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false; // Refresh Token inválido o expirado
        }
    }

    public Boolean validarTokenRecuperacion(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SecurityConstants.JWT_FIRMA)
                    .parseClaimsJws(token)
                    .getBody();

            // Verificamos si el token ha expirado
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false; // Token inválido o expirado
        }
    }

    // Método para generar un token único de recuperación de password
    public String generarTokenRecuperacion(String email) {
        Date tiempoActual = new Date();
        Date expiracionToken = new Date(
                tiempoActual.getTime() + SecurityConstants.JWT_EXPIRATION_TIME_PASSWORD_RESET);

        // Generamos el token basado en el correo del usuario
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiracionToken)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.JWT_FIRMA)
                .compact();
    }
}
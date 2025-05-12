package com.sistemas_mangager_be.edu_virtual_ufps.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.InactiveAccountException;
import com.sistemas_mangager_be.edu_virtual_ufps.shared.responses.HttpResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAutheticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, 
                        AuthenticationException authException) throws IOException, ServletException {
        
        HttpResponse errorResponse;
        HttpStatus httpStatus;
        
        // Determinar el tipo de error y configurar la respuesta adecuada
        if (authException instanceof InactiveAccountException) {
            httpStatus = HttpStatus.FORBIDDEN; // 403
            errorResponse = new HttpResponse(httpStatus.value(), httpStatus, 
                                        httpStatus.getReasonPhrase(), 
                                        "La cuenta está inactiva. Contacte al administrador.");
        } 
        else if (authException instanceof DisabledException) {
            httpStatus = HttpStatus.FORBIDDEN; // 403
            errorResponse = new HttpResponse(httpStatus.value(), httpStatus, 
                                        httpStatus.getReasonPhrase(), 
                                        "La cuenta está deshabilitada. Contacte al administrador.");
        }
        else if (authException instanceof BadCredentialsException) {
            httpStatus = HttpStatus.UNAUTHORIZED; // 401
            errorResponse = new HttpResponse(httpStatus.value(), httpStatus, 
                                        httpStatus.getReasonPhrase(), 
                                        "Credenciales incorrectas.");
        }
        else if (authException instanceof UsernameNotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND; // 404
            errorResponse = new HttpResponse(httpStatus.value(), httpStatus, 
                                        httpStatus.getReasonPhrase(), 
                                        "Usuario no encontrado.");
        }
        else if (authException instanceof InsufficientAuthenticationException) {
            httpStatus = HttpStatus.UNAUTHORIZED; // 401
            errorResponse = new HttpResponse(httpStatus.value(), httpStatus, 
                                        httpStatus.getReasonPhrase(), 
                                        "Se requiere autenticación para acceder a este recurso.");
        }
        else {
            // Caso por defecto
            httpStatus = HttpStatus.UNAUTHORIZED; // 401
            errorResponse = new HttpResponse(httpStatus.value(), httpStatus, 
                                        httpStatus.getReasonPhrase(), 
                                        authException.getMessage());
        }
        
        // Configurar la respuesta HTTP
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // Escribir el objeto de respuesta como JSON en el cuerpo de la respuesta
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
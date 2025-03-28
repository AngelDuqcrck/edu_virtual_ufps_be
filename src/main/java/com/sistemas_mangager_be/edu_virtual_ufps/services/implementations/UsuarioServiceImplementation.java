package com.sistemas_mangager_be.edu_virtual_ufps.services.implementations;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.exceptions.RoleNotFoundException;
// import com.sistemas_mangager_be.edu_virtual_ufps.repositories.RolRepository;
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

    // @Autowired
    // private RolRepository rolRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public UsuarioDTO crearProfesor(UsuarioDTO usuarioDTO) throws RoleNotFoundException {
        return null;
    }

    public void guardarOActualizarUsuario(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub"); // ID único de Google
        String nombre = oAuth2User.getAttribute("name");
        String fotoUrl = oAuth2User.getAttribute("picture");

        // Buscar si el usuario ya existe en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElse(new Usuario()); // Si no existe, crear uno nuevo

        // Actualizar o establecer los atributos del usuario
        usuario.setEmail(email);
        usuario.setGoogleId(googleId);
        usuario.setNombre(nombre);
        usuario.setFotoUrl(fotoUrl);

        // Guardar o actualizar el usuario en la base de datos
        usuarioRepository.save(usuario);
    }
}

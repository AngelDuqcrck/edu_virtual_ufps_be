package com.sistemas_mangager_be.edu_virtual_ufps.security;

import org.springframework.stereotype.Service;

import com.sistemas_mangager_be.edu_virtual_ufps.entities.Usuario;
import com.sistemas_mangager_be.edu_virtual_ufps.repositories.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("Sesión no válida o usuario no encontrado"));

        // Nos traemos la lista de autoridades a traves de la lista de roles
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(usuario.getRolId().getNombre()));

        // Creamos un CustomUserDetails que extiende User para incluir información adicional
        return new CustomUserDetails(
            usuario.getEmail(),
            usuario.getPassword(),
            true,
            true,
            true,
            true,
            authorities,
            usuario.getPrimerNombre(),
            usuario.getPrimerApellido()
        ); 
    }

    // Clase personalizada para extender UserDetails y incluir información adicional
    public class CustomUserDetails extends User {
        private final String primerNombre;
        private final String primerApellido;

        public CustomUserDetails(String username, String password, boolean enabled,
                            boolean accountNonExpired, boolean credentialsNonExpired,
                            boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
                            String primerNombre, String primerApellido) {
            super(username, password, enabled, accountNonExpired, 
                credentialsNonExpired, accountNonLocked, authorities);
            this.primerNombre = primerNombre;
            this.primerApellido = primerApellido;
        }

        public String getPrimerNombre() {
            return primerNombre;
        }

        public String getPrimerApellido() {
            return primerApellido;
        }
    }
}
                


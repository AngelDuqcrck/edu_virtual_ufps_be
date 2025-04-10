package com.sistemas_mangager_be.edu_virtual_ufps.shared.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginGoogleRequest {
    
    public String email;
    
    public String googleId;

    public String nombre;
    
    public String fotoUrl;

}

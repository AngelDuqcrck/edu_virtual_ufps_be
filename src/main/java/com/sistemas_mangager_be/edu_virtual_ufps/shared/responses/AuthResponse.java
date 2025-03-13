package com.sistemas_mangager_be.edu_virtual_ufps.shared.responses;

import lombok.*;

//Esta clase nos va a devolver la informacion con el token y el tipo que este sea
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String tokenType = "Bearer ";
    private String accessToken;
    private String refreshToken;

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}

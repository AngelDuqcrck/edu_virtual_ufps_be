package com.sistemas_mangager_be.edu_virtual_ufps.shared.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    private String email;
    private String password;
}

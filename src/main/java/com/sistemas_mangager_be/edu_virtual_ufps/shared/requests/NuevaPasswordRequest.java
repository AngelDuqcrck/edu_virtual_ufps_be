package com.sistemas_mangager_be.edu_virtual_ufps.shared.requests;
import jakarta.validation.constraints.NotEmpty;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NuevaPasswordRequest {
    public String token;
    

    @NotEmpty
    public String nuevaPassword;

    @NotEmpty
    public String nuevaPassword2;
}

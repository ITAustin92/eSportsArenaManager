package com.grupo18.auth_service.dtos;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
@Getter @Setter @NoArgsConstructor
public class LoginRequest {
    @NotBlank private String username;
    @NotBlank private String password;
}

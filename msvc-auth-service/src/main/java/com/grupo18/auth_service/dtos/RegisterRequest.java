package com.grupo18.auth_service.dtos;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.*;
@Getter @Setter @NoArgsConstructor
public class RegisterRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    private Set<String> roles = new HashSet<>();
}

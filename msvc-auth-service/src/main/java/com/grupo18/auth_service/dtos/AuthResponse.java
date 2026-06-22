package com.grupo18.auth_service.dtos;
import lombok.*;
import java.util.Set;
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType;
    private String username;
    private Set<String> roles;
}

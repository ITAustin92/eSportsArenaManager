package com.grupo18.tournament_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserDTO {

    private Long id;
    private String nickname;
    private String state;
    private String role;

}
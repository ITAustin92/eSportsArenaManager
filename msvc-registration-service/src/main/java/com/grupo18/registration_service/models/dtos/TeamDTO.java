package com.grupo18.registration_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TeamDTO {

    private Long id;
    private String name;
    private String estado;

}
package com.grupo18.team_service.models.dtos;

import com.grupo18.team_service.models.MemberTeam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class TeamDTO {

    private Long equipoId;
    private String nombre;
    private Long capitanId;
    private Long juegoPrincipalId;
    private String estado;


    private List<MemberTeam> miembros;
}
package com.grupo18.match_service.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {

    private Long userId;
    private Long teamId;
    private Long tournamentId;

    private String type;
    private String subject;
    private String message;

}
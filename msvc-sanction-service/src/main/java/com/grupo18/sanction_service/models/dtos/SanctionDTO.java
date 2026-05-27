package com.grupo18.sanction_service.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class SanctionDTO {

    private Long sanctionId;
    private Long tournamentId;
    private Long teamId;
    private Long matchId;
    private Long userId;

    private String type;
    private String reason;
    private Integer matchesSuspended;
    private Double fineAmount;
    private String status;

}
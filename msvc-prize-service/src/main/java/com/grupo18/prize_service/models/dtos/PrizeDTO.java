package com.grupo18.prize_service.models.dtos;

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
public class PrizeDTO {

    private Long prizeId;
    private Long tournamentId;
    private Long teamId;

    private String description;
    private String type;
    private Double amount;
    private String status;

}
package com.grupo18.prize_service.repositories;

import com.grupo18.prize_service.models.Prize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrizeRepository extends JpaRepository<Prize, Long> {


    List<Prize> findByTournamentId(Long tournamentId);


    List<Prize> findByTeamId(Long teamId);

    /**

     REGLA DE NEGOCIO: Gestión y automatización con el Ranking.
     Busca los premios de un torneo específico que tengan un estado en particular
     (por ejemplo, buscar todos los premios "PENDING" de un torneo para proceder a repartirlos).*/
    List<Prize> findByTournamentIdAndStatus(Long tournamentId, String status);
}
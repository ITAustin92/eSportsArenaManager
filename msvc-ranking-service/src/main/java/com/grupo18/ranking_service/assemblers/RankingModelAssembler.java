package com.grupo18.ranking_service.assemblers;
import com.grupo18.ranking_service.controllers.RankingControllerV2;
import com.grupo18.ranking_service.models.Ranking;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// El ranking no se consulta por su propio ID en la práctica: se consulta por torneo.
// El enlace "self" apunta a la posición del equipo dentro de ese torneo.
@Component
public class RankingModelAssembler implements RepresentationModelAssembler<Ranking, EntityModel<Ranking>> {
    @Override
    public EntityModel<Ranking> toModel(Ranking ranking) {
        return EntityModel.of(ranking,
                linkTo(methodOn(RankingControllerV2.class).getTeamPosition(ranking.getTournamentId(), ranking.getTeamId())).withSelfRel(),
                linkTo(methodOn(RankingControllerV2.class).getTournamentLeaderboard(ranking.getTournamentId())).withRel("leaderboard"));
    }
}

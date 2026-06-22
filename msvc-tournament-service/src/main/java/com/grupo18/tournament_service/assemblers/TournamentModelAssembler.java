package com.grupo18.tournament_service.assemblers;
import com.grupo18.tournament_service.controllers.TournamentControllerV2;
import com.grupo18.tournament_service.models.Tournament;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
@Component
public class TournamentModelAssembler implements RepresentationModelAssembler<Tournament, EntityModel<Tournament>> {
    @Override
    public EntityModel<Tournament> toModel(Tournament entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TournamentControllerV2.class).findById(entity.getTournamentId())).withSelfRel(),
                linkTo(methodOn(TournamentControllerV2.class).findAll()).withRel("tournaments"));
    }
}

package com.grupo18.team_service.assemblers;
import com.grupo18.team_service.controllers.TeamControllerV2;
import com.grupo18.team_service.models.Team;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
@Component
public class TeamModelAssembler implements RepresentationModelAssembler<Team, EntityModel<Team>> {
    @Override public EntityModel<Team> toModel(Team entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(TeamControllerV2.class).findById(entity.getEquipoId())).withSelfRel(),
                linkTo(methodOn(TeamControllerV2.class).findAll()).withRel("teams"));
    }
}

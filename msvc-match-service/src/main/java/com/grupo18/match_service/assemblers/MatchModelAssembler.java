package com.grupo18.match_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.grupo18.match_service.controllers.MatchControllerV2;
import com.grupo18.match_service.models.Match;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class MatchModelAssembler
	implements RepresentationModelAssembler<Match, EntityModel<Match>>
{

	@Override
	public EntityModel<Match> toModel(Match entity) {
		return EntityModel.of(
			entity,
			linkTo(methodOn(MatchControllerV2.class).findById(entity.getMatchId())).withSelfRel(),
			linkTo(methodOn(MatchControllerV2.class).findAll()).withRel("matches")
		);
	}
}

package com.grupo18.sanction_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.grupo18.sanction_service.controllers.SanctionControllerV2;
import com.grupo18.sanction_service.models.Sanction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class SanctionModelAssembler
	implements RepresentationModelAssembler<Sanction, EntityModel<Sanction>>
{

	@Override
	public EntityModel<Sanction> toModel(Sanction entity) {
		return EntityModel.of(
			entity,
			linkTo(methodOn(SanctionControllerV2.class).findById(entity.getSanctionId())).withSelfRel(),
			linkTo(methodOn(SanctionControllerV2.class).findAll()).withRel("sanctions")
		);
	}
}

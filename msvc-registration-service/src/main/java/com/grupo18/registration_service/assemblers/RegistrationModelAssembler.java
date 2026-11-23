package com.grupo18.registration_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.grupo18.registration_service.controllers.RegistrationControllerV2;
import com.grupo18.registration_service.models.Registration;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class RegistrationModelAssembler
	implements RepresentationModelAssembler<Registration, EntityModel<Registration>>
{

	@Override
	public EntityModel<Registration> toModel(Registration entity) {
		return EntityModel.of(
			entity,
			linkTo(
				methodOn(RegistrationControllerV2.class).findById(entity.getRegistrationId())
			).withSelfRel(),
			linkTo(methodOn(RegistrationControllerV2.class).findAll()).withRel("registrations")
		);
	}
}

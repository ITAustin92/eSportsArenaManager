package com.grupo18.notification_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.grupo18.notification_service.controllers.NotificationControllerV2;
import com.grupo18.notification_service.models.Notification;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class NotificationModelAssembler
	implements RepresentationModelAssembler<Notification, EntityModel<Notification>>
{

	@Override
	public EntityModel<Notification> toModel(Notification entity) {
		return EntityModel.of(
			entity,
			linkTo(
				methodOn(NotificationControllerV2.class).findById(entity.getNotificationId())
			).withSelfRel(),
			linkTo(methodOn(NotificationControllerV2.class).findAll()).withRel("notifications")
		);
	}
}

package com.grupo18.user_service.assemblers;


import com.grupo18.user_service.controllers.UserControllerV2;
import com.grupo18.user_service.models.User;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserControllerV2.class).findById(user.getUsuarioId())).withSelfRel(),
                linkTo(methodOn(UserControllerV2.class).findAll()).withRel("users"));
    }
}
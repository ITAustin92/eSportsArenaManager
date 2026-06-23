package com.grupo18.result_service.assemblers;
import com.grupo18.result_service.controllers.ResultControllerV2;
import com.grupo18.result_service.models.Result;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
@Component
public class ResultModelAssembler implements RepresentationModelAssembler<Result, EntityModel<Result>> {
    @Override public EntityModel<Result> toModel(Result entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ResultControllerV2.class).findById(entity.getResultId())).withSelfRel(),
                linkTo(methodOn(ResultControllerV2.class).findAll()).withRel("results"));
    }
}

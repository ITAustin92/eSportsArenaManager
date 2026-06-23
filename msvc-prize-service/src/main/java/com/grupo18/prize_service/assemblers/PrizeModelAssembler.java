package com.grupo18.prize_service.assemblers;
import com.grupo18.prize_service.controllers.PrizeControllerV2;
import com.grupo18.prize_service.models.Prize;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
@Component
public class PrizeModelAssembler implements RepresentationModelAssembler<Prize, EntityModel<Prize>> {
    @Override public EntityModel<Prize> toModel(Prize entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(PrizeControllerV2.class).findById(entity.getPrizeId())).withSelfRel(),
                linkTo(methodOn(PrizeControllerV2.class).findAll()).withRel("prizes"));
    }
}

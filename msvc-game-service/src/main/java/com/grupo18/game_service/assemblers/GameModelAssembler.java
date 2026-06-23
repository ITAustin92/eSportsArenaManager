package com.grupo18.game_service.assemblers;

import com.grupo18.game_service.controllers.GameControllerV2;
import com.grupo18.game_service.models.Game;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

// @Component: bean reutilizable que convierte Game → EntityModel<Game> (datos + enlaces HATEOAS).
@Component
public class GameModelAssembler implements RepresentationModelAssembler<Game, EntityModel<Game>> {

    @Override
    public EntityModel<Game> toModel(Game game) {
        return EntityModel.of(
                game,
                // self: enlace al propio recurso (GET /api/v2/games/{id})
                linkTo(methodOn(GameControllerV2.class).findById(game.getJuegoId())).withSelfRel(),
                // games: enlace a la colección completa
                linkTo(methodOn(GameControllerV2.class).findAll()).withRel("games")
        );
    }
}

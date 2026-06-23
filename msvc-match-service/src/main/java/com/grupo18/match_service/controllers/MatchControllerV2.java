package com.grupo18.match_service.controllers;
import com.grupo18.match_service.assemblers.MatchModelAssembler;
import com.grupo18.match_service.models.Match;
import com.grupo18.match_service.services.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@RestController @RequestMapping("/api/v2/matches") @Validated
@Tag(name = "Match V2", description = "CRUD de matches con respuestas HATEOAS")
public class MatchControllerV2 {
    @Autowired private MatchService matchService;
    @Autowired private MatchModelAssembler matchModelAssembler;
    @GetMapping
    @Operation(summary = "Listar matches")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Match>>> findAll() {
        List<EntityModel<Match>> models = matchService.findAll().stream().map(matchModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(MatchControllerV2.class).findAll()).withSelfRel()));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar Match por ID")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Encontrado"),
            @ApiResponse(responseCode="404",description="No encontrado")})
    public ResponseEntity<EntityModel<Match>> findById(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        return ResponseEntity.ok(matchModelAssembler.toModel(matchService.findById(id)));
    }
    @PostMapping
    @Operation(summary = "Crear Match")
    public ResponseEntity<EntityModel<Match>> save(@Valid @RequestBody Match entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchModelAssembler.toModel(matchService.save(entity)));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Match")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Actualizado"),
            @ApiResponse(responseCode="404",description="No encontrado")})
    public ResponseEntity<EntityModel<Match>> update(@Parameter(description="ID",required=true,example="1") @PathVariable Long id, @Valid @RequestBody Match entity) {
        return ResponseEntity.ok(matchModelAssembler.toModel(matchService.updateById(id, entity)));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Match")
    @ApiResponse(responseCode="204",description="Eliminado")
    public ResponseEntity<Void> delete(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        matchService.deleteById(id); return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

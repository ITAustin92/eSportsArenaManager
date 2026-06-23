package com.grupo18.team_service.controllers;
import com.grupo18.team_service.assemblers.TeamModelAssembler;
import com.grupo18.team_service.models.Team;
import com.grupo18.team_service.services.TeamService;
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
@RestController @RequestMapping("/api/v2/teams") @Validated
@Tag(name = "Team V2", description = "CRUD de teams con respuestas HATEOAS")
public class TeamControllerV2 {

    @Autowired private TeamService teamService;

    @Autowired private TeamModelAssembler teamModelAssembler;

    @GetMapping
    @Operation(summary = "Listar teams")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Team>>> findAll() {
        List<EntityModel<Team>> models = teamService.findAll().stream().map(teamModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(TeamControllerV2.class).findAll()).withSelfRel()));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar Team por ID")
    @ApiResponses(
            value={
                    @ApiResponse(responseCode="200",description="Encontrado"),
                    @ApiResponse(responseCode="404",description="No encontrado")})
    public ResponseEntity<EntityModel<Team>> findById(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        return ResponseEntity.ok(teamModelAssembler.toModel(teamService.findById(id)));
    }
    @PostMapping
    @Operation(summary = "Crear Team")
    public ResponseEntity<EntityModel<Team>> save(@Valid @RequestBody Team entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamModelAssembler.toModel(teamService.save(entity)));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Team")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Actualizado"),
            @ApiResponse(responseCode="404",description="No encontrado")})
    public ResponseEntity<EntityModel<Team>> update(@Parameter(description="ID",required=true,example="1") @PathVariable Long id, @Valid @RequestBody Team entity) {
        return ResponseEntity.ok(teamModelAssembler.toModel(teamService.updateById(id, entity)));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Team")
    @ApiResponse(responseCode="204",description="Eliminado")
    public ResponseEntity<Void> delete(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        teamService.deleteById(id); return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

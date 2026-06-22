package com.grupo18.tournament_service.controllers;
import com.grupo18.tournament_service.assemblers.TournamentModelAssembler;
import com.grupo18.tournament_service.models.Tournament;
import com.grupo18.tournament_service.services.TournamentService;
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
@RestController
@RequestMapping("/api/v2/tournaments")
@Validated
@Tag(name = "Tournaments V2", description = "CRUD de torneos con respuestas HATEOAS")
public class TournamentControllerV2 {
    @Autowired private TournamentService tournamentService;
    @Autowired private TournamentModelAssembler tournamentModelAssembler;
    @GetMapping @Operation(summary = "Listar torneos") @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Tournament>>> findAll() {
        List<EntityModel<Tournament>> models = tournamentService.findAll().stream().map(tournamentModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(TournamentControllerV2.class).findAll()).withSelfRel()));
    }
    @GetMapping("/{id}") @Operation(summary = "Buscar torneo por ID")
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Encontrado"),@ApiResponse(responseCode="404",description="No encontrado")})
    public ResponseEntity<EntityModel<Tournament>> findById(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        return ResponseEntity.ok(tournamentModelAssembler.toModel(tournamentService.findById(id)));
    }
    @PostMapping @Operation(summary = "Crear torneo")
    public ResponseEntity<EntityModel<Tournament>> save(@Valid @RequestBody Tournament tournament) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentModelAssembler.toModel(tournamentService.save(tournament)));
    }
    @PutMapping("/{id}") @Operation(summary = "Actualizar torneo")
    public ResponseEntity<EntityModel<Tournament>> update(@Parameter(description="ID",required=true,example="1") @PathVariable Long id, @Valid @RequestBody Tournament tournament) {
        return ResponseEntity.ok(tournamentModelAssembler.toModel(tournamentService.updateById(id, tournament)));
    }
    @DeleteMapping("/{id}") @Operation(summary = "Eliminar torneo") @ApiResponse(responseCode="204",description="Eliminado")
    public ResponseEntity<Void> delete(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        tournamentService.deleteById(id); return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

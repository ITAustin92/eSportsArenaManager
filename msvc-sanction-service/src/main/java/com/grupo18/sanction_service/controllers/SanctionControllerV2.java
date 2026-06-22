package com.grupo18.sanction_service.controllers;
import com.grupo18.sanction_service.assemblers.SanctionModelAssembler;
import com.grupo18.sanction_service.models.Sanction;
import com.grupo18.sanction_service.services.SanctionService;
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

@RestController @RequestMapping("/api/v2/sanctions") @Validated
@Tag(name = "Sanctions V2", description = "Sanciones a jugadores/equipos con respuestas HATEOAS")
public class SanctionControllerV2 {
    @Autowired private SanctionService sanctionService;
    @Autowired private SanctionModelAssembler sanctionModelAssembler;

    @GetMapping
    @Operation(summary = "Listar sanciones")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Sanction>>> findAll() {
        List<EntityModel<Sanction>> models = sanctionService.findAll().stream().map(sanctionModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(SanctionControllerV2.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar sanción por ID")
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Encontrada"),@ApiResponse(responseCode="404",description="No encontrada")})
    public ResponseEntity<EntityModel<Sanction>> findById(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        return ResponseEntity.ok(sanctionModelAssembler.toModel(sanctionService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear sanción")
    public ResponseEntity<EntityModel<Sanction>> save(@Valid @RequestBody Sanction sanction) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sanctionModelAssembler.toModel(sanctionService.save(sanction)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de la sanción", description = "Ej: ACTIVE, CLOSED")
    @ApiResponses(value={@ApiResponse(responseCode="200",description="Actualizada"),@ApiResponse(responseCode="404",description="No encontrada")})
    public ResponseEntity<EntityModel<Sanction>> updateStatus(
            @Parameter(description="ID",required=true,example="1") @PathVariable Long id,
            @Parameter(description="Nuevo estado",required=true,example="CLOSED") @RequestParam String status) {
        return ResponseEntity.ok(sanctionModelAssembler.toModel(sanctionService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sanción")
    @ApiResponse(responseCode="204",description="Eliminada")
    public ResponseEntity<Void> delete(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        sanctionService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package com.grupo18.registration_service.controllers;
import com.grupo18.registration_service.assemblers.RegistrationModelAssembler;
import com.grupo18.registration_service.models.Registration;
import com.grupo18.registration_service.services.RegistrationService;
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

// V2 — respuestas HATEOAS. Los métodos respetan la lógica real del servicio:
// updateStatus (no hay updateById genérico) y cancelById (no hay delete físico).
@RestController @RequestMapping("/api/v2/registrations") @Validated
@Tag(name = "Registrations V2", description = "Inscripciones a torneos con respuestas HATEOAS")
public class RegistrationControllerV2 {
    @Autowired private RegistrationService registrationService;
    @Autowired private RegistrationModelAssembler registrationModelAssembler;

    @GetMapping
    @Operation(summary = "Listar inscripciones")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Registration>>> findAll() {
        List<EntityModel<Registration>> models = registrationService.findAll().stream().map(registrationModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(RegistrationControllerV2.class).findAll()).withSelfRel()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar inscripción por ID")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Encontrada"),
            @ApiResponse(responseCode="404",description="No encontrada")})
    public ResponseEntity<EntityModel<Registration>> findById(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        return ResponseEntity.ok(registrationModelAssembler.toModel(registrationService.findById(id)));
    }

    @PostMapping
    @Operation(summary = "Crear inscripción", description = "Valida que el equipo esté ACTIVO y el torneo admita inscripciones")
    public ResponseEntity<EntityModel<Registration>> save(@Valid @RequestBody Registration registration) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationModelAssembler.toModel(registrationService.save(registration)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de la inscripción", description = "Ej: PENDING, ACCEPTED, REJECTED, CANCELLED")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Actualizada"),
            @ApiResponse(responseCode="404",description="No encontrada")})
    public ResponseEntity<EntityModel<Registration>> updateStatus(
            @Parameter(description="ID",required=true,example="1") @PathVariable Long id,
            @Parameter(description="Nuevo estado",required=true,example="ACCEPTED") @RequestParam String status) {
        return ResponseEntity.ok(registrationModelAssembler.toModel(registrationService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancelar inscripción")
    @ApiResponse(responseCode="204",description="Cancelada")
    public ResponseEntity<Void> cancel(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        registrationService.cancelById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

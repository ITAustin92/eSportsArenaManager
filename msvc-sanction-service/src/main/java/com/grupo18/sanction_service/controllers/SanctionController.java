package com.grupo18.sanction_service.controllers;

import com.grupo18.sanction_service.models.Sanction;
import com.grupo18.sanction_service.services.SanctionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sanctions V1", description = "Sanciones a jugadores y equipos")
@RestController
@RequestMapping("/api/v1/sanctions")
@Tag(name = "Sanciones V1", description = "Metodos CRUD para la gestión de sanciones")
public class SanctionController {

    @Autowired
    private SanctionService sanctionService;


    @PostMapping
    @Operation(summary = "Guardado de la sanción", description = "Método que guarda la sanción en la DB")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Sanción a crear", required = true,
            content = @Content(schema = @Schema(implementation = Sanction.class))
    )
    public ResponseEntity<Sanction> save(@Valid @RequestBody Sanction sanction) {
        Sanction savedSanction = sanctionService.save(sanction);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSanction);
    }

    @GetMapping
    @Operation(
            summary = "Listado de todas las sanciones",
            description = "Se devuelve una lista con todos las sanciones en la tabla usuarios de la DB"
    )
    @ApiResponse(responseCode = "200", description = "Operacion Exitosa")
    public ResponseEntity<List<Sanction>> findAll() {
        return ResponseEntity.ok(sanctionService.findAll());
    }


    @GetMapping("/{id}")
    @Operation(summary = "Buscar sanción por ID")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Encontrada"),
            @ApiResponse(responseCode="404",description="No encontrada")})
    public ResponseEntity<Sanction> findById(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        return ResponseEntity.ok(sanctionService.findById(id));
    }


    @PatchMapping("/{id}/status")
    @Operation(summary = "Actualizar estado de la sanción", description = "Ej: ACTIVE, CLOSED")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Actualizada"),
            @ApiResponse(responseCode="404",description="No encontrada")})
    public ResponseEntity<Sanction> updateStatus(@Parameter(description="ID",required=true,example="1") @PathVariable Long id,
                                                 @Parameter(description="Nuevo estado",required=true,example="CLOSED") @RequestParam String status) {
        return ResponseEntity.ok(sanctionService.updateStatus(id, status));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sanción")
    @ApiResponse(responseCode="204",description="Eliminada")
    public ResponseEntity<Void> deleteById(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        sanctionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Encontrar torneo por ID",
               description = "Se devuelve un torneo, en caso contrario se devuelve una excepcion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Torneo encontrada"),
            @ApiResponse(responseCode = "404", description = "Torneo no se encuentra en la BD")
    })
    public ResponseEntity<List<Sanction>> findByTournamentId(@Parameter(description="ID",required=true,example="1") @PathVariable Long tournamentId) {
        return ResponseEntity.ok(sanctionService.findByTournamentId(tournamentId));
    }


    @GetMapping("/team/{teamId}")
    @Operation(summary = "Encontrar equipo por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Equipo encontrado"),
            @ApiResponse(responseCode = "404", description = "Equipo no se encuentra en la BD")
    })
    public ResponseEntity<List<Sanction>> findByTeamId(@Parameter(description="ID",required=true,example="1") @PathVariable Long teamId) {
        return ResponseEntity.ok(sanctionService.findByTeamId(teamId));
    }


    @GetMapping("/user/{userId}")
    @Operation(summary = "Encontrar usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no se encuentra en la BD")
    })
    public ResponseEntity<List<Sanction>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(sanctionService.findByUserId(userId));
    }
}
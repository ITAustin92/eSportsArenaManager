package com.grupo18.result_service.controllers;
import com.grupo18.result_service.assemblers.ResultModelAssembler;
import com.grupo18.result_service.models.Result;
import com.grupo18.result_service.services.ResultService;
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



@RestController @RequestMapping("/api/v2/results") @Validated
@Tag(name = "Result V2", description = "CRUD de results con respuestas HATEOAS")
public class ResultControllerV2 {
    @Autowired private ResultService resultService;
    @Autowired private ResultModelAssembler resultModelAssembler;
    @GetMapping
    @Operation(summary = "Listar results")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<CollectionModel<EntityModel<Result>>> findAll() {
        List<EntityModel<Result>> models = resultService.findAll().stream().map(resultModelAssembler::toModel).toList();
        return ResponseEntity.ok(CollectionModel.of(models, linkTo(methodOn(ResultControllerV2.class).findAll()).withSelfRel()));
    }
    @GetMapping("/{id}")
    @Operation(summary = "Buscar Result por ID")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Encontrado"),
            @ApiResponse(responseCode="404",description="No encontrado")})
    public ResponseEntity<EntityModel<Result>> findById(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        return ResponseEntity.ok(resultModelAssembler.toModel(resultService.findById(id)));
    }
    @PostMapping
    @Operation(summary = "Crear Result")
    public ResponseEntity<EntityModel<Result>> save(@Valid @RequestBody Result entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resultModelAssembler.toModel(resultService.save(entity)));
    }
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar Result")
    @ApiResponses(value={
            @ApiResponse(responseCode="200",description="Actualizado"),
            @ApiResponse(responseCode="404",description="No encontrado")})
    public ResponseEntity<EntityModel<Result>> update(@Parameter(description="ID",required=true,example="1") @PathVariable Long id, @Valid @RequestBody Result entity) {
        return ResponseEntity.ok(resultModelAssembler.toModel(resultService.updateById(id, entity)));
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar Result")
    @ApiResponse(responseCode="204",description="Eliminado")
    public ResponseEntity<Void> delete(@Parameter(description="ID",required=true,example="1") @PathVariable Long id) {
        resultService.deleteById(id); return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

package com.grupo18.registration_service.controllers;

import com.grupo18.registration_service.clients.TeamClient;
import com.grupo18.registration_service.models.Registration;
import com.grupo18.registration_service.models.dtos.TeamDTO;
import com.grupo18.registration_service.security.UsuarioActual;
import com.grupo18.registration_service.services.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Registrations V1", description = "Inscripciones a torneos")
@RestController
@RequestMapping("/api/v1/registrations")
@Tag(name = "Registro V1", description = "Metodos CRUD para la gestión de registros")
public class RegistrationController {

	@Autowired
	private RegistrationService registrationService;

	@Autowired
	private TeamClient teamClient;

	@GetMapping
	@Operation(
		summary = "Listado de todos los registros",
		description = "Se devuelve una lista con todos los registros en la tabla registro de la DB"
	)
	@ApiResponse(responseCode = "200", description = "Operacion Exitosa")
	public ResponseEntity<List<Registration>> findAll() {
		return ResponseEntity.ok(registrationService.findAll());
	}

	@PostMapping
	@Operation(
		summary = "Guardado del registro",
		description = "Método que guarda el registro en la DB"
	)
	@io.swagger.v3.oas.annotations.parameters.RequestBody(
		description = "Registro a guardar",
		required = true,
		content = @Content(schema = @Schema(implementation = Registration.class))
	)
	public ResponseEntity<Registration> save(
		@Valid @RequestBody Registration registration,
		@Parameter(hidden = true) Authentication autenticacion
	) {
		if (!UsuarioActual.esAdmin(autenticacion)) {
			// Un jugador solo inscribe equipos donde es capitán.
			if (registration.getUserId() != null) {
				if (!Objects.equals(registration.getUserId(), UsuarioActual.exigirUserId(autenticacion))) {
					throw new ResponseStatusException(
						HttpStatus.FORBIDDEN,
						"Solo puedes inscribirte a ti mismo"
					);
				}
			} else {
				exigirCapitan(registration.getTeamId(), autenticacion);
			}
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.save(registration));
	}

	@PatchMapping("/{id}/status")
	@Operation(
		summary = "Actualizacion de registro",
		description = "Se actualizan los datos de un registro existente"
	)
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "Registro actualizado"),
			@ApiResponse(responseCode = "404", description = "Registro no se encuentra en la BD"),
		}
	)
	public ResponseEntity<Registration> updateStatus(
		@Parameter(
			description = "ID del Usuario a actualizar",
			required = true,
			example = "1"
		) @PathVariable Long id,
		@RequestParam String status
	) {
		return ResponseEntity.ok(registrationService.updateStatus(id, status));
	}

	@GetMapping("/{id}")
	@Operation(
		summary = "Busqueda de un registro por id",
		description = "Se devuelve un registro, en caso contrario se devuelve una excepcion"
	)
	@ApiResponses(
		value = {
			@ApiResponse(
				responseCode = "200",
				description = "Registro encontrado",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = Registration.class)
				)
			),
			@ApiResponse(responseCode = "404", description = "Registro no se encuentra en la BD"),
		}
	)
	public ResponseEntity<Registration> findById(
		@Parameter(
			description = "ID del Usuario buscado",
			required = true,
			example = "1"
		) @PathVariable Long id
	) {
		return ResponseEntity.ok(registrationService.findById(id));
	}

	@GetMapping("/team/{teamId}")
	@Operation(
		summary = "Busqueda de un equipo por ID",
		description = "Se devuelve un equipo, en caso contrario se devuelve una excepcion"
	)
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "Equipo encontrado"),
			@ApiResponse(responseCode = "404", description = "Equipo no se encuentra en la BD"),
		}
	)
	public ResponseEntity<List<Registration>> findByTeamId(
		@Parameter(
			description = "ID del Team buscado",
			required = true,
			example = "1"
		) @PathVariable Long teamId,
		@Parameter(hidden = true) Authentication autenticacion
	) {
		if (
			!UsuarioActual.esAdmin(autenticacion) &&
			autenticacion
				.getAuthorities()
				.stream()
				.noneMatch(a -> "ROLE_ORGANIZADOR".equals(a.getAuthority()))
		) exigirCapitan(teamId, autenticacion);
		return ResponseEntity.ok(registrationService.findByTeamId(teamId));
	}

	@GetMapping("/tournament/{tournamentId}")
	@Operation(
		summary = "Busqueda de un torneo por ID",
		description = "Se devuelve un torneo, en caso contrario se devuelve una excepcion"
	)
	@ApiResponses(
		value = {
			@ApiResponse(responseCode = "200", description = "Torneo encontrado"),
			@ApiResponse(responseCode = "404", description = "Torneo no se encuentra en la BD"),
		}
	)
	public ResponseEntity<List<Registration>> findByTournamentId(
		@Parameter(
			description = "ID del Torneo buscado",
			required = true,
			example = "1"
		) @PathVariable Long tournamentId
	) {
		return ResponseEntity.ok(registrationService.findByTournamentId(tournamentId));
	}

	@DeleteMapping("/{id}")
	@Operation(
		summary = "Cancela un registro existente",
		description = "Se elimina un registro de la base de datos"
	)
	@ApiResponse(responseCode = "204", description = "Registro cancelado")
	public ResponseEntity<Void> cancel(
		@Parameter(
			description = "ID del registro a cancelar",
			required = true,
			example = "1"
		) @PathVariable Long id,
		@Parameter(hidden = true) Authentication autenticacion
	) {
		if (!UsuarioActual.esAdmin(autenticacion)) {
			Registration registro = registrationService.findById(id);
			if (registro.getUserId() != null) {
				if (!Objects.equals(registro.getUserId(), UsuarioActual.exigirUserId(autenticacion))) {
					throw new ResponseStatusException(
						HttpStatus.FORBIDDEN,
						"La inscripción pertenece a otra persona"
					);
				}
			} else {
				exigirCapitan(registro.getTeamId(), autenticacion);
			}
		}
		registrationService.cancelById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exists")
	@Operation(summary = "Revisa si existe una inscripcion por torneo y un equipo")
	public ResponseEntity<Boolean> existsByTeamAndTournament(
		@Parameter(
			description = "ID del equipo",
			required = true,
			example = "1"
		) @RequestParam Long teamId,
		@Parameter(
			description = "ID del torneo",
			required = true,
			example = "1"
		) @RequestParam Long tournamentId
	) {
		boolean exists = registrationService.existsByTeamIdAndTournamentId(teamId, tournamentId);
		return ResponseEntity.ok(exists);
	}

	@Autowired
	private com.grupo18.registration_service.repositories.RegistrationRepository registros;

	@GetMapping("/mine")
	@Operation(summary = "Inscripciones individuales de la sesión actual")
	public ResponseEntity<List<Registration>> individuales(
		@Parameter(hidden = true) Authentication autenticacion
	) {
		return ResponseEntity.ok(registros.findByUserId(UsuarioActual.exigirUserId(autenticacion)));
	}

	private void exigirCapitan(Long teamId, Authentication autenticacion) {
		Long yo = UsuarioActual.exigirUserId(autenticacion);
		TeamDTO equipo;
		try {
			equipo = teamClient.getTeamById(teamId);
		} catch (Exception e) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"No se pudo verificar el equipo indicado"
			);
		}
		if (equipo == null || !Objects.equals(equipo.getCapitanId(), yo)) {
			throw new ResponseStatusException(
				HttpStatus.FORBIDDEN,
				"Solo el capitán del equipo puede gestionar sus inscripciones"
			);
		}
	}

	@GetMapping("/exists-player")
	public ResponseEntity<Boolean> jugadorInscrito(
		@RequestParam Long userId,
		@RequestParam Long tournamentId
	) {
		return ResponseEntity.ok(
			registros
				.findByUserIdAndTournamentId(userId, tournamentId)
				.filter(r -> !java.util.Set.of("CANCELLED", "REJECTED").contains(r.getStatus()))
				.isPresent()
		);
	}
}

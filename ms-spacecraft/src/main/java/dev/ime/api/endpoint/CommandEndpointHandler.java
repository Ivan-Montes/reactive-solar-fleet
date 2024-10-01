package dev.ime.api.endpoint;


import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.SpacecraftDto;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.port.inbound.CommandEndpointPort;
import dev.ime.domain.port.inbound.CommandServicePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Component
@Tag(name = "Spacecrafts", description = "API for managing spacecrafts")
public class CommandEndpointHandler implements CommandEndpointPort{

	private final CommandServicePort<SpacecraftDto> commandService;
	private final DtoValidator dtoValidator;
	private final ErrorHandler errorHandler;	

	public CommandEndpointHandler(CommandServicePort<SpacecraftDto> commandService, DtoValidator positionValidator,
			ErrorHandler errorHandler) {
		this.commandService = commandService;
		this.dtoValidator = positionValidator;
		this.errorHandler = errorHandler;
	}
	@Operation(
            summary = "Create a Spacecraft",
            description = "Returns created Spacecraft",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Spacecraft object that needs to be created",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = SpacecraftDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Created Spacecraft",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = SpacecraftDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> create(ServerRequest serverRequest) {		
		
		return serverRequest.bodyToMono(SpacecraftDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(commandService::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
				.onErrorResume(errorHandler::handleException);
	}

	@Operation(
            summary = "Update a Spacecraft",
            description = "Returns updated Spacecraft",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Spacecraft ID", schema = @Schema(type = "string", format = "uuid"))
                },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Spacecraft object that needs to be updated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SpacecraftDto.class)
                )
            )        
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Updated Spacecraft",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = SpacecraftDto.class)
        		)
        )
	@Override	
	public Mono<ServerResponse> update(ServerRequest serverRequest) {
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
				.map(UUID::fromString)
				.onErrorMap(IllegalArgumentException.class, error -> new InvalidUUIDException(Map.of(GlobalConstants.SPACECRAFT_ID, GlobalConstants.MSG_NODATA)))
				.flatMap( id -> serverRequest.bodyToMono(SpacecraftDto.class)
						.flatMap(dtoValidator::validateDto)
						.flatMap( dto -> commandService.update(id, dto))
						)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
				.onErrorResume(errorHandler::handleException);
		
	}
	
	@Operation(
            summary = "Delete a Spacecraft",
            description = "Returns deleted Spacecraft",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Spacecraft ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Deleted Spacecraft",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = SpacecraftDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {		
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
		.map(UUID::fromString)
		.flatMap(commandService::deleteById)
		.flatMap( obj -> ServerResponse.ok().bodyValue(obj))
		.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
                serverRequest.path(), GlobalConstants.MSG_NODATA
            ))))
		.onErrorResume(errorHandler::handleException);
		
	}
	
}

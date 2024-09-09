package dev.ime.api.endpoint;


import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.error.DtoValidator;
import dev.ime.application.dto.PositionDto;
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
@Tag(name = "Positions", description = "API for managing Positions")
public class CommandEndpointHandler implements CommandEndpointPort{

	private final CommandServicePort<PositionDto> commandService;
	private final DtoValidator dtoValidator;
	private final ErrorHandler errorHandler;	

	public CommandEndpointHandler(CommandServicePort<PositionDto> commandService, DtoValidator dtoValidator,
			ErrorHandler errorHandler) {
		this.commandService = commandService;
		this.dtoValidator = dtoValidator;
		this.errorHandler = errorHandler;
	}
	@Operation(
            summary = "Create a Position",
            description = "Returns created Position",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Position object that needs to be updated",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PositionDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Created Position",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PositionDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> create(ServerRequest serverRequest) {		
		
		return serverRequest.bodyToMono(PositionDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(commandService::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(ServerResponse.noContent().build())
				.onErrorResume(errorHandler::handleException);
	}

	@Operation(
            summary = "Update a Position",
            description = "Returns updated Position",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Position ID", schema = @Schema(type = "string", format = "uuid"))
                },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Position object that needs to be updated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PositionDto.class)
                )
            )        
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Updated Position",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PositionDto.class)
        		)
        )
	@Override	
	public Mono<ServerResponse> update(ServerRequest serverRequest) {
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
				.map(UUID::fromString)
				.onErrorMap(IllegalArgumentException.class, error -> new InvalidUUIDException(Map.of(GlobalConstants.POSITION_ID, serverRequest.pathVariable("id"))))
				.flatMap( id -> serverRequest.bodyToMono(PositionDto.class)
						.flatMap(dtoValidator::validateDto)
						.flatMap( dto -> commandService.update(id, dto))
						)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(errorHandler::handleException);
		
	}
	@Operation(
            summary = "Delete a Position",
            description = "Returns deleted Position",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Position ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Deleted Position",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PositionDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> deleteById(ServerRequest serverRequest) {		
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
		.map(UUID::fromString)
		.flatMap(commandService::deleteById)
		.flatMap( obj -> ServerResponse.ok().bodyValue(obj))
		.switchIfEmpty(ServerResponse.notFound().build())
		.onErrorResume(errorHandler::handleException);
		
	}

	
}

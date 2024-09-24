package dev.ime.api.endpoint;


import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.error.DtoValidator;
import dev.ime.application.dto.ShipclassDto;
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
@Tag(name = "Shipclasses", description = "API for managing Shipclasses")
public class CommandEndpointHandler implements CommandEndpointPort{

	private final CommandServicePort<ShipclassDto> commandService;
	private final DtoValidator dtoValidator;
	private final ErrorHandler errorHandler;	

	public CommandEndpointHandler(CommandServicePort<ShipclassDto> commandService, DtoValidator positionValidator,
			ErrorHandler errorHandler) {
		super();
		this.commandService = commandService;
		this.dtoValidator = positionValidator;
		this.errorHandler = errorHandler;
	}

    @Operation(
            summary = "Create a Shipclass",
            description = "Returns created Shipclass",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Shipclass object that needs to be created",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ShipclassDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Created Shipclass",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ShipclassDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> create(ServerRequest serverRequest) {		
		
		return serverRequest.bodyToMono(ShipclassDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(commandService::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
				.onErrorResume(errorHandler::handleException);
	}

    @Operation(
            summary = "Update a Shipclass",
            description = "Returns updated Shipclass",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Shipclass ID", schema = @Schema(type = "string", format = "uuid"))
                },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Shipclass object that needs to be updated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ShipclassDto.class)
                )
            )        
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Updated Shipclass",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ShipclassDto.class)
        		)
        )
	@Override	
	public Mono<ServerResponse> update(ServerRequest serverRequest) {
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
				.map(UUID::fromString)
				.onErrorMap(IllegalArgumentException.class, error -> new InvalidUUIDException(Map.of(GlobalConstants.SHIPCLASS_ID, GlobalConstants.MSG_NODATA)))
				.flatMap( id -> serverRequest.bodyToMono(ShipclassDto.class)
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
            summary = "Delete a Shipclass",
            description = "Returns deleted Shipclass",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Shipclass ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Deleted Shipclass",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ShipclassDto.class)
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

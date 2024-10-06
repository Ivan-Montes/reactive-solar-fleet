package dev.ime.api.endpoint;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.api.validation.DtoValidator;
import dev.ime.application.dto.CrewMemberDto;
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
@Tag(name = "CrewMembers", description = "API for managing crewmembers")
public class CommandEndpointHandler  implements CommandEndpointPort{

	private final DtoValidator dtoValidator;
	private final ErrorHandler errorHandler;
	private final CommandServicePort<CrewMemberDto> commandService;
	
	public CommandEndpointHandler(DtoValidator dtoValidator, ErrorHandler errorHandler, CommandServicePort<CrewMemberDto> commandService) {
		super();
		this.dtoValidator = dtoValidator;
		this.errorHandler = errorHandler;
		this.commandService = commandService;
	}

	@Operation(
            summary = "Create a CrewMember",
            description = "Returns created CrewMember",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "CrewMember object that needs to be created",
                    required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CrewMemberDto.class)
                    )
                )        
            )
    @ApiResponse(
        responseCode = "200", 
        description = "Created CrewMember",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CrewMemberDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> create(ServerRequest serverRequest) {
		
		return serverRequest.bodyToMono(CrewMemberDto.class)
				.flatMap(dtoValidator::validateDto)
				.flatMap(commandService::create)
				.flatMap( objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
				.onErrorResume(errorHandler::handleException);		
	}

	@Operation(
            summary = "Update a CrewMember",
            description = "Returns updated CrewMember",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "CrewMember ID", schema = @Schema(type = "string", format = "uuid"))
                },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "CrewMember object that needs to be updated",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = CrewMemberDto.class)
                )
            )        
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Updated CrewMember",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CrewMemberDto.class)
        		)
        )
	@Override
	public Mono<ServerResponse> update(ServerRequest serverRequest) {
		
		return Mono.justOrEmpty(serverRequest.pathVariable("id"))
				.map(UUID::fromString)
				.onErrorMap(IllegalArgumentException.class, error -> new InvalidUUIDException(Map.of(GlobalConstants.CREWMEMBER_ID, GlobalConstants.MSG_NODATA)))
				.flatMap( id -> serverRequest
							.bodyToMono(CrewMemberDto.class)
							.flatMap(dtoValidator::validateDto)
							.flatMap( dto -> commandService.update(id, dto))
				)
				.flatMap(objSaved -> ServerResponse.ok().bodyValue(objSaved))
				.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
				.onErrorResume(errorHandler::handleException);
	}

	@Operation(
            summary = "Delete a CrewMember",
            description = "Returns deleted CrewMember",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "CrewMember ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Deleted CrewMember",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CrewMemberDto.class)
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

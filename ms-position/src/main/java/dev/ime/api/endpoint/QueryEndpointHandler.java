package dev.ime.api.endpoint;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.application.dto.PositionDto;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.PositionMapper;
import dev.ime.domain.model.Position;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;
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
public class QueryEndpointHandler implements QueryEndpointPort{

	private final QueryServicePort<Position> queryService;
	private final PositionMapper positionMapper;
	private final ErrorHandler errorHandler;

	public QueryEndpointHandler(QueryServicePort<Position> queryService, PositionMapper positionMapper,
			ErrorHandler errorHandler) {
		this.queryService = queryService;
		this.positionMapper = positionMapper;
		this.errorHandler = errorHandler;
	}

    @Operation(
            summary = "Get all Positions",
            description = "Returns a list of all available Positions"
        )
    @ApiResponse(
        responseCode = "200", 
        description = "List of Positions found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PositionDto.class)
        )
    )
	@Override
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
    	
        return queryService.getAll()
                .map(positionMapper::fromDomainToDto)
                .collectList() 
                .flatMap(dtos -> ServerResponse.ok().bodyValue(dtos))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(errorHandler::handleException);
    }
    @Operation(
            summary = "Get a Positions by ID",
            description = "Returns a specific Positions based on its ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Positions ID", schema = @Schema(type = "string", format = "uuid"))
                }
    		)
    @ApiResponse(
        responseCode = "200", 
        description = "Positions found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PositionDto.class)
        )
    )
	@Override
	public Mono<ServerResponse> getById(ServerRequest serverRequest) {
		
		return Mono.defer( () -> {
			
			try {
				
				UUID id = UUID.fromString( serverRequest.pathVariable("id") );
				
				return queryService
						.getById(id)
						.map(positionMapper::fromDomainToDto)
						.flatMap( dto -> ServerResponse.ok().bodyValue(dto))
		                .switchIfEmpty(ServerResponse.notFound().build())
						.onErrorResume(errorHandler::handleException);
				
			} catch (IllegalArgumentException error) {
					
	            return errorHandler.handleException(new InvalidUUIDException(Map.of(GlobalConstants.POSITION_ID, serverRequest.pathVariable("id"))));
	            
	        }			
		});
	}	
	
}

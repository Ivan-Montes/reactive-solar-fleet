package dev.ime.api.endpoint;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import dev.ime.api.error.ErrorHandler;
import dev.ime.application.dto.ShipclassDto;
import dev.ime.application.exception.EmptyResponseException;
import dev.ime.application.exception.InvalidUUIDException;
import dev.ime.config.GlobalConstants;
import dev.ime.config.ShipclassMapper;
import dev.ime.domain.model.Shipclass;
import dev.ime.domain.port.inbound.QueryEndpointPort;
import dev.ime.domain.port.inbound.QueryServicePort;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import reactor.core.publisher.Mono;

@Component
@Tag(name = "Shipclasses", description = "API for managing Shipclasses")
public class QueryEndpointHandler implements QueryEndpointPort{

	private final QueryServicePort<Shipclass> queryService;
	private final ShipclassMapper mapper;
	private final ErrorHandler errorHandler;

	public QueryEndpointHandler(QueryServicePort<Shipclass> queryService, ShipclassMapper mapper,
			ErrorHandler errorHandler) {
		this.queryService = queryService;
		this.mapper = mapper;
		this.errorHandler = errorHandler;
	}
	
    @Operation(
            summary = "Get all Shipclasses",
            description = "Returns a list of all available Shipclasses"
        )
    @ApiResponse(
        responseCode = "200", 
        description = "List of Shipclasses found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ShipclassDto.class)
        )
    )
    @Override
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
    	
        return queryService.getAll()
                .map(mapper::fromDomainToDto)
                .collectList() 
                .flatMap(dtos -> ServerResponse.ok().bodyValue(dtos))
                .switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
		                serverRequest.path(), GlobalConstants.MSG_NODATA
		            ))))
                .onErrorResume(errorHandler::handleException);
    }	
	
    @Operation(
            summary = "Get a Shipclass by ID",
            description = "Returns a specific Shipclass based on its ID",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true, description = "Shipclass ID", schema = @Schema(type = "string", format = "uuid"))
                }
        )
    @ApiResponse(
        responseCode = "200", 
        description = "Shipclass found",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ShipclassDto.class)
        )
    )
    @Override
	public Mono<ServerResponse> getById(ServerRequest serverRequest) {
		
		return Mono.defer( () -> {
			
			try {
				
				UUID id = UUID.fromString( serverRequest.pathVariable("id") );
				
				return queryService
						.getById(id)
						.map(mapper::fromDomainToDto)
						.flatMap( dto -> ServerResponse.ok().bodyValue(dto))
						.switchIfEmpty(Mono.error(new EmptyResponseException(Map.of(
				                serverRequest.path(), GlobalConstants.MSG_NODATA
				            ))))
						.onErrorResume(errorHandler::handleException);
				
			} catch (IllegalArgumentException error) {
					
	            return errorHandler.handleException(new InvalidUUIDException(Map.of(GlobalConstants.SHIPCLASS_ID, GlobalConstants.MSG_NODATA)));
	            
	        }			
		});
	}	
	
}

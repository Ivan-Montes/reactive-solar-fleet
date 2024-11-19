package dev.ime.application.service;


import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.dispatcher.CommandDispatcher;
import dev.ime.application.dto.SpacecraftDto;
import dev.ime.config.GlobalConstants;
import dev.ime.config.LoggerUtil;
import dev.ime.domain.command.Command;
import dev.ime.domain.command.CommandHandler;
import dev.ime.domain.event.Event;
import dev.ime.domain.port.outbound.PublisherPort;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

	@Mock
	private LoggerUtil loggerUtil;
	
	@Mock
	private CommandDispatcher commandDispatcher;
	
	@Mock
	private PublisherPort publisherPort;
	
	@InjectMocks
	private CommandService commandService;
	
	@Mock
	private CommandHandler commandHandler;

	private Event event;
	private SpacecraftDto spacecraftDto;	

	private final UUID eventId = UUID.randomUUID();
	private final String eventCategory = GlobalConstants.SPACECRAFT_CAT;
	private final String eventType = GlobalConstants.SPACECRAFT_DELETED;
	private final Instant eventTimestamp = Instant.now();
	private final Map<String, Object> eventData = new HashMap<>();		

	private final UUID spacecraftId = UUID.randomUUID();
	private final String spacecraftName = "";
	private final UUID shipclassId = UUID.randomUUID();

	@BeforeEach
	private void setUp() {
		
		event = new Event(
				eventId,
				eventCategory,
				eventType,
				eventTimestamp,
				eventData);			
		
		spacecraftDto = new SpacecraftDto(
				spacecraftId,
				spacecraftName,
				shipclassId);
		
	}
	

	@Test
	void create_WithRighParam_ReturnMonoEvent() {
		
		Mockito.when(commandDispatcher.getCommandHandler(Mockito.any(Command.class))).thenReturn(commandHandler);
		Mockito.when(commandHandler.handle(Mockito.any(Command.class))).thenReturn(Mono.just(event));
	
		Mono<Event> result = commandService.create(spacecraftDto);
		
		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(commandDispatcher).getCommandHandler(Mockito.any(Command.class));
		Mockito.verify(commandHandler).handle(Mockito.any(Command.class));
		
	}

	@Test
	void update_WithRighParam_ReturnMonoEvent() {
		
		Mockito.when(commandDispatcher.getCommandHandler(Mockito.any(Command.class))).thenReturn(commandHandler);
		Mockito.when(commandHandler.handle(Mockito.any(Command.class))).thenReturn(Mono.just(event));
	
		Mono<Event> result = commandService.update(spacecraftId, spacecraftDto);
		
		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(commandDispatcher).getCommandHandler(Mockito.any(Command.class));
		Mockito.verify(commandHandler).handle(Mockito.any(Command.class));
		
	}	

	@Test
	void deleteById_WithRighParam_ReturnMonoEvent() {
		
		Mockito.when(commandDispatcher.getCommandHandler(Mockito.any(Command.class))).thenReturn(commandHandler);
		Mockito.when(commandHandler.handle(Mockito.any(Command.class))).thenReturn(Mono.just(event));
	
		Mono<Event> result = commandService.deleteById(spacecraftId);
		
		StepVerifier.create(result)
		.expectNext(event)
		.verifyComplete();
		Mockito.verify(commandDispatcher).getCommandHandler(Mockito.any(Command.class));
		Mockito.verify(commandHandler).handle(Mockito.any(Command.class));
		
	}
	

}

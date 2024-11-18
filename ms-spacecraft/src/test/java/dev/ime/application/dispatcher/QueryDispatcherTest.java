package dev.ime.application.dispatcher;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import dev.ime.application.handler.GetAllSpacecraftQueryHandler;
import dev.ime.application.handler.GetByIdSpacecraftQueryHandler;
import dev.ime.application.usecase.GetAllSpacecraftQuery;
import dev.ime.domain.model.Spacecraft;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;
import reactor.core.publisher.Flux;

@ExtendWith(MockitoExtension.class)
class QueryDispatcherTest {

	@Mock
	private GetAllSpacecraftQueryHandler getAllSpacecraftQueryHandler;

	@Mock
	private GetByIdSpacecraftQueryHandler getByIdSpacecraftQueryHandler;

	@InjectMocks
	private QueryDispatcher queryDispatcher;
	
	private class TestQuery implements Query{};

	@Test
	void getQueryHandler_ByDefault_ReturnQueryHandler() {
		
		GetAllSpacecraftQuery getAllQuery = new GetAllSpacecraftQuery(PageRequest.of(0, 100));
		
		QueryHandler<Flux<Spacecraft>> handler = queryDispatcher.getQueryHandler(getAllQuery);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(handler).isNotNull()
				);
	}
	
	@Test
	void getQueryHandler_WithUnknownQuery_ThrowsException() {
		
		TestQuery testQuery = new TestQuery();
		
		Exception ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> queryDispatcher.getQueryHandler(testQuery));
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(ex).isNotNull()
				);
	}

}

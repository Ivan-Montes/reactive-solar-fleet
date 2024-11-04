package dev.ime.application.dispatcher;


import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.ime.application.handler.GetAllCrewMemberQueryHandler;
import dev.ime.application.handler.GetByIdCrewMemberQueryHandler;
import dev.ime.application.usecase.GetByIdCrewMemberQuery;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@ExtendWith(MockitoExtension.class)
class QueryDispatcherTest {

	@Mock
	private GetAllCrewMemberQueryHandler getAllQueryHandler;
	
	@Mock
	private GetByIdCrewMemberQueryHandler getByIdQueryHandler;
	
	@InjectMocks
	private QueryDispatcher queryDispatcher;	
	
	@Test
	void getQueryHandler_WithGetByIdQuery_ReturnHandler() {		
		
		QueryHandler<Object> handler = queryDispatcher.getQueryHandler(new GetByIdCrewMemberQuery(UUID.randomUUID()));
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(handler).isNotNull()
				);
	}

	@Test
	void getQueryHandler_WithUnknownQuery_ThrowsException() {		
		
		class TestQuery implements Query {};
		TestQuery testQuery = new TestQuery();
		
		Exception ex = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()-> queryDispatcher.getQueryHandler(testQuery));
				
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(ex).isNotNull()
				);
	}
	
}

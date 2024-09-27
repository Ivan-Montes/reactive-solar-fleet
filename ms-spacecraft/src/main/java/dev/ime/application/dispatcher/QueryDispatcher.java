package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handler.GetAllSpacecraftQueryHandler;
import dev.ime.application.handler.GetByIdSpacecraftQueryHandler;
import dev.ime.application.usecase.GetAllSpacecraftQuery;
import dev.ime.application.usecase.GetByIdSpacecraftQuery;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class QueryDispatcher {

	private final Map<Class<? extends Query>, QueryHandler<?>> queryHandlers = new HashMap<>();

	public QueryDispatcher(GetAllSpacecraftQueryHandler getAllQueryHandler, GetByIdSpacecraftQueryHandler getByIdQueryHandler) {
		super();
		queryHandlers.put(GetAllSpacecraftQuery.class, getAllQueryHandler);
		queryHandlers.put(GetByIdSpacecraftQuery.class, getByIdQueryHandler);		
	}

	public <U> QueryHandler<U> getQueryHandler(Query query){

		@SuppressWarnings("unchecked")
		Optional<QueryHandler<U>> optHandler = Optional.ofNullable((QueryHandler<U>)queryHandlers.get(query.getClass()));
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + query.getClass().getName()));	

	}

}

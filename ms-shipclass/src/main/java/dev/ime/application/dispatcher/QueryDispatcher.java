package dev.ime.application.dispatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import dev.ime.application.handler.GetAllShipclassQueryHandler;
import dev.ime.application.handler.GetByIdShipclassQueryHandler;
import dev.ime.application.usecase.GetAllShipclassQuery;
import dev.ime.application.usecase.GetByIdShipclassQuery;
import dev.ime.config.GlobalConstants;
import dev.ime.domain.query.Query;
import dev.ime.domain.query.QueryHandler;

@Component
public class QueryDispatcher {

	private final Map<Class<? extends Query>, QueryHandler<?>> queryHandlers = new HashMap<>();

	public QueryDispatcher(GetAllShipclassQueryHandler getAllQueryHandler, GetByIdShipclassQueryHandler getByIdQueryHandler) {
		super();
		queryHandlers.put(GetAllShipclassQuery.class, getAllQueryHandler);
		queryHandlers.put(GetByIdShipclassQuery.class, getByIdQueryHandler);		
	}

	public <U> QueryHandler<U> getQueryHandler(Query query){

		@SuppressWarnings("unchecked")
		Optional<QueryHandler<U>> optHandler = Optional.ofNullable((QueryHandler<U>)queryHandlers.get(query.getClass()));
		
		return optHandler.orElseThrow( () -> new IllegalArgumentException(GlobalConstants.MSG_HANDLER_NONE + query.getClass().getName()));	

	}

}

package dev.ime.domain.port.outbound;


import dev.ime.domain.event.Event;

public interface BaseProjectorPort {

	void create(Event event);
	void deleteById(Event event);
	
}

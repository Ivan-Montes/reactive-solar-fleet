package dev.ime.domain.port.outbound;

import dev.ime.domain.event.Event;

public interface ExtendedProjectorPort {

	void update(Event event);
	
}

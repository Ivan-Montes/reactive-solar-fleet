package dev.ime.domain.port.outbound;

import dev.ime.domain.event.Event;

public interface PublisherPort {

	void publishEvent(Event event);
	
}

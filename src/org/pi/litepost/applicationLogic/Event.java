package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;

/**
 * @author Julia Moos
 *
 */
public class Event extends Post {
	private final LocalDateTime eventDate;

	public Event(int postId, String title, String text, String contact,
			LocalDateTime date, User user, boolean reported, boolean presentation, LocalDateTime eventDate) {
		super(postId, title, text, contact, date, user, reported, presentation);
		this.eventDate = eventDate;
	}

	public LocalDateTime getEventDate() {
		return eventDate;
	}
	
	@Override
	public boolean isEvent() {
		return true;
	}
}

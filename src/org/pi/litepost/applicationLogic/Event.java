package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;

/**
 * @author Julia Moos
 *
 */
public class Event extends Post {
	private final LocalDateTime eventDate;

	public Event(int postId, String title, String text, String contact,
			LocalDateTime date, int userId, boolean reported, boolean presentation, LocalDateTime eventDate) {
		super(postId, title, text, contact, date, userId, reported, presentation);
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

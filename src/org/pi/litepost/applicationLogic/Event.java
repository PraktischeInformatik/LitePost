package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;

/**
 * @author Julia Moos
 *
 */
public class Event extends Post {
	private LocalDateTime eventDate;

	public Event(int postId, String title, String text, String contact,
			LocalDateTime date, int userId, LocalDateTime eventDate) {
		super(postId, title, text, contact, date, userId);
		this.eventDate = eventDate;
	}

	public LocalDateTime getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDateTime eventDate) {
		this.eventDate = eventDate;
	}

	public boolean isEvent() {
		return true;
	}
}

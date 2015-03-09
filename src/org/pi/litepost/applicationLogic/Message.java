package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Julia Moos
 *
 */
public class Message {
	protected final static String STANDARD_DATE_FORMAT = "dd. MMMM uuuu HH:mm";
	private final int messageId;
	private final LocalDateTime date;
	private final User sender;
	private final User receiver;
	private final boolean outgoing;
	private final String subject;
	private final String text;
	private final boolean hidden;
	private final boolean read;

	public Message(int messageId, LocalDateTime date, User sender, User receiver,
			boolean outgoing, String subject, String text, boolean hidden, boolean read) {
		this.messageId = messageId;
		this.date = date;
		this.sender = sender;
		this.receiver = receiver;
		this.outgoing = outgoing;
		this.subject = subject;
		this.text = text;
		this.hidden = hidden;
		this.read = read;
	}

	public int getMessageId() {
		return messageId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public User getSender() {
		return sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public String getSubject() {
		return subject;
	}

	public String getText() {
		return text;
	}

	public boolean isHidden() {
		return hidden;
	}

	public boolean isRead() {
		return read;
	}
	
	public boolean isOutgoing() {
		return outgoing;
	}
	
	public String formatDate() {
		return formatDate(STANDARD_DATE_FORMAT);
	}
	
	public String formatDate(String pattern) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		return format.format(getDate());
	}
}

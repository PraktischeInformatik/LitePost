package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;

/**
 * @author Julia Moos
 *
 */
public class Message {
	private final int messageId;
	private final LocalDateTime date;
	private final int sender;
	private final int receiver;
	private final String subject;
	private final String text;
	private final boolean hidden;
	private final boolean read;

	public Message(int messageId, LocalDateTime date, int sender, int receiver,
			String subject, String text, boolean hidden, boolean read) {
		this.messageId = messageId;
		this.date = date;
		this.sender = sender;
		this.receiver = receiver;
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

	public int getSender() {
		return sender;
	}

	public int getReceiver() {
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
}

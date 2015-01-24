package applicationLogic;

import java.time.LocalDateTime;

public class Message {
	private int messageId;
	private LocalDateTime date;
	private int sender;
	private int receiver;
	private String subject;
	private String text;
	private boolean hidden = false;
	private boolean read = false;

	public Message(int messageId, LocalDateTime date, int sender, int receiver,
			String subject, String text) {
		this.messageId = messageId;
		this.date = date;
		this.sender = sender;
		this.receiver = receiver;
		this.subject = subject;
		this.text = text;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public int getSender() {
		return sender;
	}

	public void setSender(int sender) {
		this.sender = sender;
	}

	public int getReceiver() {
		return receiver;
	}

	public void setReceiver(int receiver) {
		this.receiver = receiver;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = true;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = true;
	}
}

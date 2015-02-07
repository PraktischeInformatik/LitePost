package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

/**
 * the MessageManager
 * 
 * @author Julia Moos
 *
 */
public class MessageManager extends Manager {

	/**
	 * sends a Message (creates a new Message-Object) and saves it in the
	 * Database; the messageId is taken from the corresponding id-table, the
	 * Date is taken from the CalenderManager
	 * 
	 * @param sender
	 * @param receiver
	 * @param subject
	 * @param content
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 */
	public int insert(String receiver, String subject, String content)
			throws SQLException, DatabaseCriticalErrorException {
		LocalDateTime date = this.model.getCalenderManager().getDate();
		String sender = "Anonym";
		if (this.model.getSessionManager().exists("username")) {
			sender = model.getUserManager().getActual().getUsername();
		}
		this.model.getQueryManager().executeQuery("insertMessage", date,
				sender, receiver, subject, content);
		return model.getQueryManager().getLastInsertId();
	}

	/**
	 * returns an ArrayList containing all Messages of the actual User
	 * 
	 * @return
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 */
	@SuppressWarnings("null")
	public ArrayList<Message> getByUser() throws SQLException,
			DatabaseCriticalErrorException {
		int userId = 0;
		if (this.model.getSessionManager().exists("username")) {
			userId = model.getUserManager().getActual().getUserId();
		}
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getMessagesByUser", userId);

		ArrayList<Message> messages = null;
		int messageId;
		Timestamp ldate;
		LocalDateTime date;
		int sender;
		int receiver;
		String subject;
		String content;
		boolean hidden;
		boolean read;
		Message lmessage;
		while (result.next()) {
			messageId = result.getInt("message_id");
			ldate = result.getTimestamp("date");
			date = ldate.toLocalDateTime();
			;
			sender = result.getInt("sender");
			receiver = result.getInt("receiver");
			subject = result.getString("subject");
			content = result.getString("content");
			hidden = result.getBoolean("hidden");
			read = result.getBoolean("read");
			lmessage = new Message(messageId, date, sender, receiver, subject,
					content);
			if (hidden)
				lmessage.isHidden();
			if (read)
				lmessage.isRead();

			messages.add(lmessage);
		}
		return messages;
	}

	/**
	 * returns a Message with the given id
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 */
	public Message getById(int id) throws SQLException,
			DatabaseCriticalErrorException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getMessagesById", id);

		Timestamp ldate;
		LocalDateTime date;
		int sender;
		int receiver;
		String subject;
		String content;
		boolean hidden;
		boolean read;
		Message lmessage;

		ldate = result.getTimestamp("date");
		date = ldate.toLocalDateTime();
		sender = result.getInt("sender");
		receiver = result.getInt("receiver");
		subject = result.getString("subjct");
		content = result.getString("content");
		hidden = result.getBoolean("hidden");
		read = result.getBoolean("read");
		lmessage = new Message(id, date, sender, receiver, subject, content);
		if (hidden)
			lmessage.isHidden();
		if (read)
			lmessage.isRead();

		return lmessage;
	}

	/**
	 * deletes the message with given id
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 */
	public void delete(String id) throws SQLException,
			DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("deleteMessage", id);
	}
}

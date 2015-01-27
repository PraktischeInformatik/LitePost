package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
	public void insert(String sender, String receiver, String subject,
			String content) throws SQLException, DatabaseCriticalErrorException {
		LocalDateTime date = this.model.getCalenderManager().getDate();
		this.model.getQueryManager().executeQuery("insertMessage", date,
				sender, receiver, subject, content);
	}

	/**
	 * returns an ArrayList containing all Messages of the given User
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 */
	@SuppressWarnings("null")
	public ArrayList<Message> getByUser(int userId) throws SQLException,
			DatabaseCriticalErrorException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getMessagesByUser", userId);

		ArrayList<Message> messages = null;
		int messageId;
		long ldate;
		Instant i;
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
			ldate = result.getDate("date").getTime();
			i = Instant.ofEpochMilli(ldate);
			date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
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

		long ldate;
		Instant i;
		LocalDateTime date;
		int sender;
		int receiver;
		String subject;
		String content;
		boolean hidden;
		boolean read;
		Message lmessage;

		ldate = result.getDate("date").getTime();
		i = Instant.ofEpochMilli(ldate);
		date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
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

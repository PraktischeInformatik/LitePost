package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

/**
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
	 * @param text
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 */
	public void insert(String sender, String receiver, String subject,
			String text) throws SQLException, DatabaseCriticalErrorException {
		LocalDateTime date = this.model.getCalenderManager().getDate();
		this.model.getQueryManager().executeQuery("insertMessage", date,
				sender, receiver, subject, text);
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
		String text;
		boolean hidden;
		boolean read;
		Message lmessage;
		while (result.next()) {
			messageId = result.getInt(1);
			ldate = result.getDate(2).getTime();
			i = Instant.ofEpochMilli(ldate);
			date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
			sender = result.getInt(3);
			receiver = result.getInt(4);
			subject = result.getString(5);
			text = result.getString(6);
			hidden = result.getBoolean(7);
			read = result.getBoolean(8);
			lmessage = new Message(messageId, date, sender, receiver, subject,
					text);
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
		String text;
		boolean hidden;
		boolean read;
		Message lmessage;

		ldate = result.getDate(2).getTime();
		i = Instant.ofEpochMilli(ldate);
		date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
		sender = result.getInt(3);
		receiver = result.getInt(4);
		subject = result.getString(5);
		text = result.getString(6);
		hidden = result.getBoolean(7);
		read = result.getBoolean(8);
		lmessage = new Message(id, date, sender, receiver, subject, text);
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

package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
	 */
	public void insert(String receiver, String subject, String content) throws SQLException{
		LocalDateTime date = this.model.getCalenderManager().getDate();
		String sender = "Anonym";
		if (this.model.getSessionManager().exists("username")) {
			sender = model.getUserManager().getCurrent().getUsername();
		}
		this.model.getQueryManager().executeQuery("insertMessage", date,
				sender, receiver, subject, content);
	}

	/**
	 * returns an ArrayList containing all Messages of the actual User
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Message> getByUser() throws SQLException{
		int userId = 0;
		if (this.model.getSessionManager().exists("username")) {
			userId = model.getUserManager().getCurrent().getUserId();
		}
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getMessagesByUser", userId);

		ArrayList<Message> messages = new ArrayList<>();
		while (result.next()) {
			messages.add(createMessage(result));
		}
		return messages;
	}

	/**
	 * returns a Message with the given id
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Message getById(int id) throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getMessagesById", id);
		if(result.next()) {
			return createMessage(result);
		} else {
			return null;
		}
	}
	
	public Message createMessage(ResultSet rs) throws SQLException {
		int messageId = rs.getInt("message_id");
		LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
		int sender = rs.getInt("sender");
		int receiver = rs.getInt("receiver");
		String subject = rs.getString("subject");
		String content = rs.getString("content");
		boolean hidden = rs.getBoolean("hidden");
		boolean read = rs.getBoolean("read");
		Message message = new Message(messageId, date, sender, receiver, subject,
				content, hidden, read);
		return message;
	}

	/**
	 * deletes the message with given id
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void delete(String id) throws SQLException{
		this.model.getQueryManager().executeQuery("deleteMessage", id);
	}
}

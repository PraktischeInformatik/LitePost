package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.pi.litepost.exceptions.ForbiddenOperationException;

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
	public int insert(User sender, User receiver, String subject, String content) throws SQLException{
		LocalDateTime date = LocalDateTime.now(clock);
		
		model.getQueryManager().executeQuery("insertMessage", date,
				sender.getUserId(), receiver.getUserId(), true, subject, content);
		ResultSet rs = model.getQueryManager().executeQuery("insertMessage", date,
				sender.getUserId(), receiver.getUserId(), false, subject, content);
		return rs.getInt(1);
	}

	/**
	 * returns an ArrayList containing all Messages a User has sent
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Message> getFromUser(User user) throws SQLException{
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getMessagesFromUser", user.getUserId());

		ArrayList<Message> messages = new ArrayList<>();
		while (result.next()) {
			messages.add(createMessage(result));
		}
		return messages;
	}
	
	/**
	 * returns an ArrayList containing all Messages a User has received
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Message> getToUser(User user) throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getMessagesToUser", user.getUserId());

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
				"getMessageById", id);
		if(result.next()) {
			return createMessage(result);
		} else {
			return null;
		}
	}
	

	/**
	 * marks a message as read
	 * 
	 * @param message_id
	 * @return
	 * @throws SQLException
	 */	
	public void readMessage(int message_id) throws SQLException {	
		model.getQueryManager().executeQuery("readMessage", message_id);
	}
	
	/**
	 * deletes a message as read
	 * 
	 * @param message_id
	 * @return
	 * @throws SQLException
	 * @throws ForbiddenOperationException 
	 */	
	public void deleteMessage(int message_id) throws SQLException, ForbiddenOperationException {
		User user = model.getUserManager().getCurrent();
		Message msg = getById(message_id);
		User msgUser = msg.isOutgoing() ? msg.getSender() : msg.getReceiver();
		if(user.getUserId() != msgUser.getUserId() && !user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
		model.getQueryManager().executeQuery("deleteMessage", message_id);
	}
	
	public Message createMessage(ResultSet rs) throws SQLException {
		int messageId = rs.getInt("message_id");
		LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
		int senderId = rs.getInt("sender");
		int receiverId = rs.getInt("receiver");
		boolean outgoing = rs.getBoolean("outgoing");
		String subject = rs.getString("subject");
		String content = rs.getString("content");
		boolean hidden = rs.getBoolean("hidden");
		boolean read = rs.getBoolean("read");
		
		User sender = model.getUserManager().getById(senderId);
		User receiver = model.getUserManager().getById(receiverId);
		Message message = new Message(messageId, date, sender, receiver, outgoing, subject,
				content, hidden, read);
		return message;
	}

}

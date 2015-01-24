package applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import databaseAccess.DatabaseDeleteMessage;
import databaseAccess.DatabaseGetMessageByUser;
import databaseAccess.DatabaseGetMessageId;
import databaseAccess.DatabaseInsertComment;
import databaseAccess.IllegalParameterLengthException;

public class MessageManager extends Manager {

	/**
	 * sends a Message (creates a new Message-Object) and saves it in the
	 * Database; the messageId is taken from the corresponding id-table, the
	 * Date is taken from the CalenderManager
	 * 
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @throws IllegalParameterLengthException
	 * @throws SQLException
	 */
	public void insert(String sender, String receiver, String subject,
			String text) throws SQLException, IllegalParameterLengthException {
		int messageId;
		LocalDateTime date = this.model.getCalenderManager().getDate();
		String sdate = date.toString();
		DatabaseGetMessageId sqlId = new DatabaseGetMessageId();
		ResultSet result = sqlId.execute();
		messageId = result.getInt(1);
		String sid = String.valueOf(messageId);

		DatabaseInsertComment sqlComment = new DatabaseInsertComment();
		sqlComment.execute(sid, sdate, sender, receiver, subject, text);
	}

	/**
	 * returns an ArrayList containing all Messages of the given User
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	@SuppressWarnings("null")
	public ArrayList<Message> getByUser(String sUserId) throws SQLException,
			IllegalParameterLengthException {
		DatabaseGetMessageByUser sqlGet = new DatabaseGetMessageByUser();
		ResultSet result = sqlGet.execute(sUserId);

		ArrayList<Message> messages = null;
		int messageId;
		String sDate;
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
			sDate = result.getString(2);
			date = LocalDateTime.parse(sDate);
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
	 * @throws IllegalParameterLengthException
	 */
	public Message getById(String sId) throws SQLException,
			IllegalParameterLengthException {
		DatabaseGetMessageByUser sqlGet = new DatabaseGetMessageByUser();
		ResultSet result = sqlGet.execute(sId);

		int messageId;
		String sDate;
		LocalDateTime date;
		int sender;
		int receiver;
		String subject;
		String text;
		boolean hidden;
		boolean read;
		Message lmessage;

		messageId = result.getInt(1);
		sDate = result.getString(2);
		date = LocalDateTime.parse(sDate);
		sender = result.getInt(3);
		receiver = result.getInt(4);
		subject = result.getString(5);
		text = result.getString(6);
		hidden = result.getBoolean(7);
		read = result.getBoolean(8);
		lmessage = new Message(messageId, date, sender, receiver, subject, text);
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
	 * @throws IllegalParameterLengthException
	 * @throws SQLException
	 */
	public void delete(String id) throws SQLException,
			IllegalParameterLengthException {
		DatabaseDeleteMessage sqlDelete = new DatabaseDeleteMessage();
		sqlDelete.execute(id);
	}
}

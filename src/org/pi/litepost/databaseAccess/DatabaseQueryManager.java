package org.pi.litepost.databaseAccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DatabaseQueryManager {
	DatabaseConnector databaseConnector;
	HashMap<String, DatabaseQuery> databaseQueries;
	
	public DatabaseQueryManager(DatabaseConnector databaseConnector) {
		this.databaseConnector = databaseConnector;
		databaseQueries = new HashMap<String, DatabaseQuery>();

		// Comments:
		databaseQueries.put("deleteComment", new DatabaseQuery(false,
				"DELETE FROM Comments WHERE comment_id = ?"));
		databaseQueries.put("resetCommentsByUser", new DatabaseQuery(false,
				"UPDATE Comments SET user_id = 0 WHERE user_id = ?"));
		databaseQueries.put("getComment", new DatabaseQuery(true,
				"SELECT * FROM Comments WHERE comment_id = ?"));
		databaseQueries.put("getCommentsByPost", new DatabaseQuery(true,
				"SELECT * FROM Comments WHERE post_id = ? and parent_id = 0"));
		databaseQueries.put("insertComment", new DatabaseQuery(false,
				"INSERT INTO Comments(comment_id, user_id, content, date, parent_id, post_id) VALUES(?, ?, ?, ?, ?, ?)",
				"Comments"));
		databaseQueries.put("updateComment", new DatabaseQuery(false,
				"UPDATE Comments SET text = ? WHERE comment_id = ?"));
		databaseQueries.put("reportComment", new DatabaseQuery(false,
				"UPDATE comments SET reported = 1"));
		databaseQueries.put("getCommentsByParentId", new DatabaseQuery(true,
				"SELECT * FROM Comments WHERE parent_id = ?"));
		databaseQueries.put("getReportedComments", new DatabaseQuery(true,
				"SELECT * FROM Comments WHERE reported = 1"));

		// Messages:
		databaseQueries.put("deleteMessage", new DatabaseQuery(false,
				"DELETE FROM Messages WHERE message_id = ?"));
		databaseQueries.put("getMessagesFromUser", new DatabaseQuery(true,
				"SELECT * FROM Messages WHERE sender = ? AND outgoing = 1 ORDER BY date DESC"));
		databaseQueries.put("getMessagesToUser", new DatabaseQuery(true,
				"SELECT * FROM Messages WHERE receiver = ? AND outgoing = 0 ORDER BY date DESC"));
		databaseQueries.put("insertMessage", new DatabaseQuery(false,
				"INSERT INTO Messages(message_id, date, sender, receiver, outgoing, subject, content) VALUES(?, ?, ?, ?, ?, ?, ?)",
				"Messages"));
		databaseQueries.put("getMessageById", new DatabaseQuery(true,
				"SELECT * FROM Messages WHERE message_id = ?"));
		databaseQueries.put("readMessage", new DatabaseQuery(false,
				"UPDATE Messages SET read = 1 WHERE message_id = ?"));
		databaseQueries.put("deleteMessagesFromUser", new DatabaseQuery(false,
				"DELEET FROM Messages WHERE outgoing = 1 AND sender = ?"));
		databaseQueries.put("deleteMessagesToUser", new DatabaseQuery(false,
				"DELETE FROM Messages WHERE outgoing = 0 AND receiver = ?"));
		
		// Posts:
		databaseQueries.put("deletePost", new DatabaseQuery(false,
				"DELETE FROM Posts WHERE post_id = ?"));
		databaseQueries.put("resetPostsByUser", new DatabaseQuery(false,
				"UPDATE Posts SET user_id = 0 WHERE user_id = ?"));
		databaseQueries.put("getReportPost", new DatabaseQuery(true,
				"SELECT * FROM Posts WHERE reported = 1"));
		databaseQueries.put("insertPost", new DatabaseQuery(false,
				"INSERT INTO Posts(post_id, title, content, date, contact, user_id, reported, presentation) VALUES(?, ?, ?, ?, ?, ?, 0, 0)",
				"Posts"));
		// TODO funktioniert das so?
		databaseQueries.put("deleteOldPosts", new DatabaseQuery(false,
				"DELETE FROM Posts WHERE date < ?"));
		databaseQueries.put("updatePost", new DatabaseQuery(false,
				"UPDATE Comments SET title = ?, content = ?, contact = ? WHERE comment_id = ?"));
		databaseQueries.put("getAllPosts", new DatabaseQuery(true,
				"SELECT * FROM Posts"));
		databaseQueries.put("getPostById", new DatabaseQuery(true,
				"SELECT * FROM Posts WHERE post_id = ?"));
		databaseQueries.put("getPostByUser", new DatabaseQuery(true,
				"SELECT * FROM Posts WHERE user_id = ?"));
		databaseQueries.put("reportPost", new DatabaseQuery(false,
				"UPDATE posts SET reported = 1"));
		databaseQueries.put("getReportedPosts", new DatabaseQuery(true,
				"SELECT * FROM posts WHERE reported = 1"));
		databaseQueries.put("searchPosts", new DatabaseQuery(true,
				"SELECT * FROM posts WHERE content LIKE ? OR title LIKE ?"));

		// Events:
		databaseQueries.put("getEventsAfter", new DatabaseQuery(true,
				"SELECT * FROM Posts NATURAL JOIN Events WHERE event_date >= ?"));
		databaseQueries.put("getEventsBetween", new DatabaseQuery(true,
				"SELECT * FROM Posts NATURAL JOIN Events WHERE event_date > ? and event_date < ?"));
		databaseQueries.put("makeEvent", new DatabaseQuery(false,
				"INSERT INTO events(event_id, post_id, event_date) VALUES(?, ?, ?)",
				"Events"));
		databaseQueries.put("getEventForPost", new DatabaseQuery(true,
				"SELECT * FROM Events WHERE post_id = ?"));
		
		// Images:
		databaseQueries.put("getImagesByPost",new DatabaseQuery(true,
				"SELECT * FROM Images WHERE image_id in (SELECT image_id FROM Post_has_Images WHERE post_id = ?)"));
		databaseQueries.put("insertImage",new DatabaseQuery(false,
				"INSERT INTO Images(image_id, source) VALUES(?, ?)",
				"Images"));
		databaseQueries.put("addImageToPost",new DatabaseQuery(false,
				"INSERT INTO Post_has_Images(image_id, post_id) VALUES(?, ?)"));
		

		// User:
		databaseQueries.put("deleteUser", new DatabaseQuery(false,
				"DELETE FROM Users WHERE user_id = ?"));
		databaseQueries.put("checkUserData", new DatabaseQuery(true,
				"SELECT * FROM Users WHERE username = ? or email = ?"));
		databaseQueries.put("insertUser", new DatabaseQuery(false, 
				"INSERT INTO Users(user_id, username, password, firstname, lastname, email) VALUES(?, ?, ?, ?, ?, ?)",
				"Users"));
		databaseQueries.put("checkUser", new DatabaseQuery(true,
				"SELECT * FROM Users WHERE username = ?, password = ?"));
		databaseQueries.put("updateUser", new DatabaseQuery(false,
				"UPDATE Users SET password = ?, firstname = ?, lastname = ? WHERE user_id = ?"));
		databaseQueries.put("verifyEmail", new DatabaseQuery(false,
				"UPDATE Users SET verified_email = 1 WHERE user_id = ?"));
		databaseQueries.put("getUserByUsername", new DatabaseQuery(true,
				"SELECT * FROM Users WHERE username = ?"));
		databaseQueries.put("getUserById", new DatabaseQuery(true,
				"SELECT * FROM Users WHERE user_id = ?"));
		databaseQueries.put("getUserByEmail", new DatabaseQuery(true,
				"SELECT * FROM Users WHERE email = ?"));
		databaseQueries.put("setAdmin", new DatabaseQuery(false,
				"UPDATE Users SET admin = 1"));

		// Session
		databaseQueries.put("endSession", new DatabaseQuery(false,
				"DELETE FROM Sessions WHERE session_id = ?"));
		databaseQueries.put("setSessionVar", new DatabaseQuery(false,
				"REPLACE INTO Sessions(session_id, key, value) VALUES(?, ?, ?)"));
		databaseQueries.put("getSessionVar", new DatabaseQuery(true,
				"SELECT value FROM Sessions WHERE session_id = ? and key = ?"));
		databaseQueries.put("sessionKeyExists",new DatabaseQuery(true,
				"SELECT Count(*) FROM Sessions WHERE session_id = ? and key = ?"));
		databaseQueries.put("getAllSessions", new DatabaseQuery(true,
				"SELECT * FROM Sessions WHERE key = \"expiration\""));
		databaseQueries.put("removeSession", new DatabaseQuery(false,
				"DELETE FROM Sessions WHERE session_id = ?"));
		
		// Tokens
		databaseQueries.put("setEmailVerificationToken", new DatabaseQuery(false,
				"REPLACE INTO Tokens(user_id, type, value, creation_time) VALUES(?, \"email_verification\", ?, datetime('now'))"));
		databaseQueries.put("setPasswordResetToken", new DatabaseQuery(false,
				"REPLACE INTO Tokens(user_id, type, value, creation_time) VALUES(?, \"password_reset\", ?, datetime('now'))"));
		databaseQueries.put("getEmailVerificationToken", new DatabaseQuery(true,
				"SELECT * FROM Tokens WHERE value = ? AND type = \"email_verification\" AND creation_time > datetime('now', '-24 hours')"));
		databaseQueries.put("getPasswordResetToken", new DatabaseQuery(true,
				"SELECT * FROM Tokens WHERE value = ? AND type = \"password_reset\" AND creation_time > datetime('now', '-24 hours')"));
		databaseQueries.put("deleteEmailVerificationToken", new DatabaseQuery(false,
				"DELETE FROM Tokens WHERE value = ? AND type = \"email_verification\""));
		databaseQueries.put("deletePasswordResetToken", new DatabaseQuery(false,
				"DELETE FROM Tokens WHERE value = ? AND type = \"password_reset\""));
		databaseQueries.put("removeOldTokens", new DatabaseQuery(false,
				"DELETE FROM Tokens WHERE creation_time < datetime('now' '-24 hours')"));
		
		// Ids:
		databaseQueries.put("getIdByTableName", new DatabaseQuery(true,
				"SELECT next_id FROM Ids WHERE table_name = ?"));
		databaseQueries.put("incrementId", new DatabaseQuery(false,
				"UPDATE Ids SET next_Id = next_Id + 1 WHERE table_name = ?"));
		databaseQueries.put("getLastId", new DatabaseQuery(true,
				"SELECT next_id - 1 FROM Ids WHERE table_name = ?"));
	}

	public ResultSet executeQuery(String queryName, Object... values)
			throws SQLException {
		DatabaseQuery query = databaseQueries.get(queryName);
		if(query == null) {
			throw new SQLException(String.format("no query with name %s found!", queryName));
		}
		return query.execute(this, databaseConnector, values);
	}
}

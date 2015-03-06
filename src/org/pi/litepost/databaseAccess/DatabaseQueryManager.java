package org.pi.litepost.databaseAccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class DatabaseQueryManager {
	DatabaseConnector databaseConnector;
	HashMap<String, DatabaseQuery> databaseQueries;
	
	@SuppressWarnings("serial")
	public DatabaseQueryManager(DatabaseConnector databaseConnector) {
		this.databaseConnector = databaseConnector;
		databaseQueries = new HashMap<String, DatabaseQuery>(){{
			put("deleteComment", new DatabaseQuery(
					"DELETE FROM Comments WHERE comment_id = ?",
					"DELETE FROM Comments WHERE parent_id = ?"));
			put("deleteCommentContent", new DatabaseQuery(
					"UPDATE Comments set content = '[gelöscht]', user_id = 0 WHERE comment_id = ?"));
//			put("deleteCommentsFromPost", new DatabaseQuery(
//					"DELETE FROM Comments WHERE post_id = ?"));
			put("getComment", new DatabaseQuery(
					"SELECT * FROM Comments WHERE comment_id = ?"));
			put("getCommentsByPost", new DatabaseQuery(
					"SELECT * FROM Comments WHERE post_id = ? and parent_id = 0"));
			put("insertComment", new DatabaseQuery(
					"INSERT INTO Comments(comment_id, user_id, content, date, parent_id, post_id) VALUES(?, ?, ?, ?, ?, ?)")
					.autoIncrement("Comments"));
			put("updateComment", new DatabaseQuery(
					"UPDATE Comments SET text = ? WHERE comment_id = ?"));
			put("reportComment", new DatabaseQuery(
					"UPDATE comments SET reported = 1"));
			put("getCommentsByParentId", new DatabaseQuery(
					"SELECT * FROM Comments WHERE parent_id = ?"));
			put("getReportedComments", new DatabaseQuery(
					"SELECT * FROM Comments WHERE reported = 1"));
			put("getCommentsByUser", new DatabaseQuery(
					"SELECT * FROM Comments WHERE user_id = ?"));
			put("getAllComments", new DatabaseQuery(
					"SELECT * FROM Comments"));

			// Messages:
			put("deleteMessage", new DatabaseQuery(
					"DELETE FROM Messages WHERE message_id = ?"));
			put("getMessagesFromUser", new DatabaseQuery(
					"SELECT * FROM Messages WHERE sender = ? AND outgoing = 1 ORDER BY date DESC"));
			put("getMessagesToUser", new DatabaseQuery(
					"SELECT * FROM Messages WHERE receiver = ? AND outgoing = 0 ORDER BY date DESC"));
			put("insertMessage", new DatabaseQuery(
					"INSERT INTO Messages(message_id, date, sender, receiver, outgoing, subject, content) VALUES(?, ?, ?, ?, ?, ?, ?)")
			      	.autoIncrement("Messages"));
			put("getMessageById", new DatabaseQuery(
					"SELECT * FROM Messages WHERE message_id = ?"));
			put("readMessage", new DatabaseQuery(
					"UPDATE Messages SET read = 1 WHERE message_id = ?"));

			// Posts:
			put("deletePost", new DatabaseQuery(
					"DELETE FROM Posts WHERE post_id = ?",
					"DELETE FROM Comments WHERE post_id = ?"
					));
			put("getReportedPosts", new DatabaseQuery(
					"SELECT * FROM Posts WHERE reported = 1"));
			put("insertPost", new DatabaseQuery(
					"INSERT INTO Posts(post_id, title, content, date, contact, user_id, reported, presentation) VALUES(?, ?, ?, ?, ?, ?, 0, 0)")
					.autoIncrement("Posts"));
			put("deleteOldPosts", new DatabaseQuery(
					"DELETE FROM Posts WHERE date < ? AND post_id IN ("
					+ "SELECT Posts.post_id FROM Posts LEFT JOIN Events ON Events.post_id = Posts.post_id WHERE event_id is NULL"
					+ ")"));
			put("updatePost", new DatabaseQuery(
					"UPDATE Comments SET title = ?, content = ?, contact = ? WHERE comment_id = ?"));
			put("getAllPosts", new DatabaseQuery(
					"SELECT * FROM Posts"));
			put("getPostById", new DatabaseQuery(
					"SELECT * FROM Posts WHERE post_id = ?"));
			put("getPostsByUser", new DatabaseQuery(
					"SELECT * FROM Posts WHERE user_id = ?"));
			put("reportPost", new DatabaseQuery(
					"UPDATE posts SET reported = 1 WHERE post_id = ?"));
			put("unblockPost", new DatabaseQuery(
					"UPDATE posts SET reported = 0 WHERE post_id = ?"));
			put("getReportedPosts", new DatabaseQuery(
					"SELECT * FROM posts WHERE reported = 1"));
			put("searchPosts", new DatabaseQuery(
					"SELECT * FROM posts WHERE content LIKE ? OR title LIKE ?"));

			// Events:
			put("getEventsAfter", new DatabaseQuery(
					"SELECT * FROM Posts NATURAL JOIN Events WHERE event_date >= ?"));
			put("getEventsBetween", new DatabaseQuery(
					"SELECT * FROM Posts NATURAL JOIN Events WHERE event_date > ? and event_date < ?"));
			put("makeEvent", new DatabaseQuery(
					"INSERT INTO events(event_id, post_id, event_date) VALUES(?, ?, ?)")
					.autoIncrement("Events"));
			put("getEventForPost", new DatabaseQuery(
					"SELECT * FROM Events WHERE post_id = ?"));
			put("deleteOldEvents", new DatabaseQuery(
					"DELETE FROM Posts WHERE post_id IN ("
						+ "SELECT Posts.post_id FROM Posts LEFT JOIN Events ON Events.post_id = Posts.post_id WHERE event_date < ?)",
					"DELETE FROM Events WHERE event_date < ?"));

			// Images:
			put("getImagesByPost",new DatabaseQuery(
					"SELECT * FROM Images WHERE image_id in (SELECT image_id FROM Post_has_Images WHERE post_id = ?)"));
			put("insertImage",new DatabaseQuery(
					"INSERT INTO Images(image_id, source) VALUES(?, ?)")
					.autoIncrement("Images"));
			put("addImageToPost",new DatabaseQuery(
					"INSERT INTO Post_has_Images(image_id, post_id) VALUES(?, ?)"));


			// User:
			put("deleteUser", new DatabaseQuery(
					"DELETE FROM Users WHERE user_id = ?",
					"DELETE FROM Posts WHERE user_id = ?",
					"DELETE FROM Comments WHERE user_id = ?",
					"DELETE FROM Messages WHERE outgoing = 1 AND sender = ?",
					"DELETE FROM Messages WHERE outgoing = 0 AND receiver = ?"));
			put("checkUserData", new DatabaseQuery(
					"SELECT * FROM Users WHERE username = ? or email = ?"));
			put("insertUser", new DatabaseQuery(
					"INSERT INTO Users(user_id, username, password, firstname, lastname, email) VALUES(?, ?, ?, ?, ?, ?)")
					.autoIncrement("Users"));
			put("checkUser", new DatabaseQuery(
					"SELECT * FROM Users WHERE username = ?, password = ?"));
			put("updateUser", new DatabaseQuery(
					"UPDATE Users SET password = ?, firstname = ?, lastname = ? WHERE user_id = ?"));
			put("verifyEmail", new DatabaseQuery(
					"UPDATE Users SET verified_email = 1 WHERE user_id = ?"));
			put("getUserByUsername", new DatabaseQuery(
					"SELECT * FROM Users WHERE username = ?"));
			put("getUserById", new DatabaseQuery(
					"SELECT * FROM Users WHERE user_id = ?"));
			put("getUserByEmail", new DatabaseQuery(
					"SELECT * FROM Users WHERE email = ?"));
			put("setAdmin", new DatabaseQuery(
					"UPDATE Users SET admin = 1 WHERE user_id = ?"));
			put("getAllUsers", new DatabaseQuery(
					"SELECT * FROM Users"));

			// Session
			put("endSession", new DatabaseQuery(
					"DELETE FROM Sessions WHERE session_id = ?"));
			put("setSessionVar", new DatabaseQuery(
					"REPLACE INTO Sessions(session_id, key, value) VALUES(?, ?, ?)"));
			put("getSessionVar", new DatabaseQuery(
					"SELECT value FROM Sessions WHERE session_id = ? and key = ?"));
			put("sessionKeyExists",new DatabaseQuery(
					"SELECT Count(*) FROM Sessions WHERE session_id = ? and key = ?"));
			put("getAllSessions", new DatabaseQuery(
					"SELECT * FROM Sessions WHERE key = \"expiration\""));
			put("removeSession", new DatabaseQuery(
					"DELETE FROM Sessions WHERE session_id = ?"));

			// Tokens
			put("setEmailVerificationToken", new DatabaseQuery(
					"REPLACE INTO Tokens(user_id, type, value, creation_time) VALUES(?, \"email_verification\", ?, datetime('now'))"));
			put("setPasswordResetToken", new DatabaseQuery(
					"REPLACE INTO Tokens(user_id, type, value, creation_time) VALUES(?, \"password_reset\", ?, datetime('now'))"));
			put("getEmailVerificationToken", new DatabaseQuery(
					"SELECT * FROM Tokens WHERE value = ? AND type = \"email_verification\" AND creation_time > datetime('now', '-24 hours')"));
			put("getPasswordResetToken", new DatabaseQuery(
					"SELECT * FROM Tokens WHERE value = ? AND type = \"password_reset\" AND creation_time > datetime('now', '-24 hours')"));
			put("deleteEmailVerificationToken", new DatabaseQuery(
					"DELETE FROM Tokens WHERE value = ? AND type = \"email_verification\""));
			put("deletePasswordResetToken", new DatabaseQuery(
					"DELETE FROM Tokens WHERE value = ? AND type = \"password_reset\""));
			put("removeOldTokens", new DatabaseQuery(
					"DELETE FROM Tokens WHERE creation_time < datetime('now' '-24 hours')"));

			// Ids:
			put("getIdByTableName", new DatabaseQuery(
					"SELECT next_id FROM Ids WHERE table_name = ?",
					"UPDATE Ids SET next_Id = next_Id + 1 WHERE table_name = ?"));
			put("getLastId", new DatabaseQuery(
					"SELECT next_id - 1 FROM Ids WHERE table_name = ?"));
		}};
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

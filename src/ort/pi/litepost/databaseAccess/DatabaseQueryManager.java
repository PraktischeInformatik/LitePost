package ort.pi.litepost.databaseAccess;

import java.sql.ResultSet;
import java.util.HashMap;

public class DatabaseQueryManager {
	DatabaseConnector databaseConnector;
	HashMap<String, DatabaseQuery> databaseQueries;

	public DatabaseQueryManager(DatabaseConnector databaseConnector) {
		this.databaseConnector = databaseConnector;
		databaseQueries = new HashMap<String, DatabaseQuery>();

		// TODO Correct all Requests
		// Comments:
		databaseQueries.put("deleteComment", new DatabaseQuery(
				"DELETE FROM Comments WHERE comment_id = ?"));
		databaseQueries.put("getComment", new DatabaseQuery(
				"SELECT * FROM Comments WHERE comment_id = ?"));
		databaseQueries.put("getCommentsByPosts", new DatabaseQuery(
				"SELECT * FROM Comments WHERE post_id = ?"));
		databaseQueries
				.put("insertComment",
						new DatabaseQuery(
								"INSERT INTO Comments VALUES(user_id = ?, content = ?, date = ?, parent_id = ?, post_id = ?)"));
		databaseQueries.put("updateComment", new DatabaseQuery(
				"UPDATE Comments SET text = ? WHERE comment_id = ?"));
		databaseQueries.put("reportComment", new DatabaseQuery(
				"UPDATE comments SET reported = 1"));
		databaseQueries.put("getCommentByParentId", new DatabaseQuery(
				"SELECT * FROM Comments WHERE parentId = ?"));
		databaseQueries.put("getReportedComments", new DatabaseQuery(
				"SELECT * FROM Comments WHERE reported = 1"));

		// Messages:
		databaseQueries.put("deleteMessage", new DatabaseQuery(
				"DELETE FROM Messages WHERE message_id = ?"));
		databaseQueries.put("getMessagesByUser", new DatabaseQuery(
				"SELECT * FROM Messages WHERE userId = ?"));
		databaseQueries
				.put("insertMessage",
						new DatabaseQuery(
								"INSERT INTO Messages VALUES(date = ?,sender = ?, receiver = ?, subject = ?, content = ?, hidden = 0, read = 0 )"));
		databaseQueries.put("getMessagesById", new DatabaseQuery(
				"SELECT * FROM Messages WHERE message_id = ?"));

		// Posts:
		databaseQueries.put("deletePost", new DatabaseQuery(
				"DELETE FROM Posts WHERE post_id = ?"));
		databaseQueries.put("getReportPost", new DatabaseQuery(
				"SELECT * FROM Posts WHERE reported = 1"));
		databaseQueries
				.put("insertPost",
						new DatabaseQuery(
								"INSERT INTO Posts VALUES(title = ?, content = ?, date = ?, contact = ?, user_id= ?)"));
		// TODO funktioniert das so?
		databaseQueries.put("deleteOldPosts", new DatabaseQuery(
				"DELETE FROM Posts WHERE date < ?"));
		databaseQueries
				.put("updatePost",
						new DatabaseQuery(
								"UPDATE Comments SET title = ?, content = ?, contact = ? WHERE comment_id = ?"));
		databaseQueries.put("getAllPosts", new DatabaseQuery(
				"SELECT * FROM Posts"));
		databaseQueries.put("getPostById", new DatabaseQuery(
				"SELECT * FROM Posts WHERE post_id = ?"));
		databaseQueries.put("getPostByUser", new DatabaseQuery(
				"SELECT * FROM Posts WHERE user_id = ?"));
		databaseQueries.put("reportPost", new DatabaseQuery(
				"UPDATE posts SET reported = 1"));
		databaseQueries.put("getReportedPosts", new DatabaseQuery(
				"SELECT * FROM posts WHERE reported = 1"));

		// Events:
		databaseQueries.put("getEvents", new DatabaseQuery(
				"SELECT * FROM Posts, Events"));

		// images
		databaseQueries
				.put("getImagesByPost",
						new DatabaseQuery(
								"SELECT * FROM Images WHERE image_id = (SELECT image_id FROM Post_has_Images WHERE post_id = ?"));

		// Ids:
		databaseQueries.put("setCommentId", new DatabaseQuery(
				"UPDATE Ids SET commentId= commentId+1"));
		databaseQueries.put("setMessageId", new DatabaseQuery(
				"UPDATE Ids SET messageId= messageId+1"));
		databaseQueries.put("setPostId", new DatabaseQuery(
				"UPDATE Ids SET postId= postId+1"));
		databaseQueries.put("setUserId", new DatabaseQuery(
				"UPDATE Ids SET userId= userId+1"));

	}

	public ResultSet executeQuery(String requestName, Object... values)
			throws DatabaseCriticalErrorException {
		DatabaseQuery databaseQuery = databaseQueries.get(requestName);
		return databaseQuery.execute(databaseConnector, values);
	}
}
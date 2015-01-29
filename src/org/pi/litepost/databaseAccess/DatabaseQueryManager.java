package org.pi.litepost.databaseAccess;

import java.sql.ResultSet;
import java.util.HashMap;

public class DatabaseQueryManager {
	DatabaseConnector databaseConnector;
	HashMap<String, DatabaseQuery> databaseQueries;

	public DatabaseQueryManager(DatabaseConnector databaseConnector) {
		this.databaseConnector = databaseConnector;
		databaseQueries = new HashMap<String, DatabaseQuery>();
		
		// Comments:
		databaseQueries.put("deleteComment", new DatabaseQuery(false, 
			"DELETE FROM Comments WHERE comment_id = ?"
		));
		databaseQueries.put("deleteCommentByUser", new DatabaseQuery(false, 
			"DELETE FROM Comments WHERE comment_id = ?"
		));
		databaseQueries.put("getComment", new DatabaseQuery(true, 
			"SELECT * FROM Comments WHERE comment_id = ?"
		));
		databaseQueries.put("getCommentsByPosts", new DatabaseQuery(true, 
			"SELECT * FROM Comments WHERE post_id = ?"
		));
		databaseQueries.put("insertComment", new DatabaseQuery(false, 
			"INSERT INTO Comments VALUES(user_id = ?,"
			+ " content = ?, date = ?, parent_id = ?, post_id = ?)"
		));
		databaseQueries.put("updateComment", new DatabaseQuery(false, 
			"UPDATE Comments SET text = ? WHERE comment_id = ?"
		));
		databaseQueries.put("reportComment", new DatabaseQuery(false, 
			"UPDATE comments SET reported = 1"
		));
		databaseQueries.put("getCommentByParentId", new DatabaseQuery(true, 
			"SELECT * FROM Comments WHERE parentId = ?"
		));
		databaseQueries.put("getReportedComments", new DatabaseQuery(true, 
			"SELECT * FROM Comments WHERE reported = 1"
		));

		// Messages:
		databaseQueries.put("deleteMessage", new DatabaseQuery(false, 
			"DELETE FROM Messages WHERE message_id = ?"
		));
		databaseQueries.put("deleteMessageByUser", new DatabaseQuery(false, 
			"DELETE FROM Messages WHERE receiver = ? OR sender = ?"
		));
		databaseQueries.put("getMessagesByUser", new DatabaseQuery(true, 
			"SELECT * FROM Messages WHERE userId = ?"
		));
		databaseQueries.put("insertMessage",new DatabaseQuery(false, 
			"INSERT INTO Messages VALUES(date = ?,sender = ?, receiver = ?,"
			+ " subject = ?, content = ?, hidden = 0, read = 0 )"
		));
		databaseQueries.put("getMessagesById", new DatabaseQuery(true, 
			"SELECT * FROM Messages WHERE message_id = ?"
		));

		// Posts:
		databaseQueries.put("deletePost", new DatabaseQuery(false, 
			"DELETE FROM Posts WHERE post_id = ?"
		));
		databaseQueries.put("deletePostByUser", new DatabaseQuery(false, 
			"DELETE FROM Posts WHERE user_id = ?"
		));
		databaseQueries.put("getReportPost", new DatabaseQuery(true, 
			"SELECT * FROM Posts WHERE reported = 1"
		));
		databaseQueries.put("insertPost", new DatabaseQuery(false, 
			"INSERT INTO Posts VALUES(title = ?, content = ?,"
			+ " date = ?, contact = ?, user_id= ?)"
		));
		// TODO funktioniert das so?
		databaseQueries.put("deleteOldPosts", new DatabaseQuery(false, 
			"DELETE FROM Posts WHERE date < ?"
		));
		databaseQueries.put("updatePost", new DatabaseQuery(false, 
			"UPDATE Comments SET title = ?,"
			+ " content = ?, contact = ? WHERE comment_id = ?"
		));
		databaseQueries.put("getAllPosts", new DatabaseQuery(true, 
			"SELECT * FROM Posts"
		));
		databaseQueries.put("getPostById", new DatabaseQuery(true, 
			"SELECT * FROM Posts WHERE post_id = ?"
		));
		databaseQueries.put("getPostByUser", new DatabaseQuery(true, 
			"SELECT * FROM Posts WHERE user_id = ?"
		));
		databaseQueries.put("reportPost", new DatabaseQuery(false, 
			"UPDATE posts SET reported = 1"
		));
		databaseQueries.put("getReportedPosts", new DatabaseQuery(true, 
			"SELECT * FROM posts WHERE reported = 1"
		));
		
		// Events:
		databaseQueries.put("getEvents", new DatabaseQuery(true,
			"SELECT * FROM Posts NATURAL JOIN Events"
		));
		// Images:
		databaseQueries.put("getImagesByPost", new DatabaseQuery(true, 
			"SELECT * FROM Images"
			+ " WHERE image_id = "
			+ "(SELECT image_id FROM Post_has_Images WHERE post_id = ?)"
		));

		// User:
		databaseQueries.put("deleteUser", new DatabaseQuery(false, 
			"DELETE FROM Users WHERE user_id = ?"
		));
		databaseQueries.put("checkUsername", new DatabaseQuery(true, 
			"SELECT * FROM Users WHERE username = ?"
		));
		databaseQueries.put("insertUser", new DatabaseQuery(false, 
			"INSERT INTO Users VALUES(username = ?, password = ?,"
			+ " fistname= ?, lastname = ?, email = ?, admin = 0 )"
		));
		databaseQueries.put("checkUser", new DatabaseQuery(true, 
			"SELECT * FROM Users WHERE username = ?, password = ?"
		));
		databaseQueries.put("updateUser", new DatabaseQuery(false, 
			"UPDATE Users SET username = ?, password = ?,"
			+ " firstname = ?, lastname = ?, email = ? WHERE id = ?"
		));
		databaseQueries.put("getUserByUsername", new DatabaseQuery(true, 
			"SELECT * FROM Users WHERE username = ?"
		));
		databaseQueries.put("getUserById", new DatabaseQuery(true, 
			"SELECT * FROM Users WHERE user_id = ?"
		));
		databaseQueries.put("setAdmin", new DatabaseQuery(false, 
			"UPDATE Users SET admin = 1"
		));

		// Ids:
		databaseQueries.put("getIdByTableName", new DatabaseQuery(true,
			"SELECT next_id FROM Ids WHERE table_name = ?"
		));
		databaseQueries.put("incrementId", new DatabaseQuery(false,
			"UPDATE Ids SET next_Id= next_Id + 1 WHERE table_name = ?"
		));
	}

	public ResultSet executeQuery(String requestName, Object... values) throws DatabaseCriticalErrorException{
		DatabaseQuery databaseQuery = databaseQueries.get(requestName);
		return databaseQuery.execute(this, databaseConnector, values);
	}
}
package applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CommentManager extends Manager {

	/**
	 * inserts a new Comment (creates a Comment-Object) and saves it in the
	 * Database; the commentId is taken from the corresponding id-table,the Date
	 * is taken from the CalenderManager
	 * 
	 * @param text
	 * @param date
	 * @param parentId
	 * @param postId
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	public void insert(int userId, String text, int parentId, int postId)
			throws SQLException {
		int commentId;
		LocalDateTime date = this.model.getCalenderManager().getDate();
		// TODO hier muss keine Id mehr geholt werden, wird in der DB gemacht!
		ResultSet result = this.model.getQueryManager
				.executeQuery("getCommentId");
		commentId = result.getInt(1);

		this.model.getQueryManager.execute("insertComment", commentId, userId,
				text, date, parentId, postId);
	}

	/**
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	public void delete(int id) throws SQLException {
		this.model.getQueryManager.executeQuery("DeleteComment", id);

	}

	/**
	 * updates the text of Comment with given id
	 * 
	 * @param id
	 * @param text
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	public void update(int id, String text) throws SQLException {
		this.model.getQueryManager.execute("UpdateComment", text, id);
	}

	/**
	 * returns an ArrayList containing all Comments of the given Post
	 * 
	 * @param postId
	 * @return
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	@SuppressWarnings("null")
	public ArrayList<Comment> getByPost(int postId) throws SQLException {
		ResultSet result = this.model.getQueryManager
				.executeQuery("getCommentsByPost", postId);

		ArrayList<Comment> comments = null;
		int commentId;
		int userId;
		String text;
		String sdate;
		LocalDateTime date;
		int parentId;
		while (result.next()) {
			commentId = result.getInt(1);
			userId = result.getInt(2);
			text = result.getString(3);
			sdate = result.getString(4);
			date = LocalDateTime.parse(sdate);
			parentId = result.getInt(5);
			comments.add(new Comment(commentId, userId, text, date, parentId,
					postId));
		}
		return comments;
	}
}

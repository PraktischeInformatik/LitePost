package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * the CommentManager
 * 
 * @author Julia Moos
 *
 */
public class CommentManager extends Manager {

	/**
	 * inserts a new Comment (creates a Comment-Object) and saves it in the
	 * Database; the commentId is taken from the corresponding id-table,the Date
	 * is taken from the CalenderManager
	 * 
	 * @param content
	 * @param date
	 * @param parentId
	 * @param postId
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	public void insert(String content, int parentId, int postId)
			throws SQLException {
		LocalDateTime date = this.model.getCalenderManager().getDate();
		int userId = 0;
		if (this.model.getSessionManager().exists("username")) {
			userId = model.getUserManager().getActual().getUserId();
		}
		this.model.getQueryManager().executeQuery("insertComment", userId,
				content, date, parentId, postId);
	}

	/**
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	public void delete(int id) throws SQLException{
		this.model.getQueryManager().executeQuery("DeleteComment", id);

	}

	/**
	 * updates the content of Comment with given id
	 * 
	 * @param id
	 * @param content
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	public void update(int id, String content) throws SQLException{
		this.model.getQueryManager().executeQuery("UpdateComment", content, id);
	}

	/**
	 * returns an ArrayList containing all Comments of the given Post
	 * 
	 * @param postId
	 * @return
	 * @throws SQLException
	 * @throws IllegalParameterLengthException
	 */
	public ArrayList<Comment> getByPost(int postId) throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getCommentsByPost", postId);
		ArrayList<Comment> comments = new ArrayList<>();
		while(result.next()) {
			comments.add(this.commentFromResultSet(result));
		}
		return comments;
	}
	
	/**
	 * assembles a comment and its sub comments form a result set recusively
	 * @param rs the result set from with the comments data
	 * @return a comment 
	 * @throws SQLException when fetching data from result set fails
	 */
	private Comment commentFromResultSet(ResultSet rs) throws SQLException {
		int commentId = rs.getInt("comment_id");
		int userId = rs.getInt("user_id");
		String content = rs.getString("content");
		LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
		int parentId = rs.getInt("parent_id");
		int postId = rs.getInt("post_id");
		Comment comment = new Comment(commentId, userId, content, date, parentId, postId);
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getCommentsByParentId", commentId);
		while(result.next()) {
			comment.addSubComment(commentFromResultSet(result));
		}
		return comment;
	}
	

	/**
	 * set the Comment to reported
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void report(int id) throws SQLException {
		this.model.getQueryManager().executeQuery("reportComment", id);
	}

	/**
	 * returns an ArrayList containing all reported Comments
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Comment> getReports() throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getReportedComments");
		ArrayList<Comment> comments = new ArrayList<>();
		while(result.next()) {
			comments.add(this.commentFromResultSet(result));
		}
		return comments;
	}
}

package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

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
	 * @throws DatabaseCriticalErrorException
	 * @throws IllegalParameterLengthException
	 */
	public void insert(int userId, String content, int parentId, int postId)
			throws SQLException, DatabaseCriticalErrorException {
		LocalDateTime date = this.model.getCalenderManager().getDate();

		this.model.getQueryManager().executeQuery("insertComment", userId,
				content, date, parentId, postId);
	}

	/**
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 * @throws IllegalParameterLengthException
	 */
	public void delete(int id) throws SQLException,
			DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("DeleteComment", id);

	}

	/**
	 * updates the content of Comment with given id
	 * 
	 * @param id
	 * @param content
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 * @throws IllegalParameterLengthException
	 */
	public void update(int id, String content) throws SQLException,
			DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("UpdateComment", content, id);
	}

	/**
	 * returns an ArrayList containing all Comments of the given Post
	 * 
	 * @param postId
	 * @return
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 * @throws IllegalParameterLengthException
	 */
	public ArrayList<Comment> getByPost(int postId) throws SQLException,
			DatabaseCriticalErrorException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getCommentsByPost", postId);
		ArrayList<Comment> comments = this.createComments(result);
		return comments;
	}

	/**
	 * set the Comment to reported
	 * 
	 * @param id
	 * @throws DatabaseCriticalErrorException
	 */
	public void report(int id) throws DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("reportComment", id);
	}

	/**
	 * returns an ArrayList containing all reported Comments
	 * 
	 * @return
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public ArrayList<Comment> getReports()
			throws DatabaseCriticalErrorException, SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getReportedComments");
		ArrayList<Comment> comments = this.createComments(result);
		return comments;
	}

	/**
	 * method creates all Comments of given ResultSet and calls method to create
	 * the subcomments
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 */
	@SuppressWarnings("null")
	private ArrayList<Comment> createComments(ResultSet result)
			throws SQLException, DatabaseCriticalErrorException {
		ArrayList<Comment> comments = null;
		int commentId;
		int userId;
		String content;
		Timestamp ldate;
		LocalDateTime date;
		int parentId;
		int postId;
		while (result.next()) {
			commentId = result.getInt("comment_id");
			userId = result.getInt("user_id");
			content = result.getString("content");
			ldate = result.getTimestamp("date");
			date = ldate.toLocalDateTime();
			parentId = result.getInt("parent_id");
			postId = result.getInt("post_id");
			Comment lComment = new Comment(commentId, userId, content, date,
					parentId, postId);
			comments.add(lComment);
			result = this.model.getQueryManager().executeQuery(
					"getCommentsByParentId", commentId);
			this.setSubComments(lComment);
		}
		return comments;
	}

	/**
	 * method adds all Subcomments to a given Comment (and the Subcomments to
	 * the Subcomments and so on)
	 * 
	 * @param comment
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	private void setSubComments(Comment comment)
			throws DatabaseCriticalErrorException, SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getCommentsByParentId", comment.getCommentId());
		int commentId;
		int userId;
		String content;
		Timestamp ldate;
		LocalDateTime date;
		int parentId;
		int postId;
		while (result.next()) {
			commentId = result.getInt("comment_id");
			userId = result.getInt("user_id");
			content = result.getString("content");
			ldate = result.getTimestamp("date");
			date = ldate.toLocalDateTime();
			parentId = result.getInt("parent_id");
			postId = result.getInt("post_id");
			Comment lComment = new Comment(commentId, userId, content, date,
					parentId, postId);
			comment.setSubComments(lComment);
			this.setSubComments(lComment);
		}

	}
}

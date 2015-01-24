package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import ort.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

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
	 * @throws DatabaseCriticalErrorException
	 * @throws IllegalParameterLengthException
	 */
	public void insert(int userId, String text, int parentId, int postId)
			throws SQLException, DatabaseCriticalErrorException {
		LocalDateTime date = this.model.getCalenderManager().getDate();

		this.model.getQueryManager().executeQuery("insertComment", userId,
				text, date, parentId, postId);
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
	 * updates the text of Comment with given id
	 * 
	 * @param id
	 * @param text
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 * @throws IllegalParameterLengthException
	 */
	public void update(int id, String text) throws SQLException,
			DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("UpdateComment", text, id);
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
	@SuppressWarnings("null")
	public ArrayList<Comment> getByPost(int postId) throws SQLException,
			DatabaseCriticalErrorException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getCommentsByPost", postId);

		ArrayList<Comment> comments = null;
		int commentId;
		int userId;
		String text;
		long ldate;
		Instant i;
		LocalDateTime date;
		int parentId;
		while (result.next()) {
			commentId = result.getInt(1);
			userId = result.getInt(2);
			text = result.getString(3);
			ldate = result.getDate(4).getTime();
			i = Instant.ofEpochMilli(ldate);
			date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
			parentId = result.getInt(5);
			Comment lComment = new Comment(commentId, userId, text, date,
					parentId, postId);
			this.setSubComments(lComment);
			comments.add(lComment);
		}

		return comments;
	}

	private void setSubComments(Comment comment)
			throws DatabaseCriticalErrorException, SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getCommentsByParentId", comment.getCommentId());
		int commentId;
		int userId;
		String text;
		long ldate;
		Instant i;
		LocalDateTime date;
		int parentId;
		int postId;
		while (result.next()) {
			commentId = result.getInt(1);
			userId = result.getInt(2);
			text = result.getString(3);
			ldate = result.getDate(4).getTime();
			i = Instant.ofEpochMilli(ldate);
			date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
			parentId = result.getInt(5);
			postId = result.getInt(6);
			Comment lComment = new Comment(commentId, userId, text, date,
					parentId, postId);
			comment.setSubComments(lComment);
			this.setSubComments(lComment);
		}

	}

	public void report() {
		this.model.getQueryManager().executeQuery("reportComment", id);
	}
}

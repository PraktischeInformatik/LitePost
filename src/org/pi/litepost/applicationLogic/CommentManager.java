package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.pi.litepost.exceptions.ForbiddenOperationException;

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
			userId = model.getUserManager().getCurrent().getUserId();
		}
		this.model.getQueryManager().executeQuery("insertComment", userId,
				content, date, parentId, postId);
	}

	/**
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws ForbiddenOperationException 
	 * @throws IllegalParameterLengthException
	 */
	public void delete(int id) throws SQLException, ForbiddenOperationException{
		Comment comment = getById(id);
		User user = model.getUserManager().getCurrent();
		if(user.getUserId() != comment.getUser().getUserId() && !user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
		this.model.getQueryManager().executeQuery("deleteComment", id, id);

	}

	private Comment getById(int id) throws SQLException {
		ResultSet rs = model.getQueryManager().executeQuery("getComment", id);
		return commentFromResultSet(rs);
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

	public ArrayList<Comment> getAll() throws SQLException, ForbiddenOperationException {
		User user = model.getUserManager().getCurrent();
		if(!user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
		ArrayList<Comment> comments = new ArrayList<>();
		ResultSet rs = this.model.getQueryManager().executeQuery("getAllComments");
		while(rs.next()) {
			comments.add(commentFromResultSet(rs, false));
		}
		return comments;
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
	
	public ArrayList<Comment> getByUser(int userId) throws SQLException, ForbiddenOperationException {
		User user = model.getUserManager().getCurrent();
		if(user.getUserId() != userId && !user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
		ArrayList<Comment> comments = new ArrayList<>();
		ResultSet rs = this.model.getQueryManager().executeQuery(
				"getCommentsByUser", userId);
		while(rs.next()) {
			comments.add(commentFromResultSet(rs, false));
		}
		return comments;
	}
	
	private Comment commentFromResultSet(ResultSet rs) throws SQLException {
		return commentFromResultSet(rs, true);
	}

	/**
	 * assembles a comment and its sub comments form a result set recusively
	 * @param rs the result set from with the comments data
	 * @return a comment 
	 * @throws SQLException when fetching data from result set fails
	 */
	private Comment commentFromResultSet(ResultSet rs, boolean loadAnswers) throws SQLException {
		int commentId = rs.getInt("comment_id");
		int userId = rs.getInt("user_id");
		String content = rs.getString("content");
		LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
		int parentId = rs.getInt("parent_id");
		int postId = rs.getInt("post_id");
		boolean reported = rs.getBoolean("reported");
		
		User user = model.getUserManager().getById(userId);
		
		Comment comment = new Comment(commentId, user, content, date, parentId, postId, reported);
		
		if(loadAnswers) {
			ResultSet result = this.model.getQueryManager().executeQuery(
					"getCommentsByParentId", commentId);
			while(result.next()) {
				comment.addSubComment(commentFromResultSet(result));
			}
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
	 * @throws ForbiddenOperationException 
	 */
	public ArrayList<Comment> getReports() throws SQLException, ForbiddenOperationException {
		User user = model.getUserManager().getCurrent();
		if(!user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getReportedComments");
		ArrayList<Comment> comments = new ArrayList<>();
		while(result.next()) {
			comments.add(this.commentFromResultSet(result));
		}
		return comments;
	}
}

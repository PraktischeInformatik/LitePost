package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author Julia Moos
 *
 */
public class Comment {
	private final int commentId;
	private final User user;
	private final String text;
	private final LocalDateTime date;
	private final int parentId;
	private final int postId;
	private final ArrayList<Comment> subComments;
	private final boolean reported;

	public Comment(int commentId, User user, String text, LocalDateTime date,
			int parentId, int postId, boolean reported) {
		this.commentId = commentId;
		this.user = user;
		this.text = text;
		this.date = date;
		this.parentId = parentId;
		this.postId = postId;
		this.subComments = new ArrayList<>();
		this.reported = reported;
	}

	public int getCommentId() {
		return commentId;
	}
	public User getUser() {
		return user;
	}

	public String getText() {
		return text;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public int getParentId() {
		return parentId;
	}

	public int getPostId() {
		return postId;
	}

	public ArrayList<Comment> getSubComments() {
		return subComments;
	}

	public void addSubComment(Comment comment) {
		this.subComments.add(comment);
	}

	public boolean isReported() {
		return reported;
	}
}

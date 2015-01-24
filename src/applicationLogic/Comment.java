package applicationLogic;

import java.time.LocalDateTime;

public class Comment {
	private int commentId;
	private int userId;

	private String text;
	private LocalDateTime date;
	private int parentId;
	private int postId;

	public Comment(int commentId, int userId, String text, LocalDateTime date,
			int parentId, int postId) {
		this.commentId = commentId;
		this.userId = userId;
		this.text = text;
		this.date = date;
		this.parentId = parentId;
		this.postId = postId;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
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

}

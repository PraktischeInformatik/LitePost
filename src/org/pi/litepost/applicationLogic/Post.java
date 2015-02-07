package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * @author Julia Moos
 *
 */
public class Post {
	private int postId;
	private String title;
	private String text;
	private LocalDateTime date;
	private String contact;
	private ArrayList<Image> images;
	private int userId;
	private ArrayList<Comment> comments;
	private boolean reported = false;

	public Post(int postId, String title, String text, String contact,
			LocalDateTime date, int userId) {
		this.postId = postId;
		this.title = title;
		this.text = text;
		this.contact = contact;
		this.userId = userId;
		this.images = new ArrayList<>();
		this.comments = new ArrayList<>();
	}

	public int getPostId() {
		return postId;
	}

	public void setPostId(int postId) {
		this.postId = postId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public int getUserId() {
		return userId;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}

	public void setComments(ArrayList<Comment> comments) {
		this.comments = comments;
	}

	public boolean isEvent() {
		return false;
	}

	public ArrayList<Image> getImages() {
		return images;
	}

	public void addImage(Image image) {
		images.add(image);
	}

	public boolean isReported() {
		return reported;
	}

	public void setReported() {
		this.reported = true;
	}

}

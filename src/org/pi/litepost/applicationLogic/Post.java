package org.pi.litepost.applicationLogic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * @author Julia Moos
 *
 */
public class Post {
	protected final static String STANDARD_DATE_FORMAT = "dd. MMMM uuuu HH:mm";
	private final int postId;
	private final String title;
	private final String text;
	private final LocalDateTime date;
	private final String contact;
	private final ArrayList<Image> images;
	private final User user;
	private final ArrayList<Comment> comments;
	private final boolean reported;
	private final boolean presentation;

	public Post(int postId, String title, String text, String contact,
			LocalDateTime date, User user, boolean reported, boolean presentation) {
		this.postId = postId;
		this.title = title;
		this.text = text;
		this.contact = contact;
		this.date = date;
		this.user = user;
		this.images = new ArrayList<>();
		this.comments = new ArrayList<>();
		this.reported = reported;
		this.presentation = presentation;
	}

	public int getPostId() {
		return postId;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public String getContact() {
		return contact;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public User getUser() {
		return user;
	}

	public ArrayList<Comment> getComments() {
		return comments;
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
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

	public boolean isPresentation() {
		return presentation;
	}
	
	public String formatDate() {
		return formatDate(STANDARD_DATE_FORMAT);
	}
	
	public String formatDate(String pattern) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern(pattern);
		return format.format(getDate());
	}
}

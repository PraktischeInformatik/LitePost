package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * the PostManager
 * 
 * @author Julia Moos
 *
 */
public class PostManager extends Manager {

	/**
	 * inserts a new Post (creates a Post-Object) and saves it in the Database;
	 * the PostId is taken from the corresponding id-table,the Date is taken
	 * from the CalenderManager
	 * 
	 * @param title
	 * @param content
	 * @param contact
	 * @param userId
	 * @throws SQLException
	 */
	public void insert(String title, String content, String contact)
			throws SQLException {
		LocalDateTime date = this.model.getCalenderManager().getDate();
		int userId = 0;
		if (this.model.getSessionManager().exists("username")) {
			userId = model.getUserManager().getCurrent().getUserId();
		}
		this.model.getQueryManager().executeQuery("insertPost", title, content,
				date, contact, userId);
	}
	
	/**
	 * creates an event for a post
	 * 
	 * @param postId  
	 * @param eventDate
	 * @throws SQLException
	 */
	public void makeEvent(int postId, LocalDateTime eventDate) 
			throws SQLException	{
		model.getQueryManager().executeQuery("makeEvent", postId, eventDate);
	}

	/**
	 * adds an image to a post
	 * 
	 * @param source
	 *            the uri of the image
	 * @param post_id
	 *            the post to which the image belongs
	 * @throws SQLException
	 */
	public void addImage(String source, int post_id) throws SQLException {
		model.getQueryManager().executeQuery("insertImage", source);
		ResultSet rs = model.getQueryManager().executeQuery("getLastId",
				"Images");
		model.getQueryManager().executeQuery("addImageToPost", rs.getInt(1),
				post_id);
	}

	/**
	 * deletes Comment with given id
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void delete(int id) throws SQLException {
		this.model.getQueryManager().executeQuery("deletePost", id);
	}

	/**
	 * deletes all Posts which are older than 30 days
	 * 
	 * @throws SQLException
	 */
	public void deleteOld() throws SQLException {
		LocalDateTime date = this.model.getCalenderManager().getDate();
		date.minusDays(30);
		this.model.getQueryManager().executeQuery("deleteOldPost", date);
	}

	/**
	 * updates all parameters of the Post with given id; method is called every
	 * time a User edits his Post
	 * 
	 * @param id
	 * @param title
	 * @param content
	 * @param contact
	 * @param userId
	 * @throws SQLException
	 */
	public void update(int id, String title, String content, String contact)
			throws SQLException {
		this.model.getQueryManager().executeQuery("updatePost", title, content,
				contact, id);
	}

	/**
	 * searches a Post by given keywords
	 * 
	 * @param keywords
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("null")
	public ArrayList<Post> search(String[] keywords) throws SQLException {
		ArrayList<Post> posts = null;
		for (int i = 0; i < keywords.length; i++) {
			String keyword = "%" + keywords[i] + "%";
			ResultSet result = this.model.getQueryManager().executeQuery(
					"searchPosts", keyword, keyword);
			posts.addAll(this.createPosts(result));
		}
		return posts;
	}

	/**
	 * returns an ArrayList containing all Post without their Comments
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Post> getAll() throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getAllPosts");
		ArrayList<Post> posts = this.createPosts(result);
		return posts;
	}

	/**
	 * returns a Post with the given id and its Comments and Subcomments
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public Post getById(int id) throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPostById", id);
		if(result.next()) {
			return createPost(result);
		}else {
			return null;
		}
	}

	/**
	 * returns an ArrayList containing all Post of the given User without their
	 * Comments
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Post> getByUser(int userId) throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPostsByUser", userId);
		ArrayList<Post> posts = this.createPosts(result);
		return posts;
	}

	/**
	 * returns an ArrayList containing all Post of the actual User without their
	 * Comments
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Post> getByUser() throws SQLException {
		int userId = 0;
		if (this.model.getSessionManager().exists("username")) {
			userId = model.getUserManager().getCurrent().getUserId();
		}

		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPostsByUser", userId);

		ArrayList<Post> posts = this.createPosts(result);
		return posts;
	}

	/**
	 * reports the Post with given id
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void report(int id) throws SQLException {
		this.model.getQueryManager().executeQuery("reportPost", id);
	}

	/**
	 * returns an ArrayList containing all reported Post without their Comments
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Post> getReports() throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getRreportPost");
		ArrayList<Post> posts = this.createPosts(result);
		return posts;
	}

	/**
	 * returns an ArrayList containing all Events of the given Month
	 * 
	 * @param month
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Event> getEvents(int month) throws SQLException {
		ArrayList<Event> events = new ArrayList<>();
		ArrayList<Event> all = this.getEvents();
		Iterator<Event> iter = all.iterator();
		while (iter.hasNext()) {
			Event actual = (Event) iter.next();
			if (actual.getEventDate().getMonthValue() == month
					&& actual.getEventDate().getYear() == model
							.getCalenderManager().getDate().getYear()) {
				events.add(actual);
			}
		}

		return events;
	}

	/**
	 * returns an ArrayList containing all Events (only Events which take place
	 * in the future)
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Event> getEvents() throws SQLException {
		ArrayList<Event> events = new ArrayList<>();

		ResultSet result = this.model.getQueryManager().executeQuery(
				"getFutureEvents");

		while (result.next()) {
			LocalDateTime postDate = result.getTimestamp("date").toLocalDateTime();
			LocalDateTime eventDate = result.getTimestamp("event_date").toLocalDateTime();
			int postId = result.getInt("post_id");
			String title = result.getString("title");
			String content = result.getString("content");
			String contact = result.getString("contact");
			int userId = result.getInt("user_id");
			boolean reported = result.getBoolean("reported");
			boolean presentation = result.getBoolean("presentation");
			Event event = new Event(postId, title, content, contact, postDate,
					userId, reported, presentation, eventDate);

			ResultSet images = this.model.getQueryManager().executeQuery(
					"getImagesByPost", postId);

			while (images.next()) {
				int imageId = images.getInt("image_id");
				String source = images.getString("source");
				event.addImage(new Image(imageId, source));
			}
			events.add(event);
		}

		return events;
	}

	public ArrayList<Post> getPresentations() throws SQLException, SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPresentations");
		ArrayList<Post> posts = this.createPosts(result);
		return posts;
	}

	/**
	 * method creates all Posts of given ResultSet
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private ArrayList<Post> createPosts(ResultSet result) throws SQLException {
		ArrayList<Post> posts = new ArrayList<>();
		while (result.next()) {
			posts.add(createPost(result));
		}
		return posts;
	}

	/**
	 * method creates one Posts of given ResultSet
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private Post createPost(ResultSet rs) throws SQLException {
		int id = rs.getInt("post_id");
		String title = rs.getString("title");
		String content = rs.getString("content");
		LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
		String contact = rs.getString("contact");
		int userId = rs.getInt("user_id");
		boolean reported = rs.getBoolean("reported");
		boolean presentation = rs.getBoolean("presentation");

		Post post; 

		ResultSet evResult = this.model.getQueryManager().executeQuery(
				"getEventForPost", id);
		
		if(evResult.next()) {
			LocalDateTime eventDate = evResult.getTimestamp("event_date").toLocalDateTime();
			post = new Event(id, title, content, contact, date, userId,
					reported, presentation, eventDate);
		} else {
			post = new Post(id, title, content, contact, date, userId,
					reported, presentation);
		}
		
		ResultSet imResult = this.model.getQueryManager().executeQuery(
				"getImagesByPost", id);
		
		while (imResult.next()) {
			int imageId = imResult.getInt("image_id");
			String source = imResult.getString("source");
			post.addImage(new Image(imageId, source));
		}
		
		for (Comment comment : this.model.getCommentManager().getByPost(id)) {
			post.addComment(comment);
		}

		return post;
	}
}

package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;

import org.pi.litepost.exceptions.ForbiddenOperationException;

/**
 * the PostManager
 * 
 * @author Julia Moos
 *
 */
public class PostManager extends Manager {

	@Override
	public void init() {
		try {
			model.getQueryManager().executeQuery("deleteOldPosts",
					LocalDateTime.now(clock).minusMonths(1));
			model.getQueryManager().executeQuery("deleteOldEvents",
					LocalDateTime.now(clock));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

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
	public int insert(String title, String content, String contact)
			throws SQLException {
		LocalDateTime date = LocalDateTime.now(clock);
		int userId = 0;
		if (this.model.getSessionManager().exists("username")) {
			userId = model.getUserManager().getCurrent().getUserId();
		}
		ResultSet rs = model.getQueryManager().executeQuery("insertPost",
				title, content, date, contact, userId);
		return rs.getInt(1);
	}

	/**
	 * creates an event for a post
	 * 
	 * @param postId
	 * @param eventDate
	 * @throws SQLException
	 * @throws InvalidDateException 
	 */
	public void makeEvent(int postId, LocalDateTime eventDate)
			throws SQLException {
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
	 * deletes Post with given id
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void delete(int id) throws SQLException, ForbiddenOperationException {
		Post post = getById(id);
		User user = model.getUserManager().getCurrent();
		if (post.getUser().getUserId() != user.getUserId() && !user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
		model.getQueryManager().executeQuery("deletePost", id);
		// model.getQueryManager().executeQuery("deleteCommentsFromPost", id);
	}

	/**
	 * deletes all Posts which are older than 30 days
	 * 
	 * @throws SQLException
	 */
	public void deleteOld() throws SQLException {
		LocalDateTime date = LocalDateTime.now(clock);
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
	 * @throws ForbiddenOperationException
	 */
	public void update(int id, String title, String content, String contact)
			throws SQLException, ForbiddenOperationException {
		Post post = getById(id);
		User user = model.getUserManager().getCurrent();
		if (user.getUserId() != post.getUser().getUserId() && !user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
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
	public ArrayList<Post> search(String[] keywords) throws SQLException {
		ArrayList<Post> allPosts = new ArrayList<>();
		for (int i = 0; i < keywords.length; i++) {
			String keyword = "%" + keywords[i] + "%";
			ResultSet result = this.model.getQueryManager().executeQuery(
					"searchPosts", keyword, keyword);
			ArrayList<Post> singlePosts = this.createPosts(result);
			
			//Post already in the list?
			for (Post p : singlePosts) {
				boolean contains = false;
				for (Post po : allPosts) {
					if (po.getPostId() == p.getPostId())
						contains = true;
				}
				if(!contains)
					allPosts.add(p);
			}
		}
		return allPosts;
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
		ArrayList<Post> posts = this.createPosts(result, false);
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
		if (result.next()) {
			return createPost(result);
		} else {
			return null;
		}
	}

	/**
	 * returns an ArrayList containing all Post of the given User without their
	 * Comments or images
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws ForbiddenOperationException
	 */
	public ArrayList<Post> getByUser(int userId) throws SQLException,
			ForbiddenOperationException {
		User user = model.getUserManager().getCurrent();
		if (user.getUserId() != userId && !user.isAdmin()) {
			throw new ForbiddenOperationException();
		}

		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPostsByUser", userId);
		ArrayList<Post> posts = this.createPosts(result, false);
		return posts;
	}

	/**
	 * returns an ArrayList containing all Post of the actual User without their
	 * Comments or images
	 * 
	 * @param userId
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Post> getByUser() throws SQLException {
		User user = model.getUserManager().getCurrent();
		if (user != null) {
			int userId = user.getUserId();
			try {
				return getByUser(userId);
			} catch (ForbiddenOperationException e) {
				// not going to happen, since we made sure userId is the current
				// Users id
			}
		}
		return null;
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
	 * unblock Post with given id
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws ForbiddenOperationException
	 */
	public void unblock(int postId) throws SQLException,
			ForbiddenOperationException {
		User user = model.getUserManager().getCurrent();
		if (user == null || !user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
		model.getQueryManager().executeQuery("unblockPost", postId);
	}

	/**
	 * returns an ArrayList containing all reported Post without their Comments
	 * or images
	 * 
	 * @return
	 * @throws SQLException
	 * @throws ForbiddenOperationException
	 */
	public ArrayList<Post> getReports() throws SQLException,
			ForbiddenOperationException {
		if (!model.getUserManager().getCurrent().isAdmin()) {
			throw new ForbiddenOperationException();
		}
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getReportedPosts");
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
	public ArrayList<Event> getEvents(YearMonth month) throws SQLException {
		LocalDateTime begin = month.atDay(1).atStartOfDay();
		LocalDateTime end = month.atEndOfMonth().plusDays(1).atStartOfDay();
		ResultSet rs = model.getQueryManager().executeQuery("getEventsBetween",
				begin, end);

		ArrayList<Event> events = new ArrayList<>();
		while (rs.next()) {
			events.add(createEvent(rs));
		}

		return events;
	}

	/**
	 * returns an ArrayList containing all Events on the given date
	 * 
	 * @param month
	 * @return
	 * @throws SQLException
	 */
	public ArrayList<Event> getEvents(LocalDate date) throws SQLException {
		LocalDateTime begin = date.atStartOfDay();
		LocalDateTime end = date.plusDays(1).atStartOfDay();
		ResultSet rs = model.getQueryManager().executeQuery("getEventsBetween",
				begin, end);

		ArrayList<Event> events = new ArrayList<>();
		while (rs.next()) {
			events.add(createEvent(rs));
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
				"getEventsAfter", LocalDateTime.now());

		while (result.next()) {
			events.add(createEvent(result));
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
		return createPosts(result, true);
	}

	private ArrayList<Post> createPosts(ResultSet result, boolean loadExtra)
			throws SQLException {
		ArrayList<Post> posts = new ArrayList<>();
		while (result.next()) {
			posts.add(createPost(result, loadExtra));
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
		return createPost(rs, true);
	}

	private Post createPost(ResultSet rs, boolean loadExtra)
			throws SQLException {
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

		User user = model.getUserManager().getById(userId);

		if (evResult.next()) {
			LocalDateTime eventDate = evResult.getTimestamp("event_date")
					.toLocalDateTime();
			post = new Event(id, title, content, contact, date, user, reported,
					presentation, eventDate);
		} else {
			post = new Post(id, title, content, contact, date, user, reported,
					presentation);
		}
		if (loadExtra) {
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
		}

		return post;
	}

	/**
	 * method creates one Event of given ResultSet
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private Event createEvent(ResultSet rs) throws SQLException {
		int id = rs.getInt("post_id");
		String title = rs.getString("title");
		String content = rs.getString("content");
		LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
		String contact = rs.getString("contact");
		int userId = rs.getInt("user_id");
		boolean reported = rs.getBoolean("reported");
		boolean presentation = rs.getBoolean("presentation");
		LocalDateTime eventDate = rs.getTimestamp("event_date")
				.toLocalDateTime();

		User user = model.getUserManager().getById(userId);

		Event event = new Event(id, title, content, contact, date, user,
				reported, presentation, eventDate);

		ResultSet imResult = this.model.getQueryManager().executeQuery(
				"getImagesByPost", id);

		while (imResult.next()) {
			int imageId = imResult.getInt("image_id");
			String source = imResult.getString("source");
			event.addImage(new Image(imageId, source));
		}

		for (Comment comment : this.model.getCommentManager().getByPost(id)) {
			event.addComment(comment);
		}

		return event;
	}
}

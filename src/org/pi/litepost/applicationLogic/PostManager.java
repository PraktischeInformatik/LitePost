package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;

import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

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
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException 
	 */
	public void insert(String title, String content, String contact)
			throws DatabaseCriticalErrorException, SQLException {
		LocalDateTime date = this.model.getCalenderManager().getDate();
		int userId = 0;
		if(this.model.getSessionManager().exists("username")){
			userId = model.getUserManager().getActual().getUserId();
		}
		this.model.getQueryManager().executeQuery("insertPost", title, content,
				date, contact, userId);
	}

	/**
	 * deletes Comment with given id
	 * 
	 * @param id
	 * @throws DatabaseCriticalErrorException
	 */
	public void delete(int id) throws DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("deletePost", id);
	}

	/**
	 * deletes all Posts which are older than 30 days
	 * 
	 * @throws DatabaseCriticalErrorException
	 */
	public void deleteOld() throws DatabaseCriticalErrorException {
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
	 * @throws DatabaseCriticalErrorException
	 */
	public void update(int id, String title, String content, String contact)
			throws DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("updatePost", title, content,
				contact, id);
	}

	/**
	 * searches a Comment by given keywords
	 * 
	 * @param keywords
	 * @return
	 */
	public ArrayList<Post> search(String[] keywords) {
		return null;
	}

	/**
	 * returns an ArrayList containing all Post without their Comments
	 * 
	 * @return
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public ArrayList<Post> getAll() throws DatabaseCriticalErrorException,
			SQLException {
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
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public Post getById(int id) throws DatabaseCriticalErrorException,
			SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPostById", id);

		String title = result.getString(2);
		String content = result.getString(3);
		long ldate = result.getDate(4).getTime();
		Instant i = Instant.ofEpochMilli(ldate);
		LocalDateTime date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
		String contact = result.getString(5);
		int userId = result.getInt(6);

		Post lPost = new Post(id, title, content, contact, date, userId);

		ResultSet imResult = this.model.getQueryManager().executeQuery(
				"getImagesByPost", id);

		int imageId;
		String source;
		while (imResult.next()) {
			imageId = result.getInt("image_id");
			source = result.getString("source");
			lPost.setImages(new Image(imageId, source));
		}
		ArrayList<Comment> comments = this.model.getCommentManager().getByPost(
				id);
		lPost.setComments(comments);
		return lPost;
	}

	/**
	 * returns an ArrayList containing all Post of the given User without their
	 * Comments
	 * 
	 * @param userId
	 * @return
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public ArrayList<Post> getByUser(int userId)
			throws DatabaseCriticalErrorException, SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPostsByUser");
		ArrayList<Post> posts = this.createPosts(result);
		return posts;
	}

	/**
	 * reports the Post with given id
	 * 
	 * @param id
	 * @throws SQLException
	 * @throws DatabaseCriticalErrorException
	 */
	public void report(int id) throws DatabaseCriticalErrorException,
			SQLException {
		this.model.getQueryManager().executeQuery("reportPost", id);
	}

	/**
	 * returns an ArrayList containing all reported Post without their Comments
	 * 
	 * @return
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public ArrayList<Post> getReports() throws DatabaseCriticalErrorException,
			SQLException {
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
	 * @throws DatabaseCriticalErrorException
	 */
	@SuppressWarnings("null")
	public ArrayList<Event> getEvents(int month)
			throws DatabaseCriticalErrorException, SQLException {
		ArrayList<Event> events = null;
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
	 * @throws DatabaseCriticalErrorException
	 */
	@SuppressWarnings("null")
	public ArrayList<Event> getEvents() throws DatabaseCriticalErrorException,
			SQLException {
		ArrayList<Event> events = null;
		int postId;
		String title;
		String content;
		Timestamp ldate;
		LocalDateTime date;
		String contact;
		int imageId;
		String source;
		int userId;
		LocalDateTime eventDate;
		Event lEvent;

		ResultSet result = this.model.getQueryManager()
				.executeQuery("getEvens");
		ResultSet imResult;

		while (result.next()) {
			ldate = result.getTimestamp("date");
			eventDate = ldate.toLocalDateTime();
			if (eventDate.isAfter(model.getCalenderManager().getDate())) {
				postId = result.getInt("post_id");
				title = result.getString("title");
				content = result.getString("content");
				ldate = result.getTimestamp("date");
				date = ldate.toLocalDateTime();
				contact = result.getString("contact");
				userId = result.getInt("user_id");
				lEvent = new Event(postId, title, content, contact, date,
						userId, eventDate);
				imResult = this.model.getQueryManager().executeQuery(
						"getImagesByPost", postId);
				while (imResult.next()) {
					imageId = result.getInt("image_id");
					source = result.getString("source");
					lEvent.setImages(new Image(imageId, source));
				}
				events.add(lEvent);
			}
		}

		return events;
	}

	/**
	 * method creates all Posts of given ResultSet
	 * 
	 * @param result
	 * @return
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	private ArrayList<Post> createPosts(ResultSet result)
			throws DatabaseCriticalErrorException, SQLException {
		int postId;
		String title;
		String content;
		Timestamp ldate;
		LocalDateTime date;
		String contact;
		int imageId;
		String source;
		int userId;
		Post lPost;

		ArrayList<Post> posts = new ArrayList<>();
		ResultSet imResult;

		while (result.next()) {
			postId = result.getInt("post_id");
			title = result.getString("title");
			content = result.getString("content");
			ldate = result.getTimestamp("date");
			date = ldate.toLocalDateTime();
			contact = result.getString("contact");
			userId = result.getInt("user_id");

			lPost = new Post(postId, title, content, contact, date, userId);

			imResult = this.model.getQueryManager().executeQuery(
					"getImagesByPost", postId);
			while (imResult.next()) {
				imageId = result.getInt("image_id");
				source = result.getString("source");
				lPost.setImages(new Image(imageId, source));
			}
			posts.add(lPost);
		}
		return posts;
	}
}

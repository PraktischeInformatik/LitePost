package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;

import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;

public class PostManager extends Manager {

	/**
	 * inserts a new Post (creates a Post-Object) and saves it in the Database;
	 * the PostId is taken from the corresponding id-table,the Date is taken
	 * from the CalenderManager
	 * 
	 * @param title
	 * @param text
	 * @param contact
	 * @param userId
	 * @throws DatabaseCriticalErrorException
	 */
	public void insert(String title, String text, String contact, int userId)
			throws DatabaseCriticalErrorException {
		LocalDateTime date = this.model.getCalenderManager().getDate();

		this.model.getQueryManager().executeQuery("insertPost", title, text,
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
	 * @param text
	 * @param contact
	 * @param userId
	 * @throws DatabaseCriticalErrorException
	 */
	public void update(int id, String title, String text, String contact)
			throws DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("updatePost", title, text,
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
	@SuppressWarnings("null")
	public ArrayList<Post> getAll() throws DatabaseCriticalErrorException,
			SQLException {
		int postId;
		String title;
		String text;
		long ldate;
		Instant i;
		LocalDateTime date;
		String contact;
		int imageId;
		String source;
		int userId;
		Post lPost;

		ArrayList<Post> posts = null;
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getAllPosts");
		ResultSet imResult;

		while (result.next()) {
			postId = result.getInt(1);
			title = result.getString(2);
			text = result.getString(3);
			ldate = result.getDate(4).getTime();
			i = Instant.ofEpochMilli(ldate);
			date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
			contact = result.getString(5);
			userId = result.getInt(6);

			lPost = new Post(postId, title, text, contact, date, userId);

			imResult = this.model.getQueryManager().executeQuery(
					"getImagesByPost", postId);
			while (imResult.next()) {
				imageId = result.getInt(1);
				source = result.getString(2);
				lPost.setImages(new Image(imageId, source));
			}
			posts.add(lPost);
		}
		return posts;
	}

	/**
	 * returns a Post with the given id
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
		String text = result.getString(3);
		long ldate = result.getDate(4).getTime();
		Instant i = Instant.ofEpochMilli(ldate);
		LocalDateTime date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
		String contact = result.getString(5);
		int userId = result.getInt(6);

		Post lPost = new Post(id, title, text, contact, date, userId);

		ResultSet imResult = this.model.getQueryManager().executeQuery(
				"getImagesByPost", id);

		int imageId;
		String source;
		while (imResult.next()) {
			imageId = result.getInt(1);
			source = result.getString(2);
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
	@SuppressWarnings("null")
	public ArrayList<Post> getByUser(int userId)
			throws DatabaseCriticalErrorException, SQLException {
		int postId;
		String title;
		String text;
		long ldate;
		Instant i;
		LocalDateTime date;
		String contact;
		int imageId;
		String source;
		Post lPost;

		ArrayList<Post> posts = null;
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPostsByUser");
		ResultSet imResult;

		while (result.next()) {
			postId = result.getInt(1);
			title = result.getString(2);
			text = result.getString(3);
			ldate = result.getDate(4).getTime();
			i = Instant.ofEpochMilli(ldate);
			date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
			contact = result.getString(5);

			lPost = new Post(postId, title, text, contact, date, userId);

			imResult = this.model.getQueryManager().executeQuery(
					"getImagesByPost", postId);
			while (imResult.next()) {
				imageId = result.getInt(1);
				source = result.getString(2);
				lPost.setImages(new Image(imageId, source));
			}
			posts.add(lPost);
		}
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
	@SuppressWarnings("null")
	public ArrayList<Post> getReports() throws DatabaseCriticalErrorException,
			SQLException {

		int postId;
		String title;
		String text;
		long ldate;
		Instant i;
		LocalDateTime date;
		String contact;
		int userId;
		int imageId;
		String source;
		Post lPost;

		ArrayList<Post> posts = null;
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getRreportPost");
		ResultSet imResult;

		while (result.next()) {
			postId = result.getInt(1);
			title = result.getString(2);
			text = result.getString(3);
			ldate = result.getDate(4).getTime();
			i = Instant.ofEpochMilli(ldate);
			date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
			contact = result.getString(5);
			userId = result.getInt(6);
			lPost = new Post(postId, title, text, contact, date, userId);

			imResult = this.model.getQueryManager().executeQuery(
					"getImagesByPost", postId);
			while (imResult.next()) {
				imageId = result.getInt(1);
				source = result.getString(2);
				lPost.setImages(new Image(imageId, source));
			}
			posts.add(lPost);
		}
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
		String text;
		long ldate;
		Instant i;
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
			postId = result.getInt(1);
			title = result.getString(2);
			text = result.getString(3);
			ldate = result.getDate(4).getTime();
			i = Instant.ofEpochMilli(ldate);
			date = LocalDateTime.ofInstant(i, ZoneId.systemDefault());
			contact = result.getString(5);
			userId = result.getInt(6);
			ldate = result.getDate(7).getTime();
			i = Instant.ofEpochMilli(ldate);
			eventDate = LocalDateTime.ofInstant(i, ZoneId.systemDefault());

			lEvent = new Event(postId, title, text, contact, date, userId,
					eventDate);

			imResult = this.model.getQueryManager().executeQuery(
					"getImagesByPost", postId);
			while (imResult.next()) {
				imageId = result.getInt(1);
				source = result.getString(2);
				lEvent.setImages(new Image(imageId, source));
			}
			events.add(lEvent);
		}

		return events;
	}
}

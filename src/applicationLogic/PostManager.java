package applicationLogic;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;

public class PostManager extends Manager{

	/**
	 * inserts a new Post (creates a Post-Object) and saves it in the Database;
	 * the PostId is taken from the corresponding id-table,the Date is taken
	 * from the CalenderManager
	 * 
	 * @param title
	 * @param text
	 * @param contact
	 * @param userId
	 */
	public void insert(String title, String text, String contact, int userId) {

	}

	/**
	 * deletes Comment with given id
	 * 
	 * @param id
	 */
	public void delete(int id) {

	}

	/**
	 * deletes all Posts which are older than 30 days
	 */
	public void deleteOld() {

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
	 */
	public void update(int id, String title, String text, String contact,
			int userId) {

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
	 * returns an ArrayList containing all Post
	 * 
	 * @return
	 */
	public ArrayList<Post> getAll() {
		return null;

	}

	/**
	 * returns a Post with the given id
	 * 
	 * @param id
	 * @return
	 */
	public Post getById(int id) {
		return null;
	}

	/**
	 * returns an ArrayList containing all Post of the given User
	 * 
	 * @param userId
	 * @return
	 */
	public ArrayList<Post> getByUser(int userId) {
		return null;
	}

	/**
	 * reports the Post with given id; the report is passed on to an
	 * administrator, so he could decide what to do
	 * 
	 * @param id
	 */
	public void report(int id) {

	}
	
	/**
	 * returns an ArrayList containing all Events of the given Month
	 * 
	 * @param month
	 * @return
	 */
	public ArrayList<Event> getEvents(YearMonth month) {
		return null;
	}

	/**
	 * returns an ArrayList containing all Events (only Events which take place
	 * in the future)
	 * 
	 * @return
	 */
	public ArrayList<Event> getEvents() {
		return null;
	}
}

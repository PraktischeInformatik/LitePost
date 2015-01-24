package org.pi.litepost.applicationLogic;

public class UserManager extends Manager{

	/**
	 * inserts a new User (creates a User-Object) and saves it in the Database;
	 * the userId is taken from the corresponding id-table
	 * 
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param email
	 */
	public void insert(String username, String password, String firstname,
			String lastname, String email) {

	}

	/**
	 * login of a User; checks if there is an corresponding pair of username and
	 * password in the database
	 * 
	 * @param username
	 * @param password
	 */
	public void login(String username, String password) {

	}

	/**
	 * logout of User with given userId
	 * 
	 * @param userId
	 */
	public void logout(int userId) {

	}

	/**
	 * updates all parameters of the User with given id; method is called every
	 * time a User edits his profile
	 * 
	 * @param UserId
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param email
	 */
	public void update(int id, String username, String password,
			String firstname, String lastname, String email) {

	}

	/**
	 * deletes User with given userId, all his Posts, Comments and Messages are
	 * deleted as well
	 * 
	 * @param userId
	 */
	public void delete(int userId) {

	}

	/**
	 * returns a User with the given username
	 * 
	 * @param username
	 * @return
	 */
	public User getByName(String username) {
		return null;
	}

	/**
	 * returns a User with the given id
	 * 
	 * @param id
	 * @return
	 */
	public User getById(int id) {
		return null;
	}
}

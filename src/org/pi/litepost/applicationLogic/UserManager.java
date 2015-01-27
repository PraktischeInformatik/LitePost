package org.pi.litepost.applicationLogic;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.pi.litepost.databaseAccess.DatabaseCriticalErrorException;
import org.pi.litepost.exceptions.LoginFailedException;
import org.pi.litepost.exceptions.UseranameExistsException;

/**
 * the UserManager
 * 
 * @author Julia Moos
 *
 */
public class UserManager extends Manager {

	/**
	 * inserts a new User (creates a User-Object) and saves it in the Database;
	 * the userId is taken from the corresponding id-table
	 * 
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @throws DatabaseCriticalErrorException
	 * @throws UseranameExistsException
	 */
	public void insert(String username, String password, String firstname,
			String lastname, String email)
			throws DatabaseCriticalErrorException, UseranameExistsException {
		// check if username is already used
		ResultSet result = this.model.getQueryManager().executeQuery(
				"checkUsername", username);
		if (result == null) {
			this.model.getQueryManager().executeQuery("insertUser", username,
					password, firstname, lastname, email);
		} else {
			throw new UseranameExistsException();
		}
	}

	/**
	 * login of a User; checks if there is an corresponding pair of username and
	 * password in the database
	 * 
	 * @param username
	 * @param password
	 * @throws DatabaseCriticalErrorException
	 * @throws LoginFailedException
	 */
	public void login(String username, String password)
			throws DatabaseCriticalErrorException, LoginFailedException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"checkUser", username);
		if (result == null) {
			throw new LoginFailedException();
		}
		// TODO muss man nochwas machen um User einzuloggen?
	}

	/**
	 * logout of User with given userId
	 * 
	 * @param userId
	 */
	public void logout(int userId) {
		// TODO close Session
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
	 * @throws DatabaseCriticalErrorException
	 */
	public void update(int id, String username, String password,
			String firstname, String lastname, String email)
			throws DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("updateUser", username,
				password, firstname, lastname, email, id);
	}

	/**
	 * deletes User with given userId, all his Posts, Comments and Messages are
	 * deleted as well
	 * 
	 * @param userId
	 * @throws DatabaseCriticalErrorException
	 */
	public void delete(int id) throws DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("deleteUser", id);
		this.model.getQueryManager().executeQuery("deletePostByUser", id);
		this.model.getQueryManager().executeQuery("deleteCommentByUser", id);
		this.model.getQueryManager().executeQuery("deleteMessageByUser", id);
	}

	/**
	 * returns a User with the given username
	 * 
	 * @param username
	 * @return
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public User getByName(String username)
			throws DatabaseCriticalErrorException, SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getUserByUsername", username);
		return this.createUser(result);
	}

	/**
	 * returns a User with the given id
	 * 
	 * @param id
	 * @return
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public User getById(int id) throws DatabaseCriticalErrorException,
			SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getUserById", id);
		return this.createUser(result);
	}

	/**
	 * sets the User with given id as administrator
	 * 
	 * @param id
	 * @throws DatabaseCriticalErrorException
	 */
	public void setAdmin(int id) throws DatabaseCriticalErrorException {
		this.model.getQueryManager().executeQuery("setAdmin", id);
	}

	/**
	 * method creates User out of given ResultSet
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private User createUser(ResultSet result) throws SQLException {
		int userId;
		String username;
		String password;
		String firstname;
		String lastname;
		String email;
		userId = result.getInt(1);
		username = result.getString(2);
		password = result.getString(3);
		firstname = result.getString(4);
		lastname = result.getString(5);
		email = result.getString(6);
		User luser = new User(userId, username, password, firstname, lastname,
				email);
		if (result.getBoolean(7)) {
			luser.setAdmin();
		}
		return luser;
	}
}

package org.pi.litepost.applicationLogic;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.pi.litepost.PasswordHash;
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
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws SQLException
	 */
	public void insert(String username, String password, String firstname,
			String lastname, String email)
			throws DatabaseCriticalErrorException, UseranameExistsException,
			NoSuchAlgorithmException, InvalidKeySpecException, SQLException {
		// check if username is already used
		ResultSet result = this.model.getQueryManager().executeQuery(
				"checkUsername", username);
		if (result == null) {
			String hpassword = PasswordHash.createHash(password);
			this.model.getQueryManager().executeQuery("insertUser", username,
					hpassword, firstname, lastname, email);
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
	 * @throws SQLException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	public void login(String username, String password)
			throws DatabaseCriticalErrorException, LoginFailedException,
			SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
		// TODO check if user has validate his/her email
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getPasswordHash", username);
		if (result == null) {
			throw new LoginFailedException();
		}
		String hpassword = result.getString("password");

		if (!PasswordHash.validatePassword(password, hpassword)) {
			throw new LoginFailedException();
		}
		this.model.getSessionManager().startSession();
		this.model.getSessionManager().set("username", username);
	}

	/**
	 * logout of User with given userId
	 * 
	 * @throws DatabaseCriticalErrorException
	 */
	public void logout() throws DatabaseCriticalErrorException {
		model.getSessionManager().endSession();
	}

	/**
	 * updates parameters of actual User
	 * 
	 * @param firstname
	 * @param lastname
	 * @param password
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public void update(String firstname, String lastname, String password)
			throws DatabaseCriticalErrorException, SQLException {
		User user = this.getActual();
		int id = user.getUserId();
		this.model.getQueryManager().executeQuery("updateUser", firstname,
				lastname, password, id);
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
	 * deletes actual User, all his Posts, Comments and Messages are deleted as
	 * well
	 * 
	 * @param userId
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public void delete() throws DatabaseCriticalErrorException, SQLException {
		User user = this.getActual();
		int id = user.getUserId();
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
	 * returns the actual User
	 * 
	 * @return
	 * @throws DatabaseCriticalErrorException
	 * @throws SQLException
	 */
	public User getActual() throws DatabaseCriticalErrorException, SQLException {
		String username = this.model.getSessionManager().get("username");
		return this.getByName(username);
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
		userId = result.getInt("user_id");
		username = result.getString("username");
		password = result.getString("password");
		firstname = result.getString("firstname");
		lastname = result.getString("lastname");
		email = result.getString("email");
		User luser = new User(userId, username, password, firstname, lastname,
				email);
		if (result.getBoolean("admin")) {
			luser.setAdmin();
		}
		return luser;
	}
}

package org.pi.litepost.applicationLogic;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

import javax.mail.MessagingException;

import org.pi.litepost.App;
import org.pi.litepost.PasswordHash;
import org.pi.litepost.Router;
import org.pi.litepost.exceptions.EmailExistsException;
import org.pi.litepost.exceptions.ForbiddenOperationException;
import org.pi.litepost.exceptions.LoginFailedException;
import org.pi.litepost.exceptions.PasswordResetException;
import org.pi.litepost.exceptions.UserEmailNotVerifiedException;
import org.pi.litepost.exceptions.UseranameExistsException;

/**
 * the UserManager
 * 
 * @author Julia Moos
 *
 */
public class UserManager extends Manager {
	
	private User current;

	/**
	 * registers a new User (creates a User-Object) and saves it in the Database;
	 * the userId is taken from the corresponding id-table
	 * 
	 * @param username
	 * @param password
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @throws UseranameExistsException when the username is already in use
	 * @throws EmailExistsException when the email address is already in use
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws SQLException
	 * @throws MessagingException when the verification mail send fails
	 */
	public int register(String username, String password, String firstname,
			String lastname, String email)
			throws UseranameExistsException,
			NoSuchAlgorithmException, InvalidKeySpecException, SQLException, MessagingException, EmailExistsException {
		// check if username/email is already used
		ResultSet result = this.model.getQueryManager().executeQuery(
				"checkUserData", username, email);
		if (result.next()) {
			if(result.getString("username").equals(username)) {
				throw new UseranameExistsException();	
			}
			if(result.getString("email").equals(email)) {
				throw new EmailExistsException();
			}
		}
		
		String hpassword = PasswordHash.createHash(password);
		
		ResultSet rs = model.getQueryManager().executeQuery("insertUser", username,
				hpassword, firstname, lastname, email);
		int newUserId = rs.getInt(1);
		
		User user = createUser(
				this.model.getQueryManager().executeQuery("getUserByUsername", username));
		
		String token = createToken();
		this.model.getQueryManager().executeQuery("setEmailVerificationToken", user.getUserId(), token);
		
		String host = App.config.getProperty("litepost.serverhost");
		String link = Router.linkTo("emailVerification", token);
		String uri = String.format("http://%s%s", host, link);
		
		HashMap<String, Object> data = new HashMap<>();
		data.put("verificationLink", uri);
		data.put("user", user);
		model.getMailManager().sendSystemMail(user.getEmail(), "Wilkommen bei litepost", "mail.welcome", data);
		return newUserId;
	}

	/**
	 * login of a User; checks if there is an corresponding pair of username and
	 * password in the database
	 * 
	 * @param username
	 * @param password
	 * @throws LoginFailedException
	 * @throws SQLException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws UserEmailNotVerifiedException 
	 */

	public void login(String username, String password, boolean remember)
			throws SQLException, LoginFailedException,
			SQLException, NoSuchAlgorithmException, InvalidKeySpecException, UserEmailNotVerifiedException {
		// TODO check if user has validate his/her email
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getUserByUsername", username);
		if (!result.next()) {
			throw new LoginFailedException();
		}
		User user = createUser(result);
		String hpassword = user.getPassword();

		if (!PasswordHash.validatePassword(password, hpassword)) {
			throw new LoginFailedException();
		}
		if(!user.isVerifiedEmail()) {
			throw new UserEmailNotVerifiedException();
		}
		
		if(remember) {
			this.model.getSessionManager().initSession(Duration.ofDays(30));
		}else {
			this.model.getSessionManager().initSession();
		}
		
		this.model.getSessionManager().set("username", username);
	}
	
	/**
	 * Verify a users email;
	 * @param token
	 * @throws SQLException 
	 */
	public boolean verifyEmail(String token) throws SQLException {
		ResultSet rs = model.getQueryManager().executeQuery("getEmailVerificationToken", token);
		if(!rs.next()) {
			return false;
		}
		int userId = rs.getInt("user_id");
		model.getQueryManager().executeQuery("verifyEmail", userId);
		return true;
	}

	/**
	 * logout of User with given userId
	 * 
	 * @throws SQLException
	 */
	public void logout() throws SQLException {
		model.getSessionManager().endSession();
		current = null;
	}
	
	/**
	 * sends a password reset link
	 * @param email the email address to send the link to
	 * @throws MessagingException when sending the email fails 
	 * @throws SQLException 
	 */
	public void sendResetPassword(String email) throws MessagingException, SQLException {
		HashMap<String, Object> data = new HashMap<>();
		String token = createToken();
		
		ResultSet rs = model.getQueryManager().executeQuery("getUserByEmail", email);
		int userId = rs.getInt("user_id");
		model.getQueryManager().executeQuery("setPasswordResetToken", userId, token);
		
		String host = App.config.getProperty("litepost.serverhost");
		String link = Router.linkTo("resetPasswordPage", token);
		String uri = String.format("http://%s%s", host, link);
		
		data.put("resetLink", uri);
		model.getMailManager().sendSystemMail(email, "Passwort zurücksetzen", "mail.passwordreset", data);
	}
	
	public void resetPassword(String newPassword, String token) throws SQLException, PasswordResetException, NoSuchAlgorithmException, InvalidKeySpecException {
		ResultSet rs_token = model.getQueryManager().executeQuery("getPasswordResetToken", token);
		if(!rs_token.next()) {
			throw new PasswordResetException();
		}
		
		ResultSet rs_userid = model.getQueryManager().executeQuery("getUserById", rs_token.getInt("user_id"));
		User user = createUser(rs_userid);
		
		model.getQueryManager().executeQuery("updateUser", PasswordHash.createHash(newPassword), user.getFirstname(), user.getLastname(), user.getUserId());
	}

	/**
	 * updates parameters of actual User
	 * 
	 * @param firstname
	 * @param lastname
	 * @param password
	 * @throws SQLException
	 */
	public void update(String firstname, String lastname, String password)
			throws SQLException {
		User user = this.getCurrent();
		int id = user.getUserId();
		this.model.getQueryManager().executeQuery("updateUser", firstname,
				lastname, password, id);
	}

	/**
	 * deletes User with given userId, all his Posts, Comments and Messages are
	 * deleted as well
	 * 
	 * @param userId
	 * @throws SQLException
	 * @throws ForbiddenOperationException 
	 */
	public void delete(int id) throws SQLException, ForbiddenOperationException {
		User user = getCurrent();
		if(id != user.getUserId() && !user.isAdmin()) {
			throw new ForbiddenOperationException();
		}
		logout();
		model.getQueryManager().executeQuery("deleteUser", id);
		model.getQueryManager().executeQuery("resetPostsByUser", id);
		model.getQueryManager().executeQuery("resetCommentsByUser", id);
		model.getQueryManager().executeQuery("deleteMessagesFromUser", id);
		model.getQueryManager().executeQuery("deleteMessagesToUser", id);
	}

	/**
	 * deletes actual User, all his Posts, Comments and Messages are deleted as
	 * well
	 * 
	 * @param userId
	 * @throws SQLException
	 */
	public void delete() throws SQLException {
		int id = getCurrent().getUserId();
		try {
			delete(id);
		} catch (ForbiddenOperationException e) {}
	}
	
	/**
	 * retreives all users from the database
	 * 
	 * @param userId
	 * @throws SQLException
	 */
	public ArrayList<User> getAll() throws SQLException, ForbiddenOperationException {
		if(!getCurrent().isAdmin()) {
			throw new ForbiddenOperationException();
		}
		ResultSet rs = model.getQueryManager().executeQuery("getAllUsers");
		ArrayList<User> users = new ArrayList<>();
		while(rs.next()) {
			users.add(createUser(rs));
		}
		return users;
	}

	/**
	 * returns a User with the given username
	 * 
	 * @param username
	 * @return
	 * @throws SQLException
	 */
	public User getByName(String username) throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getUserByUsername", username);
		if(!result.next()) {
			return null;
		}
		return this.createUser(result);
	}

	/**
	 * returns a User with the given id
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	public User getById(int id) throws SQLException {
		ResultSet result = this.model.getQueryManager().executeQuery(
				"getUserById", id);
		if(!result.next()) return null;
		return this.createUser(result);
	}

	/**
	 * sets the User with given id as administrator
	 * 
	 * @param id
	 * @throws SQLException
	 */
	public void setAdmin(int id) throws SQLException {
		this.model.getQueryManager().executeQuery("setAdmin", id);
	}

	/**
	 * returns the actual User
	 * 
	 * @return
	 * @throws SQLException
	 */
	public User getCurrent() throws SQLException {
		if(current == null) {
			String username = this.model.getSessionManager().get("username");
			if(username != null){
				current = this.getByName(username);
			}
		}
		return current;
	}

	/**
	 * method creates User out of given ResultSet
	 * 
	 * @param result
	 * @return
	 * @throws SQLException
	 */
	private User createUser(ResultSet result) throws SQLException {
		int userId = result.getInt("user_id");
		String username = result.getString("username");
		String password = result.getString("password");
		String firstname = result.getString("firstname");
		String lastname = result.getString("lastname");
		String email = result.getString("email");
		boolean verified = result.getBoolean("verified_email");
		boolean admin = result.getBoolean("admin");
		User luser = new User(userId, username, password, firstname, lastname,
				email, admin, verified);
		return luser;
	}
	
	/**
	 * Creates a random string for verification purposes
	 * @return a random string
	 */
	private String createToken() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
}

package org.pi.litepost.applicationLogic;

/**
 * @author Julia Moos
 *
 */
public class User {
	private final int userId;
	private final String username;
	private final String password;
	private final String firstname;
	private final String lastname;
	private final String email;
	private final boolean admin;
	private final boolean verified;

	public User(int userId, String username, String password, String firstname,
			String lastname, String email, boolean admin, boolean verified) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.admin = admin;
		this.email = email;
		this.verified = verified;
	}

	public int getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getEmail() {
		return email;
	}

	public boolean isAdmin() {
		return this.admin;
	}
	
	public boolean isVerifiedEmail() {
		return this.verified;
	}
}

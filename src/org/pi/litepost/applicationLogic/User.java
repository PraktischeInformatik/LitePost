package org.pi.litepost.applicationLogic;

/**
 * @author Julia Moos
 *
 */
public class User {
	private int userId;
	private String username;
	private String password;
	private String firstname;
	private String lastname;
	private String email;
	private boolean admin = false;

	public User(int userId, String username, String password, String firstname,
			String lastname, String email) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAdmin() {
		if (admin)
			return true;
		else
			return false;
	}

	public void setAdmin() {
		this.admin = true;
	}
}

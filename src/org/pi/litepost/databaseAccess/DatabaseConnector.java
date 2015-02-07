package org.pi.litepost.databaseAccess;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector implements AutoCloseable{
	private final String jdbcDriverPath;
	private final String databasePath;
	private Connection connection;
	
	
	public DatabaseConnector(String databasePath){
		jdbcDriverPath = "org.sqlite.JDBC";
		this.databasePath = "jdbc:sqlite:" + databasePath;
	}
	
	public void connect() throws DatabaseCriticalErrorException {
		try{
			Class.forName(jdbcDriverPath);
		}catch (ClassNotFoundException e){
			throw new DatabaseCriticalErrorException("JDBC-Driver was not found!");
		}
		
		connection = null;
		try{
			connection = DriverManager.getConnection(this.databasePath);
		}catch (SQLException e) {
			throw new DatabaseCriticalErrorException("Database was not found!");
		}
		
		DatabaseMetaData databaseMetaData;
		try{
			databaseMetaData = connection.getMetaData();
		}catch(SQLException e){
			throw new DatabaseCriticalErrorException("MetaData could not be accessed!");
		}
		
		if(databaseMetaData != null){
			createUsersTable(databaseMetaData);
			createMessagesTable(databaseMetaData);
			createPostsTable(databaseMetaData);
			createEventsTable(databaseMetaData);
			createCommentsTable(databaseMetaData);
			createSessionsTable(databaseMetaData);
			createIdsTable(databaseMetaData);
			createImagesTable(databaseMetaData);
			createPostHasImageTable(databaseMetaData);
		}
	}
	
	
	
	private void createUsersTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Users", "user_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Users", "username").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Users", "password").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Users", "firstname").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Users", "lastname").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Users", "email").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Users", "admin").next())
				throw new SQLException();
		}catch(SQLException e){
			try {
				connection.createStatement().execute(
					"DROP TABLE IF EXISTS Users;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Users("
					+ " user_id INT PRIMARY KEY NOT NULL,"
					+ " username VARCHAR(255) NOT NULL,"
					+ " password VARCHAR(102) NOT NULL,"
					+ " firstname VARCHAR(255) NOT NULL,"
					+ " lastname VARCHAR(255) NOT NULL,"
					+ " email VARCHAR(255) NOT NULL,"
					+ " admin INT(1) NOT NULL);"
				);
			}catch (SQLException e1) {
				e1.printStackTrace();
				throw new DatabaseCriticalErrorException("Could not create table 'Users'!");
			}
		}
	}
	
	private void createMessagesTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Messages", "message_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Messages", "date").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Messages", "sender").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Messages", "receiver").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Messages", "subject").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Messages", "content").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Messages", "hidden").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Messages", "read").next())
				throw new SQLException();
		}
		catch(SQLException e){
			try{connection.createStatement().execute(
					"DROP TABLE IF EXISTS Messages;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Messages("
					+ "message_id INT PRIMARY KEY NOT NULL,"
					+ "date DATE NOT NULL,"
					+ "sender INT NOT NULL,"
					+ "receiver INT NOT NULL,"
					+ "subject TEXT NOT NULL,"
					+ "content TEXT NOT NULL,"
					+ "hidden INT(1) NOT NULL DEFAULT 0,"
					+ "read INT(1) NOT NULL DEFAULT 0,"
					+ "FOREIGN KEY(sender) REFERENCES users(user_id),"
					+ "FOREIGN KEY(receiver) REFERENCES users(user_id));"
				);
			}catch (SQLException e1) {
				e1.printStackTrace();
				throw new DatabaseCriticalErrorException("Could not create table 'Messages'!");
			}
		}
	}
	
	private void createPostsTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Posts", "post_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Posts", "title").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Posts", "content").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Posts", "date").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Posts", "contact").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Posts", "user_id").next())
				throw new SQLException();
		}catch(SQLException e){
			try {
				connection.createStatement().execute(
					"DROP TABLE IF EXISTS Posts;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Posts("
					+ "post_id INT PRIMARY KEY NOT NULL,"
					+ "title TEXT NOT NULL,"
					+ "content TEXT NOT NULL,"
					+ "date DATE NOT NULL,"
					+ "contact TEXT NOT NULL,"
					+ "user_id INT NOT NULL,"
					+ "FOREIGN KEY(user_id) REFERENCES users(user_id));"
				);
			}catch (SQLException e1) {
				e1.printStackTrace();
				throw new DatabaseCriticalErrorException("Could not create table 'Posts'!");
			}
		}
	}
	
	private void createEventsTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Events", "event_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Events", "post_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Events", "event_date").next())
				throw new SQLException();
		}catch(SQLException e){
			try{
				connection.createStatement().execute(
					"DROP TABLE IF EXISTS Events;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Events("
					+ "event_id INT PRIMARY KEY NOT NULL,"
					+ "post_id INT NOT NULL,"
					+ "date DATE NOT NULL,"
					+ "FOREIGN KEY(post_id) REFERENCES Posts(post_id));"
				);
			}catch (SQLException e1){
				e1.printStackTrace();
				throw new DatabaseCriticalErrorException("Could not create table 'Events'!");
			}
		}
	}
	
	private void createCommentsTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Comments", "comment_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Comments", "user_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Comments", "content").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Comments", "date").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Comments", "parent_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Comments", "post_id").next())
				throw new SQLException();
		}catch(SQLException e){
			try{
				connection.createStatement().execute(
					"DROP TABLE IF EXISTS Comments;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Comments("
					+ "comment_id INT PRIMARY KEY NOT NULL,"
					+ "user_id INT NOT NULL,"
					+ "content TEXT NOT NULL,"
					+ "date DATE NOT NULL,"
					+ "parent_id INT NOT NULL DEFAULT 0,"
					+ "post_id INT NOT NULL,"
					+ "FOREIGN KEY(post_id) REFERENCES Posts(post_id),"
					+ "FOREIGN KEY(user_id) REFERENCES Users(user_id));"
				);
			}catch (SQLException e1) {
				e1.printStackTrace();
				throw new DatabaseCriticalErrorException("Could not create table 'Comments'!");
			}
		}
	}
	
	private void createSessionsTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Sessions", "sesion_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Sessions", "key").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Sessions", "value").next())
				throw new SQLException();
		}catch(SQLException e){
			try {
				connection.createStatement().execute(
					"DROP TABLE IF EXISTS Sessions;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Sessions("
					+ "session_id VARCHAR(255) NOT NULL,"
					+ "key VARCHAR(64) NOT NULL,"
					+ "value TEXT NOT NULL,"
					+ "PRIMARY KEY(session_id, key));"
				);
			}catch (SQLException e1) {
				e1.printStackTrace();
				throw new DatabaseCriticalErrorException("Could not create table 'Sessions'!");
			}
		}
	}
	
	private void createIdsTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Ids", "table_name").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Ids", "next_id").next())
				throw new SQLException();
			
			ResultSet rs = connection.createStatement().executeQuery("select count(*) from Ids where next_id > 0 and table_name in (\"Users\", \"Messages\", \"Posts\", \"Events\", \"Comments\", \"Images\");");
			if(rs.getInt(1) != 6) 
				throw new SQLException();
		}catch(SQLException e){
			try {
				connection.createStatement().execute(
					"DROP TABLE IF EXISTS Ids;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Ids("
					+ "table_name varchar(128) PRIMARY KEY NOT NULL,"
					+ "next_id INT NOT NULL DEFAULT 0);"
				);
				connection.createStatement().execute(
					"INSERT INTO Ids VALUES('Users', 1)"
				);
				connection.createStatement().execute(
					"INSERT INTO Ids VALUES('Messages', 1)"
				);
				connection.createStatement().execute(
					"INSERT INTO Ids VALUES('Posts', 1)"
				);
				connection.createStatement().execute(
					"INSERT INTO Ids VALUES('Events', 1)"
				);
				connection.createStatement().execute(
					"INSERT INTO Ids VALUES('Comments', 1)"
				);
				connection.createStatement().execute(
					"INSERT INTO Ids VALUES('Images', 1)"
				);
			}catch (SQLException e1) {
				throw new DatabaseCriticalErrorException("Could not create table 'Ids'!");
			}
		}
	}
	
	private void createImagesTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Images", "image_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Images", "source").next())
				throw new SQLException();
		}catch(SQLException e){
			try {
				connection.createStatement().execute(
					"DROP TABLE IF EXISTS Images;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Images("
					+ "image_id INT PRIMARY KEY NOT NULL,"
					+ "source TEXT NOT NULL);"
				);
			}catch (SQLException e1){
				e1.printStackTrace();
				throw new DatabaseCriticalErrorException("Could not create table 'Images'!");
			}
		}
	}
	
	private void createPostHasImageTable(DatabaseMetaData databaseMetaData) throws DatabaseCriticalErrorException{
		try{
			if(!databaseMetaData.getColumns(null, null, "Post_has_Images", "post_id").next())
				throw new SQLException();
			if(!databaseMetaData.getColumns(null, null, "Post_has_Images", "image_id").next())
				throw new SQLException();
		}catch(SQLException e){
			try {
				connection.createStatement().execute(
					"DROP TABLE IF EXISTS Post_has_Images;"
				);
				connection.createStatement().execute(
					"CREATE TABLE Post_has_Images("
					+ "post_id INT NOT NULL,"
					+ "image_id INT NOT NULL,"
					+ "PRIMARY KEY (post_id, image_id),"
					+ "FOREIGN KEY(post_id) REFERENCES Posts(post_id),"
					+ "FOREIGN KEY(image_id) REFERENCES Images(image_id));"
				);
			}catch (SQLException e1) {
				e1.printStackTrace();
				throw new DatabaseCriticalErrorException("Could not create table 'Post_has_Images'!");
			}
		}
	}
	
	public PreparedStatement createPreparedStatement(String sql) throws SQLException{
		return(connection.prepareStatement(sql));
	}
	
	public void beginTransaction() throws SQLException{
		connection.setAutoCommit(false);
	}
	
	public void commitTransaction() throws SQLException{
		connection.commit();
		connection.setAutoCommit(true);
	}
	
	public void rollbackTransaction() throws SQLException {
		connection.rollback();
	}
	
	public void close() throws DatabaseCriticalErrorException{
		try{
			connection.close();
		}catch(SQLException e){
			throw new DatabaseCriticalErrorException("Connection to database could not be closed!");
		}
	}
}

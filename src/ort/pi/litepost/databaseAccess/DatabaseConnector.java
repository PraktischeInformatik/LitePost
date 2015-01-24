package ort.pi.litepost.databaseAccess;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnector {
	private final String jdbcDriverPath;
	private final String databasePath;
	private Connection connection;
	
	public DatabaseConnector() throws DatabaseCriticalErrorException{
		jdbcDriverPath = "org.sqlite.JDBC";
		databasePath = "jdbc:sqlite:src\\litepost.db";
		
		try{
			Class.forName(jdbcDriverPath);
		}
		catch (ClassNotFoundException e){
			throw new DatabaseCriticalErrorException("JDBC-Driver was not found!");
		}
		
		connection = null;
		try{
			connection = DriverManager.getConnection(databasePath);
		}
		catch (SQLException e) {
			throw new DatabaseCriticalErrorException("Database was not found!");
		}
		
		//TODO Check if Database is prepared
		DatabaseMetaData databaseMetadata = null;
		try{
			databaseMetadata = connection.getMetaData();
		}
		catch(SQLException e){
			throw new DatabaseCriticalErrorException("MetaData could not be accessed!");
		}
		try{
			ResultSet resultSet;
			
			//TODO Check if schema fits
			//CHECK IF Tables exist
			resultSet = databaseMetadata.getTables(null, null, "Users", null);
			resultSet = databaseMetadata.getTables(null, null, "Messages", null);
			resultSet = databaseMetadata.getTables(null, null, "Posts", null);
			resultSet = databaseMetadata.getTables(null, null, "Events", null);
			resultSet = databaseMetadata.getTables(null, null, "Comments", null);
			resultSet = databaseMetadata.getTables(null, null, "Sessions", null);
			resultSet = databaseMetadata.getTables(null, null, "Ids", null);
			resultSet = databaseMetadata.getTables(null, null, "Images", null);
			resultSet = databaseMetadata.getTables(null, null, "Post_has_Images", null);
			
			System.out.println(resultSet.getString(3));
			//CHECK IF columns in tables exist
			resultSet = databaseMetadata.getColumns(null, null, "Users", "user_id");
		}
		catch(SQLException e){
			
		}
	}
	
	public PreparedStatement createPreparedStatement(String sql) throws SQLException{
		return(connection.prepareStatement(sql));
	}
	
	public void close() throws DatabaseCriticalErrorException{
		try{
			connection.close();
		}
		catch(SQLException e){
			throw new DatabaseCriticalErrorException("Connection to database could not be closed!");
		}
	}
}
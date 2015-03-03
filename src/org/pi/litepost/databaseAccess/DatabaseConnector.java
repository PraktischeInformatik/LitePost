package org.pi.litepost.databaseAccess;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.pi.litepost.App;

public class DatabaseConnector implements AutoCloseable{
	private final String jdbcDriverPath;
	private final String databasePath;
	private Connection connection;
	//for nesting transactions
	private int transactionCounter = 0;
	
	public DatabaseConnector(String databasePath){
		jdbcDriverPath = "org.sqlite.JDBC";
		this.databasePath = "jdbc:sqlite:" + databasePath;
	}
	
	public void connect() throws SQLException, ClassNotFoundException {
		connect(false);
	}
	
	public void connect(boolean forceRebuildSchema) throws SQLException, ClassNotFoundException {
		Class.forName(jdbcDriverPath);
		connection = DriverManager.getConnection(this.databasePath);
		DatabaseMetaData meta = connection.getMetaData();
		ResultSet tables = meta.getTables(null, null, null, null);
		HashMap<String, ArrayList<String>> existingSchema = new HashMap<>();
		while(tables.next()) {
			String tableName = tables.getString("TABLE_NAME");
			ResultSet columns = meta.getColumns(null, null, tableName, null);
			ArrayList<String> columnNames = new ArrayList<>();
			while(columns.next()) {
				columnNames.add(columns.getString("COLUMN_NAME"));
			}
			existingSchema.put(tableName, columnNames);
		}
		if(forceRebuildSchema || !DatabaseSchema.SCHEMA.validate(existingSchema)) {
			for(String s : DatabaseSchema.SCHEMA.getDropAndCreate()) {
				connection.createStatement().executeUpdate(s);
			}
		}
	}
	
	public void connect(Collection<IDatabaseSeeder> seeders) throws ClassNotFoundException, SQLException {
		boolean debug = App.config.getProperty("litepost.debug").equalsIgnoreCase("true");
		if(debug) {
			System.out.println("Building database...");
			connect(true);
		} else {
			connect(false);
		}
		
		if(!debug) return;
		DatabaseQueryManager db = new DatabaseQueryManager(this);
		if(seeders.size() != 0) {
			for(IDatabaseSeeder s : seeders) {
				try {
					System.out.println("Seeding " + s.getName() + "...");
					s.seed(db);
				} catch(Exception e) {
					System.out.println("Failed in seeder " + s.getName());
					e.printStackTrace();
				}
			}
			System.out.println("Done Seeding ");
		}
	}
	
	public PreparedStatement createPreparedStatement(String sql) throws SQLException{
		return(connection.prepareStatement(sql));
	}
	
	public void beginTransaction() throws SQLException{
		transactionCounter++;
		connection.setAutoCommit(false);
	}
	
	public void commitTransaction() throws SQLException{
		transactionCounter--;
		if(transactionCounter <= 0) {
			transactionCounter = 0;
			connection.commit();
			connection.setAutoCommit(true);
		}
	}
	
	public void rollbackTransaction() throws SQLException {
		connection.rollback();
	}
	
	public void close() throws SQLException{
		connection.close();
	}
}

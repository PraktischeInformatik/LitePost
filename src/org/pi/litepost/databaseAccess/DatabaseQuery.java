package org.pi.litepost.databaseAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class DatabaseQuery{
	private final boolean returnsResultSet;
	private final String preparationSQL;
	private PreparedStatement preparedStatement;
	private final String tableNameOfId;
	private int lastId;
	
	public DatabaseQuery(boolean returnsResultSet, String preparationSQL){
		this.returnsResultSet = returnsResultSet;
		this.preparationSQL = preparationSQL;
		preparedStatement = null;
		tableNameOfId = null;
	}
	
	public DatabaseQuery(boolean returnsResultSet, String preparationSQL, String tableNameOfId){
		this.returnsResultSet = returnsResultSet;
		this.preparationSQL = preparationSQL;
		preparedStatement = null;
		this.tableNameOfId = tableNameOfId;
	}
	
	public ResultSet execute(DatabaseQueryManager databaseQueryManager, DatabaseConnector databaseConnector, Object...values) throws DatabaseCriticalErrorException{
		if(this.preparedStatement == null){
			try{
				this.preparedStatement = databaseConnector.createPreparedStatement(preparationSQL);
			}catch(SQLException e){
				throw new DatabaseCriticalErrorException("Could not create prepared statement: " + this.preparationSQL);
			}
		}
		else{
			try {
				preparedStatement.clearParameters();
			}catch (SQLException e) {
				throw new DatabaseCriticalErrorException("Unexpected error occured while clearing preparedStatement values!");
			}
		}
		
		if(tableNameOfId == null){
			if(values != null){
				try{
					for(int i = 0; i < values.length; i++){
						if(values[i] instanceof String){
							preparedStatement.setString(i+1, (String)values[i]);
						}
						else if(values[i] instanceof Integer){
							preparedStatement.setInt(i+1, (int)values[i]);
						}
						else if(values[i] instanceof Double){
							preparedStatement.setDouble(i+1, (double)values[i]);
						}
						else if(values[i] instanceof Boolean){
							preparedStatement.setBoolean(i+1, (boolean)values[i]);
						}
						else if(values[i] instanceof Float){
							preparedStatement.setFloat(i+1, (float)values[i]);
						}
						else if(values[i] instanceof Long){
							preparedStatement.setLong(i+1, (long)values[i]);
						}
						else if(values[i] instanceof LocalDateTime){
							preparedStatement.setTimestamp(i+1, Timestamp.valueOf((LocalDateTime)values[i]));
						}
						else{
							throw new DatabaseCriticalErrorException("Type of request parameters must be primitive!");
						}
					}
				}catch(ArrayIndexOutOfBoundsException e1){
					throw new DatabaseCriticalErrorException("Too many parameters for this type of request!");		
				}catch(SQLException e2){
					throw new DatabaseCriticalErrorException("Unexpected error occured executing the prepared statement!");
				}
			}
		}
		else{
			try{
				databaseConnector.beginTransaction();
				ResultSet resultSet = databaseQueryManager.executeQuery("getIdByTableName", tableNameOfId);
				resultSet.next();
				lastId = resultSet.getInt(1);
				databaseQueryManager.executeQuery("incrementId", tableNameOfId);
				
				preparedStatement.setInt(1, lastId);
				
				for(int i = 0; i < values.length; i++){
					if(values[i] instanceof String){
						preparedStatement.setString(i+2, (String)values[i]);
					}
					else if(values[i] instanceof Integer){
						preparedStatement.setInt(i+2, (int)values[i]);
					}
					else if(values[i] instanceof Double){
						preparedStatement.setDouble(i+2, (double)values[i]);
					}
					else if(values[i] instanceof Boolean){
						preparedStatement.setBoolean(i+2, (boolean)values[i]);
					}
					else if(values[i] instanceof Float){
						preparedStatement.setFloat(i+2, (float)values[i]);
					}
					else if(values[i] instanceof Long){
						preparedStatement.setLong(i+2, (long)values[i]);
					}
					else if(values[i] instanceof LocalDateTime){
						preparedStatement.setTimestamp(i+2, Timestamp.valueOf((LocalDateTime)values[i]));
					}
					else{
						throw new DatabaseCriticalErrorException("Type of request parameters must be primitive!");
					}
				}
			}catch(ArrayIndexOutOfBoundsException e1){
				throw new DatabaseCriticalErrorException("Too many parameters for this type of request!");		
			}catch(SQLException e2){
				throw new DatabaseCriticalErrorException("Unexpected error occured executing the prepared statement!");
			}
			finally{
				try{
					databaseConnector.commitTransaction();
				}
				catch(SQLException e){
					try {
						databaseConnector.rollbackTransaction();
					} catch (SQLException e1) {
						throw new DatabaseCriticalErrorException("Could not rollback transactions");
					}
					throw new DatabaseCriticalErrorException("Could not commit transactions and activate auto commit!");
				}
			}
		}
		
		try{
			if(returnsResultSet){
				return preparedStatement.executeQuery();
			}else{
				preparedStatement.executeUpdate();
				return null;
			}
		}catch(SQLException e){
			throw new DatabaseCriticalErrorException(
				"An error occured in the database or there are not enough parameteres for this type of request!", e
			);
		}
	}
	
	public int getLastInsertId() {
		return lastId;
	}
}
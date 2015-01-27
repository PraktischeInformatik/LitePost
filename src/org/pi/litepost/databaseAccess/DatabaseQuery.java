package org.pi.litepost.databaseAccess;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public class DatabaseQuery{
	private final String preparationSQL;
	private PreparedStatement preparedStatement;
	
	public DatabaseQuery(String preparationSQL){
		this.preparationSQL = preparationSQL;
		preparedStatement = null;
	}
	
	public ResultSet execute(DatabaseConnector databaseConnector, Object[] values) throws DatabaseCriticalErrorException{
		if(this.preparedStatement == null){
			try{
				this.preparedStatement = databaseConnector.createPreparedStatement(preparationSQL);
			}
			catch(SQLException e){
				throw new DatabaseCriticalErrorException("Could not create prepared statement: " + this.preparationSQL);
			}
		}
		
		try{
			for(int i = 0; i < values.length; i++){
				if(values[i] instanceof String){
					preparedStatement.setString(i, (String)values[i]);
				}
				else if(values[i] instanceof Integer){
					preparedStatement.setInt(i, (int)values[i]);
				}
				else if(values[i] instanceof Double){
					preparedStatement.setDouble(i, (double)values[i]);
				}
				else if(values[i] instanceof Boolean){
					preparedStatement.setBoolean(i, (boolean)values[i]);
				}
				else if(values[i] instanceof Float){
					preparedStatement.setFloat(i, (float)values[i]);
				}
				else if(values[i] instanceof Long){
					preparedStatement.setLong(i, (long)values[i]);
				}
				else if(values[i] instanceof LocalTime){
					preparedStatement.setTime(i, Time.valueOf((LocalTime)values[i]));
				}
				else if(values[i] instanceof LocalDate){
					preparedStatement.setDate(i, Date.valueOf((LocalDate)values[i]));
				}
				else{
					throw new DatabaseCriticalErrorException("Type of parameters must be primitive!");
				}
			}
		}
		catch(ArrayIndexOutOfBoundsException e1){
			throw new DatabaseCriticalErrorException("Too many parameters for this type of request!");		
		}
		catch(SQLException e2){
			throw new DatabaseCriticalErrorException("Unexpected error happened in the database!");
		}
		
		try{
			return preparedStatement.executeQuery();
		}
		catch(SQLException e){
			throw new DatabaseCriticalErrorException("An error occured in the database or there are not enough parameteres for this type of request!");
		}
	}
}
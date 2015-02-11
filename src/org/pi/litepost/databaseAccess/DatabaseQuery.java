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
	
	public ResultSet execute(DatabaseQueryManager databaseQueryManager, DatabaseConnector databaseConnector, Object...values) throws SQLException{
		if(this.preparedStatement == null){
			this.preparedStatement = databaseConnector.createPreparedStatement(preparationSQL);
		}
		else{
			preparedStatement.clearParameters();
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
							throw new SQLException("Type of request parameters must be primitive!");
						}
					}
				}catch(ArrayIndexOutOfBoundsException e1){
					throw new SQLException("Too many parameters for this type of request!");
				}
			}
		}
		else{
			try{
				databaseConnector.beginTransaction();
				ResultSet resultSet = databaseQueryManager.executeQuery("getIdByTableName", tableNameOfId);
				resultSet.next();
				int next_id = resultSet.getInt(1);
				databaseQueryManager.executeQuery("incrementId", tableNameOfId);
				
				preparedStatement.setInt(1, next_id);
				
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
						throw new SQLException("Type of request parameters must be primitive!");
					}
				}
			}catch(ArrayIndexOutOfBoundsException e1){
				throw new SQLException("Too many parameters for this type of request!");
			}
			finally{
				try{
					databaseConnector.commitTransaction();
				}
				catch(SQLException e){
					databaseConnector.rollbackTransaction();
					throw e;
				}
			}
		}
		
		if(returnsResultSet){
			return preparedStatement.executeQuery();
		}else{
			preparedStatement.executeUpdate();
			return null;
		}
	}
}
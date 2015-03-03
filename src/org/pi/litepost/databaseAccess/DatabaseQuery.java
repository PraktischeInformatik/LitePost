package org.pi.litepost.databaseAccess;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.stream.Stream;

public class DatabaseQuery {
	private final Boolean[] returnsResultSet;
	private final String[] SQLStatements;
	private final PreparedStatement[] preparedStatements;
	private String autoIncrementTable = null;
	
	
	public DatabaseQuery(String... SQLStatements) {
		this.SQLStatements = SQLStatements;
		returnsResultSet = Stream.of(SQLStatements)
			.map(s -> s.trim()
				.substring(0, 6)
				.equalsIgnoreCase("SELECT"))
			.toArray(Boolean[]::new);
		preparedStatements = new PreparedStatement[SQLStatements.length];
	}
	
	public DatabaseQuery autoIncrement(String tableName) {
		this.autoIncrementTable = tableName;
		return this;
	}
	
	public ResultSet execute(DatabaseQueryManager databaseQueryManager, DatabaseConnector databaseConnector, Object...values) throws SQLException{
		ResultSet result = null;
		try {
			databaseConnector.beginTransaction();
			for(int i = 0; i < preparedStatements.length; i++) {
				if(preparedStatements[i] == null || !preparedStatements[i].isClosed()) {
					preparedStatements[i] = databaseConnector.createPreparedStatement(SQLStatements[i]);
				}
				else{
					preparedStatements[i].clearParameters();
				}
	
				int offset = 1;
				//get automatic ID for first query
				if(autoIncrementTable != null && i == 0){
					offset++;
					ResultSet resultSet = databaseQueryManager.executeQuery("getIdByTableName", autoIncrementTable);
					resultSet.next();
					int next_id = resultSet.getInt(1);
					preparedStatements[i].setInt(1, next_id);
				}
				
				
				int maxParams = preparedStatements[i].getParameterMetaData().getParameterCount();
				maxParams = Math.min(maxParams, values.length);
				for(int paramIndex = 0; paramIndex < maxParams; paramIndex++){
					Object value = values[paramIndex];
					if(value instanceof String){
		               preparedStatements[i].setString(paramIndex + offset, (String)value);
		            }
		            else if(value instanceof Integer){
		               preparedStatements[i].setInt(paramIndex + offset, (int)value);
		            }
		            else if(value instanceof Double){
		               preparedStatements[i].setDouble(paramIndex + offset, (double)value);
		            }
		            else if(value instanceof Boolean){
		               preparedStatements[i].setBoolean(paramIndex + offset, (boolean)value);
		            }
		            else if(value instanceof Float){
		               preparedStatements[i].setFloat(paramIndex + offset, (float)value);
		            }
		            else if(value instanceof Long){
		               preparedStatements[i].setLong(paramIndex + offset, (long)value);
		            }
		            else if(value instanceof LocalDateTime){
		               preparedStatements[i].setTimestamp(paramIndex + offset, Timestamp.valueOf((LocalDateTime)value));
		            }
		            else{
		               throw new SQLException("Unsupported parameter type!");
		            }
				}
				
				if(returnsResultSet[i]){
					result = preparedStatements[i].executeQuery();
				}else{
					preparedStatements[i].executeUpdate();
				}
			}
			if(autoIncrementTable != null) {
				result = databaseQueryManager.executeQuery("getLastId", autoIncrementTable);
			}
		} finally {
			try{
				databaseConnector.commitTransaction();
			}
			catch(SQLException e){
				databaseConnector.rollbackTransaction();
				throw e;
			}
		}
		
		return result;
	}
}
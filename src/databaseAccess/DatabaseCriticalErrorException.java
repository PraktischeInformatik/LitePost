package databaseAccess;

public class DatabaseCriticalErrorException extends Exception {
	
	public DatabaseCriticalErrorException(String error){
		super(error);
	}
}

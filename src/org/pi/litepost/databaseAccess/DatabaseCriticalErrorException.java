package org.pi.litepost.databaseAccess;

public class DatabaseCriticalErrorException extends Exception {
	
	public DatabaseCriticalErrorException(String error){
		super(error);
	}
	
	public DatabaseCriticalErrorException(String error, Exception e){
		super(error, e);
	}
}
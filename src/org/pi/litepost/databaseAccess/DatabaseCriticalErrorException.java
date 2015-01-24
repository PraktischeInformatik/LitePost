package org.pi.litepost.databaseAccess;

public class DatabaseCriticalErrorException extends Exception {
	
	public DatabaseCriticalErrorException(String error){
		super(error);
	}
}

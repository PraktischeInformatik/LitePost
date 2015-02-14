package org.pi.litepost.exceptions;

/**
 * Exception is thrown when username already exists in database
 */
public class UseranameExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	public UseranameExistsException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UseranameExistsException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public UseranameExistsException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UseranameExistsException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UseranameExistsException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}

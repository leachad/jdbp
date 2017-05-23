package com.andrewdleach.jdbp.exception;

/**
 * Exception used to encompass the multitude
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class JdbpException extends Exception {
	private static final long serialVersionUID = 1320542324622606780L;

	/**
	 * No-argument constructor
	 */
	public JdbpException() {
		super();
	}

	/**
	 * @param message
	 *        to be recorded
	 */
	public JdbpException(String message) {
		super(message);
	}

	/**
	 * @param throwable
	 *        to be recorded
	 */
	public JdbpException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param e
	 * @throws JdbpException
	 */
	public static void throwException(Exception e) throws JdbpException {
		throw new JdbpException(e);
	}

	/**
	 * @param message
	 * @throws JdbpException
	 */
	public static void throwException(String message) throws JdbpException {
		throw new JdbpException(message);

	}

}

package exception;

/**
 * Exception used to encompass the multitude
 * 
 * @since 12.1.2016
 * @author andrew.leach
 */
public class JdbpDriverException extends Exception {
	private static final long serialVersionUID = 1320542324622606780L;

	/**
	 * No-argument constructor
	 */
	public JdbpDriverException() {
		super();
	}

	/**
	 * @param message
	 *        to be recorded
	 */
	public JdbpDriverException(String message) {
		super(message);
	}

	/**
	 * @param throwable
	 *        to be recorded
	 */
	public JdbpDriverException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param e
	 * @throws JdbpDriverException
	 */
	public static void throwException(Exception e) throws JdbpDriverException {
		throw new JdbpDriverException(e);
	}

	/**
	 * @param message
	 * @throws JdbpDriverException
	 */
	public static void throwException(String message) throws JdbpDriverException {
		throw new JdbpDriverException(message);

	}

}

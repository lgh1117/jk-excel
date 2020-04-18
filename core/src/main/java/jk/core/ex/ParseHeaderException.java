/**
 * 
 */
package jk.core.ex;

/**
 * @author Jack lee
 *
 */
public class ParseHeaderException extends ExcelParseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8253740314608969014L;

	/**
	 * 
	 */
	public ParseHeaderException() {
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ParseHeaderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public ParseHeaderException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ParseHeaderException(Throwable cause) {
		super(cause);
	}

}

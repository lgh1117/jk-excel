package jk.core.ex;

public class ExportException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 208271983879880060L;

	public ExportException() {
	}

	public ExportException(String message) {
		super(message);
	}

	public ExportException(Throwable cause) {
		super(cause);
	}

	public ExportException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExportException(String message, Throwable cause,
						   boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

package org.springfield.mojo.cor;

/**
 * Container for status messages
 * 
 * @param <R>
 */
public class Status<R extends Request> {
	
	/** status code constants */
	public static final int HANDLER_FINISHED = 0;
	public static final int COR_FINISHED = 1;
	public static final int HANDLER_FAILED = 2;
	public static final int COR_FAILED = 3;
	public static final int RUNNING = 4;
	
	/** Source of status message*/
	private Handler<R> source;
	
	/** Request that is being handled*/
	private R request;
	
	/** status code */
	private int code;
	
	/** status message */
	private String message;
	
	public Status(Handler<R> source, R request, int code, String message) {
		this.source = source;
		this.request = request;
		this.code = code;
		this.message = message;
	}

	/**
	 * @return the source
	 */
	public Handler<R> getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Handler<R> source) {
		this.source = source;
	}

	/**
	 * @return the request
	 */
	public R getRequest() {
		return request;
	}

	/**
	 * @param request the request to set
	 */
	public void setRequest(R request) {
		this.request = request;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}

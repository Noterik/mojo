package org.springfield.mojo.cor;

import java.util.Observable;

import org.apache.log4j.Logger;

/**
 * Handler in the Chain of Responsibility
 * 
 * TODO: move retry functionality to a external Observer
 */
public abstract class Handler <R extends Request> extends Observable {
	/** The Handler's log4j Logger */
	protected static Logger logger = Logger.getLogger(Handler.class);
	
	/** next Handler in the chain */
	protected Handler<R> successor;
	
	/** number of times to retry any operation */
	private static final int MAX_TRIES = 5;
	
	/** time between tries */
	private static final long TIME_TO_WAIT = 5000;
	
	/**
	 * Handle a request
	 * 
	 * @param request
	 */
	public void handleRequest(R request) {
		boolean success = false;
		int numTries = 0;
		while(!success) {
			success = doHandle(request);
			if(!success) {
				// break chain if number of tries exceeds limit
				if(++numTries >= MAX_TRIES) {
					updateStatus(request, Status.COR_FAILED,"Handler.handleRequest: failure ("+numTries+")in " + this.getClass().getName()+" EXIT");
					return;
				} 
				// sleep some time before trying again
				else {
					updateStatus(request, Status.RUNNING,"Handler.handleRequest: failure ("+numTries+")in " + this.getClass().getName()+" RETRY");
					try{
						Thread.sleep(TIME_TO_WAIT);
					} catch(Exception e) { /* not a problem */ }
				}
			}
		}
		
		if(successor!=null) {
			successor.handleRequest(request);
		} else {
			updateStatus(request, Status.COR_FINISHED, "Chain finished");
		}
	}
	
	/**
	 * Handle the Request
	 * 
	 * @param  request 	The request to handle
	 * @return success
	 */
	protected abstract boolean doHandle(R request);
	
	/**
	 * @param successor the successor to set
	 */
	public void setSuccessor(Handler<R> successor) {
		this.successor = successor;
	}
	
	/**
	 * Update the status message
	 * Generates status object and notifies all observers.
	 * 
	 * @param statusMessage
	 */
	public void updateStatus(R request, int statusCode, String statusMessage) {
		Status<R> status = new Status<R>(this, request, statusCode, statusMessage);
		setChanged();
		notifyObservers(status);
	}
}

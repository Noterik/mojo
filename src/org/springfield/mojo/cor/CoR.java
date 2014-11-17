package org.springfield.mojo.cor;


import java.util.Observable;
import java.util.Observer;

/**
 * Implements a generic CoR (chain of responsibility), as a linked list
 */
public class CoR <H extends Handler<R>, R extends Request, S extends Status<R>> extends Observable implements Observer {

	private Handler<R> first;
	private Handler<R> last;
	
	public CoR() {}
	
	/**
	 * Push ingest handler to the end of the CoR
	 * 
	 * @param handler
	 */
	public void pushHandler(Handler<R> handler) {
		synchronized(this) {
			// determine first and last handler
			if(first==null) {
				first = handler;
				last = handler;
			} else {
				last.setSuccessor(handler);
				last = handler;
			}
			
			// add observer to handler
			handler.addObserver(this);
		}
	}
	
	/**
	 * Start the CoR (chain of responsibility)
	 * 
	 * @param request
	 */
	public void start(R request) {
		first.handleRequest(request);
	}

	/**
	 * Delegate status message to Observers of this class
	 */
	public void update(Observable observable, Object o) {
		if(o instanceof Status) {
			setChanged();
			notifyObservers(o);
		}
	}

}

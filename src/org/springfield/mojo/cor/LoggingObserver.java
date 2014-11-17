package org.springfield.mojo.cor;

import java.util.Observable;
import java.util.Observer;
import org.apache.log4j.Logger;

/**
 * Observer that logs every IngestStatus message
 */
public class LoggingObserver implements Observer {
	/** the LoggingObserver's log4j Logger */
	private static Logger logger = Logger.getLogger(LoggingObserver.class);
	
	public void update(Observable observable, Object o) {
		if(o instanceof Status) {
			Status status = (Status) o;
			Logger sourceLogger = Logger.getLogger(status.getSource().getClass());
			// log error on failed status
			switch(status.getCode()) {
			case Status.HANDLER_FAILED:
				sourceLogger.error(status.getMessage());
				break;
			case Status.COR_FAILED:
				sourceLogger.fatal(status.getMessage());
				break;
			default:
				sourceLogger.info(status.getMessage());
			}
		} else {
			logger.error("Object was not an instance of Status: "+o);
		}
	}

}


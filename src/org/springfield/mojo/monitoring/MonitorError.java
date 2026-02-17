package org.springfield.mojo.monitoring;

public class MonitorError {

	private String error="";
	private long time = -1;
	
	public void setMsg(String e) {
		error = e;
	}
	
	public void setTime(long t) {
		time = t;
	}
}

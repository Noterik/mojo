package org.springfield.mojo.monitoring;

public class MonitorThread extends Thread {
		
		public static boolean running = false;
		
		public MonitorThread() {
			super("monitor thread");
			running = true;
			start();
		}
		
		public void shutdown() {
			running = false;
		}
		
		public void run() {
			while (running) {
				try {	
					MonitorManager.reportTimes();
					MonitorManager.callBacks();
					Thread.sleep(10*1000);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
}

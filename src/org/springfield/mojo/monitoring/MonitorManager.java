package org.springfield.mojo.monitoring;

import java.io.FileWriter;
import java.util.*;
import org.springfield.fs.*;

public class MonitorManager {
	
	private static MonitorThread monitorthread;
	
	private static Map<String, MonitorCallbackInterface> watchers = new HashMap<String, MonitorCallbackInterface>();
	private static Map<String, MonitorAction> actions = new HashMap<String, MonitorAction>();
	
	
	public static void start() {
		if (monitorthread==null) monitorthread = new MonitorThread(); // should be moved
	}
	
	public static void shutdown() {
		if (monitorthread!=null) {
			monitorthread.shutdown();
			monitorthread=null;
		}
	}
	
	public static void addWatcher(String watch,MonitorCallbackInterface watcher) {
		watchers.put(watch, watcher);
	}
	
	public static void logAction(String a,long token) {
		MonitorAction action = actions.get(a);
		action.logAction(token);
	}
	
	public static void logError(String a,String error) {
		MonitorAction action = actions.get(a);
		if (action!=null) { 
			// create the new error
			MonitorError newerror = new MonitorError();
			newerror.setMsg(error);
			newerror.setTime(new Date().getTime());
			action.logError(newerror);
		}
	}
	
	public static long getStartToken(String a,boolean track) {
		MonitorAction action = actions.get(a);
		if (action==null) {
			action = new MonitorAction(a);
			actions.put(a, action);
		}
		return action.addStartCounter(track);
	}
	
	public static long getStartToken(String a) {
		return getStartToken(a,false);
	}
	
	public static void callBacks() {
		for (Map.Entry<String, MonitorAction> entry : actions.entrySet()) {
		    MonitorAction a = entry.getValue();
		    // do we have a watcher ?
		    MonitorCallbackInterface watcher = watchers.get(a.getName());
		    if (watcher!=null) {
		    	// do we allways callback ?
		    	watcher.monitorCallback(a);
		    }
		}
	}
	
	public static void reportTimes() {
	    String body="<!DOCTYPE html>";
	    body+="<html><head><link rel=\"stylesheet\" href=\"css/times.css\"></head><body>";
	    body+="<table><tr><th>name</th><th>total</th><th>10 buckets</th><th>20/50/100 avg</tr>";
	    
	     ArrayList<String> skeys = new ArrayList<String>(actions.keySet());
	     Collections.sort(skeys,String.CASE_INSENSITIVE_ORDER);
	    
	    for (String item : skeys) {
			MonitorAction a = actions.get(item);
		    body+="<tr><td>"+a.getName()+"</td><td>"+a.getAvgTime()+"("+a.getStartCounter()+"/"+a.getEndCounter()+")</td><td>"+a.getBucketString()+"</td><td>"+a.getAvgTimesString()+"</td></tr>";
		}
	    body+="<tr><th>cache</th><th>total/hit</th><th>size</th><th>hitrate</th></tr>";
		body+="<tr><td>get node</td><td> C="+FsNodeCache.getTotalRequests()+" H="+FsNodeCache.getHitRequests()+"</td><td>"+FsNodeCache.size()+"</td><td>%="+FsNodeCache.getHitRate()+"</td></tr>";
	    body+="<tr><th>timeouts</th><th>starttime</th><th>duration</th><th>handler</th></tr>";
		long now = new Date().getTime();
		for (Map.Entry<String, MonitorAction> entry : actions.entrySet()) {
		    MonitorAction a = entry.getValue();
		    ArrayList<Long>activelist = a.getActiveList();
		    if (activelist.size()>0) {
		    	for (int i=0;i<activelist.size();i++) {
		    		long time = activelist.get(i);
		    		
		    		int delta = (int)(now-time)/1000;
					int hours = delta  / 3600;
					int minutes = (delta % 3600) / 60;
					int seconds = delta % 60;
					String stime = String.format("%02dh%02dm%02ds", hours, minutes, seconds);
					if (hours==0) stime = String.format("%02dmin %02dsec", minutes, seconds);
				    body+="<tr><td>"+a.getName()+"</td><td>"+new Date(time).toLocaleString()+"</td><td>"+stime+"</td><td>--</td></tr>";	
		    	}
		    }
		}
	
		body+="</table></body></html>";
	    try {
	    	FileWriter filewriter = new FileWriter("/springfield/tomcat/webapps/ROOT/eddie/times.html", false);
	    	filewriter.write(body);
	    	filewriter.flush();
	    	filewriter.close();
	    } catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
	
}

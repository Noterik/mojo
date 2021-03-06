/* 
* LazyMarge.java
* 
* Copyright (c) 2012 Noterik B.V.
* 
* This file is part of Lou, related to the Noterik Springfield project.
*
* Lou is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Lou is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Lou.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.springfield.marge;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;


/**
 * Marge
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.homer
 *
 */
public class Marge extends Thread {
	
	String group = "224.0.0.0";
	int errorcounter = 0;
	int errorcounter2 = 0;
	private static boolean running = false;
	private static Map<String, ArrayList<MargeObserver>> observers = new HashMap<String,ArrayList<MargeObserver>>();
	private static enum methods { GET,POST,PUT,DELETE,INFO,TRACE,LINK,AUTH,PAUTH; }
	private static MargeTimerThread timerthread = null;
	MulticastSocket s = null;
	private static Marge instance = null;
	
	public Marge() {
		if (!running) {
			running = true;
			start();
		}
		if (timerthread==null) {
			timerthread = new MargeTimerThread();
		}
	}
	
    public static Marge instance(){
    	if(instance==null) instance = new Marge();
    	return instance;
    }
	
	public static void addObserver(String url,MargeObserver o) {
		if (url==null || url.equals("")) return;
		
		ArrayList cur = observers.get(url);
		if (cur!=null) {
			cur.add(o);
		} else {
			cur = new ArrayList<MargeObserver>();
			cur.add(o);
			observers.put(url, cur);
		}
	}
	
	public static void removeObserver(MargeObserver o) {
		int total = 0;
		String removekey = null;
		Set<String> keys = observers.keySet();
		Iterator<String> it = keys.iterator();
		while(it.hasNext()){
			String key = it.next();
			ArrayList cur = observers.get(key);
			cur.remove(o);
			total += cur.size();
			if (cur.size()==0) {
				removekey = key;
			}
		}
		if (removekey!=null) observers.remove(removekey);
		
	}
	
	public static void addTimedObserver(String url,int counter,MargeObserver o) {
		if (timerthread!=null) timerthread.addTimedObserver(url,counter,o);
	}
	
	public void run() {
		try {
			s = new MulticastSocket(getPort()); // why is this hardcoded ? daniel. 
			s.joinGroup(InetAddress.getByName(group));
			byte[] buffer = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buffer, 1024);
			while (running) {
				try {
					dp.setLength(1024);
					s.receive(dp);
					byte[] message = new byte[dp.getLength()];
					System.arraycopy(dp.getData(), 0, message, 0, dp.getLength());
					String line = new String(message);
					//System.out.println("marge line="+line);
					String[] result = new String(message).split("\\s");
					switch (methods.valueOf(result[1])) {
					case POST :
						signalObservers(result[0],result[1],result[2]);
						break;
					case PUT :
						signalObservers(result[0],result[1],result[2]);
						break;
					case DELETE :
						signalObservers(result[0],result[1],result[2]);
						break;
					case TRACE :
						signalObservers(result[0],result[1],result[2]);
						break;
					case LINK :
						signalObservers(result[0],result[1],result[2]+" "+result[3]+" "+result[4]);
						break;
					case INFO :
						break;
					}
				} catch(Exception e2) {	
					if (running) {
						if (errorcounter<10) {
							errorcounter++;
							e2.printStackTrace();
						}
					}
				}
			}
		} catch(Exception e) {
			if (running) {
				if (errorcounter2<10) {
					errorcounter2++;
					e.printStackTrace();
				}
			}
		}
	}
	
	private void signalObservers(String from,String method,String url) {
		String ourl = url;		
		int pos = url.lastIndexOf("/");
		while (pos!=-1) { // we need to walk up the tree to check
			// only works for one entry now, daniel (no dubs in urls)
			
			// check for direct matches
			Object obs = observers.get(url);
			if (obs!=null) {
				ArrayList<MargeObserver> list = (ArrayList<MargeObserver>)obs;
				for(MargeObserver caller: list){
					caller.remoteSignal(from, method, url);
				}
			}			

			url = url.substring(0,pos);
			pos = url.lastIndexOf("/");
		}
		
		// more complex matches won't be found so we have to check them one by one
		for (Iterator<String> i = observers.keySet().iterator(); i.hasNext();) {
			String key = i.next();
			if (key.indexOf("*")!=-1) { // its a complex one so check
				if (isObserverMatch(key,ourl)) {
					Object obs = observers.get(key);
					if (obs!=null) {
						ArrayList<MargeObserver> list = (ArrayList<MargeObserver>)obs;
						for(MargeObserver caller: list){
							caller.remoteSignal(from, method, ourl+","+key);
						}
					}
				}
			}
		}
		
	}
	
	private boolean isObserverMatch(String key,String url) {
		String[] keya= key.split("/");
		String[] urla= url.split("/");
		for (int i=1;i<keya.length;i++) { // we need to match each part all must be valid
			String skey = keya[i];
			if (urla.length-1<i) return false;  
	
			String surl = urla[i];
			// if we don't have a surl we always fail
			if (surl==null) {
				return false;
			} else if (!skey.equals("*") && !skey.equals(surl)) {
				return false;
			}
		}
		return true;
	}

    /**
     * Shutdown
     */
	public void destroy() {
		running = false;
		if (timerthread!=null) {
			timerthread.destroy();
		}
		running = false;
		if (s!=null) s.close();
	}	
	
	private int getPort() {
		// properties
		Properties props = new Properties();
		
		// new loader to load from disk instead of war file
		String configfilename = "/springfield/homer/config.xml";
		// load from file
		try {
			System.out.println("INFO: Loading config file from load : "+configfilename);
			File file = new File(configfilename);

			if (file.exists()) {
				props.loadFromXML(new BufferedInputStream(new FileInputStream(file)));
			} else { 
				System.out.println("FATAL: Could not load config "+configfilename);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int port = -1;
		try {
			port = Integer.parseInt(props.getProperty("marge-port"));
		} catch(Exception e) {
			System.out.println("MOJO CAN'T READ PORT NUMBER FROM homer/config.xml");
		}
		return port;
	}

}

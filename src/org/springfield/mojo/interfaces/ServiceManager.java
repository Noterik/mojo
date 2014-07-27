package org.springfield.mojo.interfaces;

import java.util.*;

import org.springfield.fs.FSList;
import org.springfield.fs.FSListManager;
import org.springfield.fs.FsNode;

public class ServiceManager {
	
	private static HashMap<String, ServiceInterface> services = new HashMap<String, ServiceInterface>();
	private static HashMap<String, HashMap<String, ServiceInterface>> proxies = new HashMap<String,HashMap<String,ServiceInterface>>();
	private static ServiceManager instance;

		public static ServiceInterface getService(String name) {
			//System.out.println("GET SERVICE INTERFACE = "+name+" M="+instance);
			int retryCounter = 0; // we retry a few times incase the service was still booting
			ServiceInterface s = services.get(name);
			while (s==null && retryCounter<20) {
				retryCounter++;
				try {
					Thread.sleep(500*retryCounter);
				} catch(Exception e) {}
					//System.out.println("retrying to access "+name+" retry count "+retryCounter);
				s = services.get(name);
			}
			return s;
		}
		
		public static ServiceInterface getService(String name,String ipnumber) {
			// get the proxy map for this service
			Map<String, ServiceInterface> list = proxies.get(name);
			
			if (list==null) {
				startProxies(name);
				list = proxies.get(name);
			}
			
			if (list!=null) { // if ok now continue
				return list.get(ipnumber);
			}
			
			return null;
		}
		
		
		public static void setService(ServiceInterface s) {
			if (instance==null) instance =  new ServiceManager();
			//System.out.println("SET SERVICE INTERFACE = "+s.getName()+" M="+instance);
			services.put(s.getName(),s);
		}
		
		private static void startProxies(String name) {
			// lets talk to smithers 
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers!=null) {
				HashMap<String, ServiceInterface> plist = new HashMap<String, ServiceInterface>();
				FSList fslist = FSListManager.get("/domain/internal/service/"+name+"/nodes",0,false);
				for(Iterator<FsNode> iter = fslist.getNodes().iterator(); iter.hasNext(); ) {
					FsNode n = (FsNode)iter.next();
					if (n.getName().equals("nodes")) { // lets make sure its a node 
						String ipnumber = n.getId();
						ServiceInterface newproxy = new ServiceProxy(name, ipnumber,"8081"); // need to store port
						plist.put(ipnumber,newproxy);
					}
				}
				proxies.put(name, plist);
			}
		}
		
		
}

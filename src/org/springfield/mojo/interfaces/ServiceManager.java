package org.springfield.mojo.interfaces;

import java.util.*;

public class ServiceManager {
	
	private static Map<String, ServiceInterface> services = new HashMap<String, ServiceInterface>();
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
				System.out.println("retrying to access "+name+" retry count "+retryCounter);
				s = services.get(name);
			}
			return s;
		}
		
		public static void setService(ServiceInterface s) {
			if (instance==null) instance =  new ServiceManager();
			System.out.println("SET SERVICE INTERFACE = "+s.getName()+" M="+instance);
			services.put(s.getName(),s);
		}
		
		
}

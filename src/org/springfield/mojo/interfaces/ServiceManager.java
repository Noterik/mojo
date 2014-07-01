package org.springfield.mojo.interfaces;

import java.util.*;

public class ServiceManager {
	
	private static Map<String, ServiceInterface> services = new HashMap<String, ServiceInterface>();
	private static ServiceManager instance;

		public static ServiceInterface getService(String name) {
			//System.out.println("GET SERVICE INTERFACE = "+name+" M="+instance);
			ServiceInterface s = services.get(name);
			return s;
		}
		
		public static void setService(ServiceInterface s) {
			if (instance==null) instance =  new ServiceManager();
			System.out.println("SET SERVICE INTERFACE = "+s.getName()+" M="+instance);
			services.put(s.getName(),s);
		}
		
		
}

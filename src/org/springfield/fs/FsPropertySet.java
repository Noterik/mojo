package org.springfield.fs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FsPropertySet {
	private Map<String, String> properties = new HashMap<String, String>();
	
	public void setProperty(String name,String value) {
		properties.put(name, value);
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}
	
	public Iterator<String> getKeys() {
		return properties.keySet().iterator();
	}
	
	public int size() {
		return properties.size();
	}
	
	
}

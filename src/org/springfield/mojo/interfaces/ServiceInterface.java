package org.springfield.mojo.interfaces;

public interface ServiceInterface {
	public String getName();
	public String get(String path,String fsxml,String mimetype);
	public String put(String path,String value,String mimetype);
}

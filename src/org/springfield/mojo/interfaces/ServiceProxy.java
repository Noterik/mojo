package org.springfield.mojo.interfaces;

import org.springfield.mojo.http.HttpHelper;
import org.springfield.mojo.http.Response;

public class ServiceProxy implements ServiceInterface {
	
	private String name = null;
	private String ipnumber = null;
	private String port = null;
	private String auth = null;
	
	public ServiceProxy(String name,String ipnumber, String port) {
		this.name = name;
		this.ipnumber = ipnumber;
		this.port = port;
		System.out.println("Creating proxy "+name+" ipnumber "+ipnumber+" "+port);
	}
	
	public String getName() {
		return name;
	}
	
	public String get(String path,String fsxml,String mimetype) {
		if (auth==null) {
			auth = getAuth();
		}
		String url = "http://"+ipnumber+":"+port+"/"+name+"/proxy/"+path;
		if (url.indexOf("?")!=-1) {
			url+= "&spw="+auth;
		} else {
			url+= "?spw="+auth;
		}
		System.out.println("PROXY URL: " + url);
		Response result = HttpHelper.sendRequest("GET",url,fsxml);
		if (result!=null) {
			int status = result.getStatusCode();
			if (status==200) return result.getResponse();
			if (status==410) {
				auth = getAuth(); // get the new auth !
				url = "http://"+ipnumber+":"+port+"/"+name+"/proxy/"+path;
				if (url.indexOf("?")!=-1) {
					url+= "&spw="+auth;
				} else {
					url+= "?spw="+auth;
				}
				result = HttpHelper.sendRequest("GET",url,fsxml);
				if (result!=null) {
					if (status==200) return result.getResponse();
				}
			}
		}
		return null;
	}
	
	public String put(String path,String fsxml,String mimetype) {
    	    if (auth==null) {
    		auth = getAuth();
        	}
        	String url = "http://"+ipnumber+":"+port+"/"+name+"/proxy/"+path;
        	if (url.indexOf("?")!=-1) {
        		url+= "&spw="+auth;
        	} else {
        		url+= "?spw="+auth;
        	}
        	System.out.println("PROXY URL: " + url);
        	Response result = HttpHelper.sendRequest("PUT",url,fsxml);
        	if (result!=null) {
        		int status = result.getStatusCode();
        		if (status==200) return result.getResponse();
        		if (status==410) {
        			auth = getAuth(); // get the new auth !
        			url = "http://"+ipnumber+":"+port+"/"+name+"/proxy/"+path;
        			if (url.indexOf("?")!=-1) {
        				url+= "&spw="+auth;
        			} else {
        				url+= "?spw="+auth;
        			}
        			result = HttpHelper.sendRequest("PUT",url,fsxml);
        			if (result!=null) {
        				if (status==200) return result.getResponse();
        			}
        		}
        	}
            return null;
	}
	
	public String post(String path,String fsxml,String mimetype) {
	    if (auth==null) {
    		auth = getAuth();
        	}
        	String url = "http://"+ipnumber+":"+port+"/"+name+"/proxy/"+path;
        	if (url.indexOf("?")!=-1) {
        		url+= "&spw="+auth;
        	} else {
        		url+= "?spw="+auth;
        	}
        	System.out.println("PROXY URL: " + url);
        	Response result = HttpHelper.sendRequest("POST",url,fsxml);
        	if (result!=null) {
        		int status = result.getStatusCode();
        		if (status==200) return result.getResponse();
        		if (status==410) {
        			auth = getAuth(); // get the new auth !
        			url = "http://"+ipnumber+":"+port+"/"+name+"/proxy/"+path;
        			if (url.indexOf("?")!=-1) {
        				url+= "&spw="+auth;
        			} else {
        				url+= "?spw="+auth;
        			}
        			result = HttpHelper.sendRequest("POST",url,fsxml);
        			if (result!=null) {
        				if (status==200) return result.getResponse();
        			}
        		}
        	}
            return null;
	}
	
	public String delete(String path,String fsxml,String mimetype) {
	    if (auth==null) {
    		auth = getAuth();
        	}
        	String url = "http://"+ipnumber+":"+port+"/"+name+"/proxy/"+path;
        	if (url.indexOf("?")!=-1) {
        		url+= "&spw="+auth;
        	} else {
        		url+= "?spw="+auth;
        	}
        	System.out.println("PROXY URL: " + url);
        	Response result = HttpHelper.sendRequest("DELETE",url,fsxml);
        	if (result!=null) {
        		int status = result.getStatusCode();
        		if (status==200) return result.getResponse();
        		if (status==410) {
        			auth = getAuth(); // get the new auth !
        			url = "http://"+ipnumber+":"+port+"/"+name+"/proxy/"+path;
        			if (url.indexOf("?")!=-1) {
        				url+= "&spw="+auth;
        			} else {
        				url+= "?spw="+auth;
        			}
        			result = HttpHelper.sendRequest("DELETE",url,fsxml);
        			if (result!=null) {
        				if (status==200) return result.getResponse();
        			}
        		}
        	}
            return null;
	}
	
	private String getAuth() {
		ServiceInterface barney = ServiceManager.getService("barney");
		String result = barney.get("getserviceauth("+ipnumber+")",null,null);
		System.out.println("PROXY AUTH="+result);
		return result;
	}
	
}

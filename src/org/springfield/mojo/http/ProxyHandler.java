package org.springfield.mojo.http;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springfield.mojo.interfaces.ServiceInterface;
import org.springfield.mojo.interfaces.ServiceManager;

public class ProxyHandler {
	
	public static void get(String name,HttpServletRequest request, HttpServletResponse response) {
		// lets check if the request is signed
		String spw = request.getParameter("spw");
		if (spw==null || spw.equals("")) {
			response.setContentType("text/xml; charset=UTF-8");
			response.setStatus(410);
			return; 
		}
		try {
			ServiceInterface barney = ServiceManager.getService("barney");
			if (barney==null) return;
			String valid = barney.get("valid_spw("+spw+")",null,null);
			if (valid==null || valid.equals("false")) {
				response.setContentType("text/xml; charset=UTF-8");
				response.setStatus(410);
				return;
			}
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		// kind of weird to get the local service like this should demand local at some point
		ServiceInterface service = ServiceManager.getService(name);
		if (service!=null) {
			String body = service.get(request.getRequestURI(),null, null);
			try {
				response.setContentType("text/xml; charset=UTF-8");
				OutputStream out = response.getOutputStream();
				out.write(body.getBytes());
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			// return a error 500 ?
		}
	}
	
}

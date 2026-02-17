package org.springfield.mojo.http;

import java.io.BufferedReader;
import java.io.OutputStream;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
			String uri = request.getRequestURI();
			String body = service.get(uri.substring(uri.indexOf("/proxy/")+7),null, null);
			try {
				response.setContentType("text/xml; charset=UTF-8");
				OutputStream out = response.getOutputStream();
				out.write(body.getBytes());
				out.flush();
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			// return a error 500 ?
		}
	}
	
	public static void post(String name,HttpServletRequest request, HttpServletResponse response) {
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
			
			/*
			String valid = barney.get("valid_spw("+spw+")",null,null);
			if (valid==null || valid.equals("false")) {
				response.setContentType("text/xml; charset=UTF-8");
				response.setStatus(410);
				return;
			}
			*/
			if (spw==null || spw.equals("") || !spw.equals("asdads")) {
				response.setContentType("text/xml; charset=UTF-8");
				response.setStatus(410);
				String body="{ \"http-error\": \"410\", \"reason\": \"access refused\" }";
				OutputStream out = response.getOutputStream();
				if (body!=null) out.write(body.getBytes());
				out.flush();
				out.close();
				return;
			}
			
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
			return;
		}
		
		// kind of weird to get the local service like this should demand local at some point
		ServiceInterface service = ServiceManager.getService(name);
		if (service!=null) {
			String uri = request.getRequestURI();
			StringBuilder sb = new StringBuilder();
			try {
				BufferedReader br = request.getReader();
				for(String line; (line=br.readLine())!=null;){
					sb.append(line).append("\n");
				}
			 } catch (Exception e) {
			        System.out.println("POST READ ERROR");
			        e.printStackTrace();
			 }
			String incb = sb.toString();
			String body = service.post(uri.substring(uri.indexOf("/proxy/")+7),incb, null);
			//System.out.println("BODY="+body);
			try {
				JSONObject returndata = (JSONObject)new JSONParser().parse(body);
				String httperror = (String)returndata.get("http-error");
				if (httperror!=null) {
					response.setStatus(Integer.parseInt(httperror));
				}
				response.setContentType("text/xml; charset=UTF-8");
				OutputStream out = response.getOutputStream();
				if (body!=null) out.write(body.getBytes());
				out.flush();
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			// return a error 500 ?
		}
	}
	
}

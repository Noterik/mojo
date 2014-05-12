/* 
* HttpHelper.java
* 
* Copyright (c) 2008 - 2014 Noterik B.V.
* 
* This file is part of Mojo, related to the Noterik Springfield project.
*
* Mojo is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Mojo is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Mojo.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.springfield.mojo.http;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.methods.OptionsMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.TraceMethod;
import org.springfield.mojo.http.methods.HttpMethods;

/**
 * Helper class for basic HTTP request functionality.
 * Uses the methods supported in the package 
 * org.apache.commons.httpclient.methods and supports
 * GET, PUT, POST, DELETE, HEAD, TRACE, OPTIONS
 * 
 * @author Jaap Blom
 * @author Levi Pires
 * @author Derk Crezee
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2008 - 2014
 * @package org.springfield.mojo.http
 * 
 */
public class HttpHelper {

	private static final String DEFAULT_CONTENT_TYPE = "text/html";
	private static final String DEFAULT_REST_CONTENT_TYPE = "text/xml";
	private static final String DEFAULT_CHARACTER_SET = "UTF-8";

	/**
	 * Sends a standard HTTP request to the specified URI using the determined method.
	 * 
	 * @param method - the request method
	 * @param uri - the uri to request
	 * @return response
	 */
	public static Response sendRequest(String method, String uri) {
		return sendRequest(method, uri, null);
	}
	
	/**
	 * Sends a REST request (text/xml) to the specified URI using the determined method.
	 * 
	 * @param method - the request method
	 * @param uri - the uri to request
	 * @return response
	 */
	public static Response sendRestRequest(String method, String uri) {
		return sendRestRequest(method, uri, null);
	}
	
	/**
	 * Sends a standard HTTP request to the specified URI using the determined method.
	 * Also the content will be sent attached.
	 * 
	 * @param method - the request method
	 * @param uri - the uri to request
	 * @param body - the content  
	 * @return response     
	 */
	public static Response sendRequest(String method, String uri, String body) {
		return sendRequest(method, uri, body, DEFAULT_CONTENT_TYPE);	
	}
	
	/**
	 * Sends a REST request (text/xml) to the specified URI using the determined method.
	 * 
	 * @param method - the request method
	 * @param uri - the uri to request
	 * @param body - the content
	 * @return response
	 */
	public static Response sendRestRequest(String method, String uri, String body) {
		return sendRequest(method, uri, body, DEFAULT_REST_CONTENT_TYPE);
	}
	
	/**
	 * Sends a standard HTTP request to the specified URI using the determined method.
	 * Attaches the content and uses the specified content type
	 * 
	 * @param method - the request method
	 * @param uri - the uri to request
	 * @param body  - the content  
	 * @param contentType - the content type
	 * @return response
	 */
	public static Response sendRequest(String method, String uri, String body, String contentType) {
		return sendRequest(method, uri, body, contentType, null, -1, DEFAULT_CHARACTER_SET);
	}
	
	/**
	 * Sends a standard HTTP request to the specified URI using the determined method.
	 * Attaches the content, uses the specified content type and sets the timeout
	 * 
	 * @param method - the request method
	 * @param uri - the uri to request
	 * @param body - the content  
	 * @param contentType - the content type
	 * @param timeout - timeout in milliseconds
	 * @return response
	 */
	public static Response sendRequest(String method, String uri, String body, String contentType, int timeout) {
		return sendRequest(method, uri, body, contentType, null, timeout, DEFAULT_CHARACTER_SET);
	}

	/**
	 * Sends a standard HTTP request to the specified URI using the determined method.
	 * Attaches the content, uses the specified content type and sets cookies
	 * 
	 * @param method - the request method
	 * @param uri - the uri to request
	 * @param body - the content  
	 * @param contentType - the content type
	 * @param cookies - cookies
	 * @return response
	 */
	public static Response sendRequest(String method, String uri, String body, String contentType, String cookies) {
		return sendRequest(method, uri, body, contentType, cookies, -1, DEFAULT_CHARACTER_SET);
	}
	
	/**
	  * Sends a standard HTTP request to the specified URI using the determined method.
	 * Attaches the content, uses the specified content type, sets cookies and timeout
	 *  
	 * @param method - the request method
	 * @param uri - the uri to request
	 * @param body - the content  
	 * @param contentType - the content type
	 * @param cookies - cookies
	 * @param timeout - timeout in milliseconds
	 * @param charSet - the character set
	 * @return response
	 */
	public static Response sendRequest(String method, String uri, String body, String contentType, String cookies, int timeout, String charSet) {
		// http client
		HttpClient client = new HttpClient();

		// method
		HttpMethodBase reqMethod = null;
		if (method.equals(HttpMethods.PUT)) {
			reqMethod = new PutMethod(uri);
		} else if (method.equals(HttpMethods.POST)) {
			reqMethod = new PostMethod(uri);
		} else if (method.equals(HttpMethods.GET)) {
			if( body != null ) {
				// hack to be able to send a request body with a get (only if required)
				reqMethod = new PostMethod(uri) {
					public String getName() {
						return "GET";
					}
				};
			} else {
				reqMethod = new GetMethod(uri);
			}
		} else if (method.equals(HttpMethods.DELETE)) {
			if( body != null ) {
				// hack to be able to send a request body with a delete (only if required)
				reqMethod = new PostMethod(uri) {
					public String getName() {
						return "DELETE";
					}
				};
			} else {
				reqMethod = new DeleteMethod(uri);
			}
		} else if (method.equals(HttpMethods.HEAD)) {
			reqMethod = new HeadMethod(uri);
		} else if (method.equals(HttpMethods.TRACE)) {
			reqMethod = new TraceMethod(uri);
		} else if (method.equals(HttpMethods.OPTIONS)) {
			reqMethod = new OptionsMethod(uri);
		}

		// add request body
		if (body != null) {
			try {
				RequestEntity entity = new StringRequestEntity(body, contentType, charSet);
				((EntityEnclosingMethod)reqMethod).setRequestEntity(entity);
				reqMethod.setRequestHeader("Content-type", contentType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// add cookies
		if (cookies != null)
			reqMethod.addRequestHeader("Cookie", cookies);
		
		Response response = new Response();
		
		// do request
		try {
			if (timeout != -1) {
				client.getParams().setSoTimeout(timeout);
			}
			int statusCode = client.executeMethod(reqMethod);
			response.setStatusCode(statusCode);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// read response
		try {
			InputStream instream = reqMethod.getResponseBodyAsStream();
			ByteArrayOutputStream outstream = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
            int len;
            while ((len = instream.read(buffer)) > 0) {
                outstream.write(buffer, 0, len);
            }
            String resp = new String(outstream.toByteArray(), reqMethod.getResponseCharSet());
            response.setResponse(resp);
            
            //set content length
            long contentLength = reqMethod.getResponseContentLength();
            response.setContentLength(contentLength);
            //set character set
			String respCharSet = reqMethod.getResponseCharSet();
			response.setCharSet(respCharSet);
			//set all headers
			Header[] headers = reqMethod.getResponseHeaders();
			response.setHeaders(headers);            
		} catch (Exception e) {
			e.printStackTrace();
		}

		// release connection
		reqMethod.releaseConnection();

		// return
		return response;
	}
	
	/**
	 * Get a file through http
	 * 
	 * @param address
	 * 					remote file location
	 * @param localFileName
	 */
	public static boolean getFileWithHttp(String address, String localFileName) {		
		OutputStream out = null;
		URLConnection conn = null;
		InputStream  in = null;
		try {
			// create local folders
			new File(localFileName).getParentFile().mkdirs();
			
			// get file
			URL url = new URL(address);
			out = new BufferedOutputStream(new FileOutputStream(localFileName));
			conn = url.openConnection();
			in = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int numRead;
			long numWritten = 0;
			while ((numRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, numRead);
				numWritten += numRead;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * Get filename from a URL
	 * 
	 * @param url - the url
	 * @return filename if available, otherwise null
	 */
	public static String getFileNameFromURL(String url) {
		if (url == null) {
			return null;
		}
		if (url.lastIndexOf(".") == -1 || url.lastIndexOf("/") == -1) {
			return null;
		} else {
			String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
			return fileName;
		}
	}

}
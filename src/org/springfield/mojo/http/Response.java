/* 
* Response.java
* 
* Copyright (c) 2014 Noterik B.V.
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

import org.apache.commons.httpclient.Header;

/**
 * Stores the response body from a request
 * including all the headers that are returned
 * 
 * 
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2014
 * @package org.springfield.mojo.http
 *
 */

public class Response {
	
	private int statusCode;
	private long contentLength;
	private String responseBody;
	private String charSet;
	private Header[] headers;
	
	public Response() {
		//constructor
	}
	
	/**
	 * Set status code
	 * 
	 * @param statusCode - the status code
	 */
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	/**
	 * Get the status code
	 * 
	 * @return statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}
	
	/**
	 * Set the response body
	 * 
	 * @param responseBody - the response body
	 */
	public void setResponse(String responseBody) {
		this.responseBody = responseBody;
	}
	
	/**
	 * Get the response body
	 * 
	 * @return responseBody
	 */
	public String getResponse() {
		return responseBody;
	}
	
	/**
	 * Set the content length
	 * 
	 * @param contentLength - the length of the content
	 */
	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	
	/**
	 * Get the content length
	 * 
	 * @return contentLength
	 */
	public long getContentLength() {
		return contentLength;
	}
	
	/**
	 * Set the character set
	 * 
	 * @param charSet - the used character set
	 */
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	
	/**
	 * Get the character set
	 * 
	 * @return charSet
	 */
	public String getCharSet() {
		return charSet;
	}
	
	/**
	 * Set all the response headers
	 * 
	 * @param headers - all the headers
	 */
	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}
	
	/**
	 * Get all the response headers
	 * 
	 * @return headers
	 */
	public Header[] getHeaders() {
		return headers;
	}	
	
	/**
	 * Return response body
	 * 
	 * @return responseBody
	 */
	@Override
	public String toString() {
		return responseBody;
	}
}

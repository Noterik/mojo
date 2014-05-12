/* 
* HttpMethods.java
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

package org.springfield.mojo.http.methods;

/**
 * All supported HTTP methods for HttpHelper
 * 
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2014
 * @package org.springfield.mojo.http.methods
 *
 */

public final class HttpMethods {
	public static final String GET = "GET";
	public static final String PUT = "PUT";
	public static final String POST = "POST";
	public static final String DELETE = "DELETE";
	public static final String HEAD = "HEAD";
	public static final String TRACE = "TRACE";
	public static final String OPTIONS = "OPTIONS";

	private HttpMethods() { }	
}

/* 
* Channel.java
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
package org.springfield.mojo.linkedtv;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.mojo.http.HttpHelper;
import org.springfield.mojo.http.Response;

/**
 * Channel.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2014
 * @package org.springfield.mojo.linkedtv
 * 
 */
public class Channel {
	private static String API = "http://api.linkedtv.eu/mediaresource?status=6&";
	private static String API_USER = "admin";
	private static String API_PASSWORD = "linkedtv";
	private static String API_AUTHORIZATION = API_USER+":"+API_PASSWORD;
	private static byte[] API_AUTHORIZATION_ENCODED = Base64.encodeBase64(API_AUTHORIZATION.getBytes());
	private static String CHARACTER_SET = "UTF-8";
	private static String BART = "http://bart2.noterik.com/bart";	
	
	private String domain;
	private String channel;
	private String channelXml;
	
	public Channel() {
		
	}
	
	public Channel(String domain, String channel) {
		this.domain = domain;
		this.channel = channel;
		
		if (domain.toLowerCase().equals("linkedtv")) {
			try {
				channel = URLEncoder.encode(channel, CHARACTER_SET);
			} catch (UnsupportedEncodingException e) {
				
			}
			String uri = API+"publisher="+channel;
			
			HashMap<String, String> requestHeaders = new HashMap<String, String>();
			requestHeaders.put("Authorization", "Basic "+new String(API_AUTHORIZATION_ENCODED));
			requestHeaders.put("Accept", "application/xml");
			
			System.out.println("GET uri "+uri);
			
			Response response = HttpHelper.sendRequest("GET", uri, null, "application/xml", null, -1, CHARACTER_SET, requestHeaders);
			
			System.out.println("responsecode "+response.getStatusCode());
			System.out.println("response "+response.toString());
			
			if (response.getStatusCode() == 200) {
				channelXml = response.getResponse();
			}
		}
	}
	
	public List<Episode> getEpisodes() {
		try {
			Document doc = DocumentHelper.parseText(channelXml);			
			
			List<Node> nodes = doc.selectNodes("//mediaresources/mediaresource");
			ArrayList<Episode> episodes = new ArrayList<Episode>();
			
			for (Node node : nodes) {
				String id = node.selectSingleNode("id") == null ? null :  node.selectSingleNode("id").getText();
				if (id != null) {
					episodes.add(new Episode(id));
				}
			}			
			return episodes;
		} catch (DocumentException e) {
			return new ArrayList<Episode>();
		}
	}
	
	public Episode getLatestEpisode() {
		try {
			Document doc = DocumentHelper.parseText(channelXml);
			
			List<Node> nodes = doc.selectNodes("//mediaresources/mediaresource");
			
			if (nodes.size() > 0) {
				Node node = nodes.get(0);
				String id = node.selectSingleNode("id") == null ? null :  node.selectSingleNode("id").getText();
				if (id != null) {
					return new Episode(id);
				}
			}
			
			return new Episode();
		} catch (DocumentException e) {
			return new Episode();
		}
	}

}

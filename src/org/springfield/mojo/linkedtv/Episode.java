/* 
* Episode.java
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

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;
import org.springfield.mojo.http.HttpHelper;
import org.springfield.mojo.http.Response;

/**
 * Episode.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2014
 * @package org.springfield.mojo.linkedtv
 * 
 */
public class Episode {
	private static String MAGGIE = "http://player2.noterik.com/maggie/?domain=linkedtv";
	private static String BART = "http://bart2.noterik.com/bart";
	
	private String mediaResourceId;
	private String title;
	private String baseLocator;
	private String stillsUri;
	private int duration;
	
	public Episode() {
		
	}
	
	public Episode(String mediaResourceId) {
		this.mediaResourceId = mediaResourceId;
		
		//Ask Maggie for some details
		Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId);
		
		if (response.getStatusCode() == 200) {
			try {
				Document doc = DocumentHelper.parseText(response.toString());
				
				title = doc.selectSingleNode("//properties/presentationtitle") == null ? "" : doc.selectSingleNode("//properties/presentationtitle").getText();
				baseLocator = doc.selectSingleNode("//locator/@href") == null ? "" : doc.selectSingleNode("//locator/@href").getText();
				
				//Ask video for some details
				if (baseLocator.indexOf("/domain/") != -1) {
					String videoLocation = baseLocator.substring(baseLocator.indexOf("/domain/"));
					
					Response r = HttpHelper.sendRequest("GET", BART+videoLocation);
					
					try {
						Document d = DocumentHelper.parseText(r.toString());
						
						Double dur = d.selectSingleNode("//rawvideo[@id='1']/properties/duration") == null ? -1.0 : Double.parseDouble(d.selectSingleNode("//rawvideo[@id='1']/properties/duration").getText());
						dur = dur < 0.0 ? -1.0 : dur * 1000;
						duration = dur.intValue();
						
						stillsUri = d.selectSingleNode("//screens[@id='1']/properties/uri") == null ? null : d.selectSingleNode("//screens[@id='1']/properties/uri").getText();
						
					} catch (DocumentException e) {
						
					}
				}
			} catch (DocumentException e) {
				
			}			
		}
	}
	
	public String getMediaResourceId() {
		return mediaResourceId;
	}
	
	public String getStillsUri() {
		return stillsUri;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public String getStreamUri() {
		//TODO: integrate ticket engine
		return baseLocator+"raw/4/raw.mp4";
	}
	
	public String getStreamuri(int quality) {
		//TODO: integrate ticket engine
		return baseLocator+"raw/"+quality+"/raw.mp4";
	}
	
	public String getTitle() {
		return title;
	}
	
	public FSList getAnnotations() {
		Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId+"&annotations&curated&renew");
		
		if (response.getStatusCode() != 200) {
			return new FSList();
		} else {
			try {
				Document doc = DocumentHelper.parseText(response.toString());
				List<Node> nodes = doc.selectNodes("//annotations/*");
				
				FSList annotations = new FSList();
				
				for (Node annotation : nodes) {
					Element a = (Element) annotation;
					FsNode result = new FsNode();
					
					result.setName(a.getName());
					result.setId(a.attribute("id").getText());
					
					List<Node> properties = a.selectNodes("properties");
					for (Node property : properties) {
						result.setProperty(property.getName(), property.getText());
					}
					annotations.addNode(result);
				}
				return annotations;
				
			} catch (DocumentException e) {
				return new FSList();
			}
		}
	}
	
	public FSList getChapters() {
		Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId+"&chapters");
		
		if (response.getStatusCode() != 200) {
			return new FSList();
		} else {
			try {
				Document doc = DocumentHelper.parseText(response.toString());
				List<Node> nodes = doc.selectNodes("//chapter");
				
				FSList chapters = new FSList();
				
				for (Node chapter : nodes) {
					Element c = (Element)chapter;					
					FsNode result = new FsNode();
					
					result.setName(c.getName());
					result.setId(c.attribute("id").getText());
					
					List<Node> properties = c.selectNodes("properties");
					for (Node property : properties) {
						result.setProperty(property.getName(), property.getText());
					}					
					chapters.addNode(result);
				}				
				return chapters;
				
			} catch (DocumentException e) {
				return new FSList();
			}
		}		
	}
}

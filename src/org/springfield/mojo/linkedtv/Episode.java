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
	private String presentationId;
	private String title;
	private String baseLocator;
	private String stillsUri;
	private int duration;
	private int width;
	private int height;
	
	private FSList annotations;
	private FSList chapters;
	private FSList enrichments;
	
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
				presentationId = doc.selectSingleNode("//properties/presentation") == null ? "" : doc.selectSingleNode("//properties/presentation").getText();

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
						
						width = d.selectSingleNode("//rawvideo[@id='1']/properties/width") == null ? -1 : Integer.parseInt(d.selectSingleNode("//rawvideo[@id='1']/properties/width").getText());
						height = d.selectSingleNode("//rawvideo[@id='1']/properties/height") == null ? -1 : Integer.parseInt(d.selectSingleNode("//rawvideo[@id='1']/properties/height").getText());
					} catch (DocumentException e) {
						System.out.println("What? "+e.getMessage());
					}
				}
			} catch (DocumentException e) {
				System.out.println("What? "+e.getMessage());
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
		return baseLocator+"rawvideo/4/raw.mp4";
	}
	
	public String getStreamuri(int quality) {
		//TODO: integrate ticket engine
		return baseLocator+"rawvideo/"+quality+"/raw.mp4";
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getPresentationId() {
		return presentationId;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public FSList getAnnotations() {
		Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId+"&annotations&curated&renew");
		this.annotations = new FSList();
		
		if (response.getStatusCode() != 200) {
			System.out.println("What? "+response.getStatusCode());
			return new FSList();
		} else {
			try {
				Document doc = DocumentHelper.parseText(response.toString());
				List<Node> nodes = doc.selectNodes("//annotations/*");
				String presentationUri = doc.selectSingleNode("properties/presentation") == null ? "" : doc.selectSingleNode("properties/presentation").getText();
				presentationUri += "annotations";
				
				FSList annotations = new FSList(presentationUri);
				
				for (Node annotation : nodes) {
					Element a = (Element) annotation;
					FsNode result = new FsNode();
					
					result.setName(a.getName());
					result.setId(a.attribute("id").getText());
					result.setPath(presentationId+"/"+result.getName()+"/"+result.getId());
					result.setImageBaseUri(stillsUri);
					
					List<Node> properties = a.selectNodes("properties/*");
					for (Node property : properties) {
						result.setProperty(property.getName(), property.getText());
					}
					annotations.addNode(result);
				}
				this.annotations = annotations;
				return annotations;
				
			} catch (DocumentException e) {
				System.out.println("Statuscode = "+response.getStatusCode());
				return new FSList();
			}
		}
	}
	
	public FSList getChapters() {
		Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId+"&chapters");
		this.chapters = new FSList();
		
		if (response.getStatusCode() != 200) {
			System.out.println("Statuscode = "+response.getStatusCode());
			return new FSList();
		} else {
			try {
				Document doc = DocumentHelper.parseText(response.toString());
				List<Node> nodes = doc.selectNodes("//chapter");
				String presentationUri = doc.selectSingleNode("properties/presentation") == null ? "" : doc.selectSingleNode("properties/presentation").getText();
				presentationUri += "chapters";
				
				FSList chapters = new FSList(presentationUri);
				
				for (Node chapter : nodes) {
					Element c = (Element)chapter;					
					FsNode result = new FsNode();
					
					result.setName(c.getName());
					result.setId(c.attribute("id").getText());
					result.setPath(presentationId+"/"+result.getName()+"/"+result.getId());
					result.setImageBaseUri(stillsUri);
					
					List<Node> properties = c.selectNodes("properties/*");
					for (Node property : properties) {
						result.setProperty(property.getName(), property.getText());
					}					
					chapters.addNode(result);
				}
				this.chapters = chapters;
				return chapters;
				
			} catch (DocumentException e) {
				System.out.println("What? "+e.getMessage());
				return new FSList();
			}
		}		
	}
	
	public FSList getAnnotationsFromChapter(FsNode chapter) {
		if (chapter != null) {
			if (this.annotations == null) {
				System.out.println("getting annotations");
				getAnnotations();
			}
			
			FSList annotations = new FSList("chapter/"+chapter.getId());
			
			float start = chapter.getStarttime();
			float duration = chapter.getDuration();
			
			List<FsNode> nodes = this.annotations.getNodes();
			
			for (FsNode node : nodes) {
				if (node != null) {					
					if (node.getStarttime() >= start && node.getStarttime() <= start+duration) {
						annotations.addNode(node);
					}
				}
			}
			return annotations;
		} else {
			System.out.println("Empty chapter");
		}
		return new FSList();
	}
	
	public FSList getEnrichmentsFromAnnotation(FsNode annotation) {
		if (annotation != null) {		
			Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+annotation.getId()+"&enrichments");
			
			if (response.getStatusCode() != 200) {
				System.out.println("Statuscode = "+response.getStatusCode());
				return new FSList();
			} else {
				try {
					Document doc = DocumentHelper.parseText(response.toString());
					List<Node> nodes = doc.selectNodes("//enrichment");
					
					FSList enrichments = new FSList("annotation/"+annotation.getId());
					
					for (Node enrichment : nodes) {
						Element c = (Element)enrichment;					
						FsNode result = new FsNode();
						
						result.setName(c.getName());
						result.setId(c.attribute("id").getText());
						result.setPath(presentationId+"/"+result.getName()+"/"+result.getId());
						result.setImageBaseUri(stillsUri);
						
						List<Node> properties = c.selectNodes("properties/*");
						for (Node property : properties) {
							result.setProperty(property.getName(), property.getText());
						}					
						enrichments.addNode(result);
					}					
					this.enrichments = new FSList();
					return enrichments;
				} catch (DocumentException e) {
					System.out.println("What? "+e.getMessage());
					return new FSList();
				}
			}
		} else {
			System.out.println("Empty annotation");
		}
		return new FSList();		
	}
}

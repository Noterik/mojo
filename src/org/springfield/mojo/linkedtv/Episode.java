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

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
	private Map<String, FsNode> proxyenrichments = new HashMap<String, FsNode>();
	
	public Episode() {
		
	}
	
	public Episode(String mediaResourceId) {
		this.mediaResourceId = mediaResourceId;
		
		// try reading it from disk
		String readpath  = "/springfield/lisa/data/linkedtv/"+mediaResourceId+"/episode.xml";
		System.out.println("READPATH="+readpath);
		String body  = readFile(readpath);
		if (body==null) {
			//Ask Maggie for some details
			Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId);
			//System.out.println("BART2="+response.toString());
			if (response.getStatusCode() == 200) {
				body = response.getResponse();
			} else {
				System.out.println("Statuscode = "+response.getStatusCode());
			}
				
		} else {
			System.out.println("READING FROM LISA DISK CACHE "+readpath);
		}
			try {
				Document doc = DocumentHelper.parseText(body);
				
				title = doc.selectSingleNode("//properties/presentationtitle") == null ? "" : doc.selectSingleNode("//properties/presentationtitle").getText();
				baseLocator = doc.selectSingleNode("//locator/@href") == null ? "" : doc.selectSingleNode("//locator/@href").getText();
				presentationId = doc.selectSingleNode("//properties/presentation") == null ? "" : doc.selectSingleNode("//properties/presentation").getText();

				//Ask video for some details
				if (baseLocator.indexOf("/domain/") != -1) {
					String videoLocation = baseLocator.substring(baseLocator.indexOf("/domain/"));
				
					try {
						Response r = HttpHelper.sendRequest("GET", BART+videoLocation);
						System.out.println("BARTVIDEO="+r.getResponse());
						Document d = DocumentHelper.parseText(r.toString());
						
						Double dur = d.selectSingleNode("//rawvideo[@id='1']/properties/duration") == null ? -1.0 : Double.parseDouble(d.selectSingleNode("//rawvideo[@id='1']/properties/duration").getText());
						dur = dur < 0.0 ? -1.0 : dur * 1000;
						duration = dur.intValue();
						System.out.println("D="+duration);
						stillsUri = d.selectSingleNode("//screens[@id='1']/properties/uri") == null ? null : d.selectSingleNode("//screens[@id='1']/properties/uri").getText();
						System.out.println("S="+stillsUri);
						width = d.selectSingleNode("//rawvideo[@id='1']/properties/width") == null ? -1 : Integer.parseInt(d.selectSingleNode("//rawvideo[@id='1']/properties/width").getText());
						height = d.selectSingleNode("//rawvideo[@id='1']/properties/height") == null ? -1 : Integer.parseInt(d.selectSingleNode("//rawvideo[@id='1']/properties/height").getText());
						System.out.println("W="+width+" H="+height);
					} catch (Exception e) {
						duration = 1730470;
						stillsUri ="http://images1.noterik.com/domain/linkedtv/user/rbb/video/1633/shots/1";
						width = 512;
						height = 288;
					}
				}
			} catch (DocumentException e) {
				System.out.println("What? "+e.getMessage());
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
		// try reading it from disk
		String readpath  = "/springfield/lisa/data/linkedtv/"+mediaResourceId+"/annotations.xml";
		System.out.println("READPATH="+readpath);
		String body  = readFile(readpath);
		if (body==null) {
			Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId+"&annotations&curated&renew");
			if (response.getStatusCode() != 200) {
				System.out.println("What? "+response.getStatusCode());
				return new FSList();
			} else {
				body = response.toString();	
			}
		} else {
			System.out.println("READING FROM LISA DISK CACHE "+readpath);
		}
		//Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId+"&annotations&curated&renew");
		this.annotations = new FSList();
		
			try {
				Document doc = DocumentHelper.parseText(body);
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
						//System.out.println("DANIEL2: "+property.getName()+"="+property.getText());
						result.setProperty(property.getName(), property.getText());
						if (property.getName().equals("locator")) {
							loadEntityFromProxy(property.getName(),property.getText());
						}
					}
					annotations.addNode(result);
				}
				this.annotations = annotations;
				return annotations;
				
			} catch (DocumentException e) {
				System.out.println("What? "+e.getMessage());
				return new FSList();
			}
	}
	
	public FSList getChapters() {
		// try reading it from disk
		String readpath  = "/springfield/lisa/data/linkedtv/"+mediaResourceId+"/chapters.xml";
		System.out.println("READPATH="+readpath);
		String body  = readFile(readpath);
		if (body==null) {
			Response response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+mediaResourceId+"&chapters");
			if (response.getStatusCode() != 200) {
				System.out.println("Statuscode = "+response.getStatusCode());
				return new FSList();
			} else {
				body = response.toString();
			}
		//System.out.println("CHAPTERS="+response.toString());
		} else {
			System.out.println("READING FROM LISA DISK CACHE "+readpath);
		}
		this.chapters = new FSList();
		
			try {
				Document doc = DocumentHelper.parseText(body);
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
						//System.out.println("DANIEL: "+property.getName()+"="+property.getText());
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
			Response response = null;
			try {
				response = HttpHelper.sendRequest("GET", MAGGIE+"&id="+annotation.getId()+"&enrichments");
			} catch(Exception e) {
				return new FSList();
			}
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
	
	private void loadEntityFromProxy(String name,String url) {
		FsNode result = new FsNode();

		String decurl = StringEscapeUtils.unescapeHtml(url);

		
		// try reading it from disk
		String readpath  = "/springfield/lisa/data/linkedtv/"+mediaResourceId+"/de_"+decurl.substring(url.lastIndexOf("/")+1);
		System.out.println("READPATH="+readpath);
		String body  = readFile(readpath);
		if (body==null) {
			System.out.println("CALLING CWI PROXY FOR : "+url);
			Response response = HttpHelper.sendRequest("GET", "http://linkedtv.project.cwi.nl/explore/entity_proxy?url="+url+"&lang=de");
			if (response.getStatusCode() != 200) {
				System.out.println("CWI PROXY Statuscode = "+response.getStatusCode());
			} else {
				body = response.toString();
			}
		} else {
			System.out.println("READING FROM LISA DISK CACHE "+readpath);
			
			FsNode cachednode = FsNode.parseFsNode(body);
			System.out.println("LISA PUT "+decurl+" NODE="+cachednode);	
			proxyenrichments.put(decurl, cachednode);
			return;
		}
		
			//System.out.println("PROXY="+url+" B="+response.toString());
			try {
			JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject)jsonParser.parse(body);
				JSONObject mainobj= (JSONObject) jsonObject.get(url);
				if (mainobj!=null) {
					JSONArray comments= (JSONArray)mainobj.get("comment");
					if (comments!=null) {
						JSONObject comment = (JSONObject)comments.get(0);
						System.out.println("DESCRIPTION="+url+" "+comment.get("value"));
						result.setProperty("description",comment.get("value").toString());
					}
					JSONArray labels= (JSONArray)mainobj.get("label");
					if (labels!=null) {
						JSONObject label = (JSONObject)labels.get(0);
						System.out.println("LABEL="+url+" "+label.get("value"));
						result.setProperty("label",label.get("value").toString());
					}
					JSONArray thumbs= (JSONArray)mainobj.get("thumb");
					if (thumbs!=null) {
						if (thumbs.size()>0) {
							String thumb = (String)thumbs.get(0);
							result.setProperty("thumb",thumb);
						} 
					}

					// type 
					JSONArray types= (JSONArray)mainobj.get("type");
					if (types!=null) {
						if (types.size()==0) {
							result.setProperty("type","unknown");
						} else {
							Object o = types.get(0);
							System.out.println("O="+o.getClass().toString());
							if (o instanceof String) {
								result.setProperty("type",(String)o);
							} else {
								result.setProperty("type","");
							}

						}
					}
					
					// birthdate 
					JSONArray birthdates= (JSONArray)mainobj.get("birthDate");
					if (birthdates!=null) {
						if (birthdates.size()==0) {
							result.setProperty("birthdate","");
						} else {
							Object o = birthdates.get(0);
							System.out.println("O2="+o.getClass().toString());
							if (o instanceof String) {
								result.setProperty("birthday",(String)o);
							} else {
								result.setProperty("birthday","");
							}
						}
					}
					
					// birthplace 
					JSONArray birthplaces= (JSONArray)mainobj.get("birthPlace");
					if (birthplaces!=null) {
						if (birthplaces.size()==0) {
							result.setProperty("birthplace","");
						} else {
							JSONObject birthplace = (JSONObject)birthplaces.get(0);
							result.setProperty("birthplace",birthplace.get("value").toString());
						}
					}

					
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			result.setName("entity");
			result.setId(decurl);

			proxyenrichments.put(decurl, result);
			String filename="de_"+decurl.substring(url.lastIndexOf("/")+1);
				
			writeFile("/springfield/lisa/data/linkedtv/"+mediaResourceId,filename,result.asXML());
	}
	
	public FsNode getEntityFromProxy(String name) {
		name = StringEscapeUtils.unescapeHtml(name);
		return proxyenrichments.get(name);
	}
	
	private String readFile(String filename) {
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader(filename));
			StringBuffer str = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				str.append(line);
				str.append("\n");
				line = br.readLine();
			}
			br.close();
			String body = str.toString();
			return body;
		} catch(Exception e) {
		}
		return null;
	}
	
	private void writeFile(String writedir,String filename,String body) {
		try {
			File md = new File(writedir);
			md.mkdirs();
		    PrintWriter writer = new PrintWriter(writedir+"/"+filename, "UTF-8");
		    writer.println(body);
		    writer.close();
		} catch(Exception e) {
			System.out.println("FILENAME="+writedir+"/"+filename);
			e.printStackTrace();
		}
	}
	
}

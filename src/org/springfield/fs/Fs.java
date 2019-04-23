/* 
* Fs.java
* 
* Copyright (c) 2012 Noterik B.V.
* 
* This file is part of Lou, related to the Noterik Springfield project.
*
* Lou is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Lou is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Lou.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.springfield.fs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.mojo.interfaces.*;

/**
 * Fs
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class Fs {
	/** logger */
	private static Logger LOG = Logger.getLogger(Fs.class);
	
	private static String[] ignorelist = {"rawvideo","screens"};

	public static FsNode getNode(String path) {
		String pathWithProperties = path;
		FsNode result = null;
		//FsNode result = new FsNode();
		//result.setPath(path);
		if (!pathWithProperties.endsWith("/properties")) {
			pathWithProperties += "/properties";
		}
		String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
		
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return null;
		}
		String node = smithers.get(pathWithProperties,xml,"text/xml");
				
		if (node.indexOf("<error id=\"404\">")!=-1) {
			return null; // node not found
		}
 		try { 
			Document doc = DocumentHelper.parseText(node);
			for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {
				Element p = (Element)iter.next();
				result = new FsNode(p.getName(),p.attribute("id").getText());
				//result.setName(p.getName(),p.attribute("id").getText());
				//result.setId(p.attribute("id").getText());
				//here we don't want the /properties to be appended, as this causes issues with determining the parent node
				result.setPath(path);
				if (p.attribute("referid")!=null) {
					String referid = p.attribute("referid").getText();
					if (referid!=null) result.setReferid(referid);
				}
				for(Iterator<Node> iter2 = p.nodeIterator(); iter2.hasNext(); ) {
					Element p2 = (Element)iter2.next();
					if (p2.getName().equals("properties")) {
						for(Iterator<Node> iter3 = p2.nodeIterator(); iter3.hasNext(); ) {
							Object p3 = iter3.next();
							if (p3 instanceof Element) {
								String pname = ((Element)p3).getName();
								String pvalue = ((Element)p3).getText();
								if (pvalue.indexOf("Solistai Laima")!=-1) {
									System.out.println("D1="+pvalue);
									System.out.println("D2="+FsEncoding.decode(pvalue));
								}
								result.setProperty(pname,FsEncoding.decode(pvalue));
							} else {
								
							}
						}
					}
				}
			}
 		} catch(Exception e) {
 			e.printStackTrace();
 		}
		return result;		
	}
	
	public static boolean deleteNode(String path){
		String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return false;
		}
		smithers.delete(path, xml, "text/xml");
		return true;
	}
	
	public static boolean isMainNode(String path) {
		//discard trailing slash
		if (path.length() > 0 && path.substring(path.length()-1).equals("/")) {
			path = path.substring(0, path.length()-1);
		}
		int r = path.split("/").length;
		if  ( r % 2 == 0 ) return true;
		return false;
	}
	
	public static int getTotalResultsAvailable(String path) {
		//results can only be given for main parent nodes
		if (!isMainNode(path)) {
			return -1;
		}
		
		String xml = "<fsxml><properties>"
				+ "<depth>0</depth>"
				+ "<start>0</start>"
				+ "<limit>0</limit>"
				+ "</properties></fsxml>";
	
		String results = "";
			
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return -1;
		}
		results = smithers.get(path,xml,"text/xml");
			
		try { 
			Document doc = DocumentHelper.parseText(results);
			
			return doc.selectSingleNode("/fsxml/properties/totalResultsAvailable") == null ? -1 : Integer.parseInt(doc.selectSingleNode("/fsxml/properties/totalResultsAvailable").getText());
		} catch(Exception e) {
 			e.printStackTrace();
 		}
		return -1;
	}	
	
	public static List<FsNode> getNodes(String path,int depth) {
		return getNodes(path, depth, 0, Integer.MAX_VALUE);
	}
	
	public static List<FsNode> getNodes(String path,int depth, int start, int limit) {
		List<FsNode> result = new ArrayList<FsNode>();
		String xml = "<fsxml><properties>"
					+ "<depth>"+depth+"</depth>"
					+ "<start>"+start+"</start>"
					+ "<limit>"+limit+"</limit>"
					+ "</properties></fsxml>";
		
		String nodes = "";
		
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return null;
		}
		nodes = smithers.get(path,xml,"text/xml");
		path = path.substring(path.indexOf("/domain/"));
		
		if (nodes.indexOf("<error id=\"404\">")!=-1) {
			return null; // node not found
		}
		
		LOG.debug("nodes "+nodes);
		
 		try { 
			Document doc = DocumentHelper.parseText(nodes);		
			
			if (isMainNode(path)) {
				for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {
					Element node = (Element)iter.next();
					FsNode nn = new FsNode("unknown","unknown");
					if (!node.getName().equals("properties")) {
						nn.setName(node.getName());
						nn.setId(node.attribute("id").getText());
						nn.setPath(path+"/"+nn.getId());
						if (node.attribute("referid")!=null) {
							String referid = node.attribute("referid").getText();
							if (referid!=null) nn.setReferid(referid);
						}
						result.add(nn);
						for(Iterator<Node> iter2 = node.nodeIterator(); iter2.hasNext(); ) {
							Element p2 = (Element)iter2.next();
							if (p2.getName().equals("properties")) {
								for(Iterator<Node> iter3 = p2.nodeIterator(); iter3.hasNext(); ) {
									Object o  = iter3.next();
									if (o instanceof Element) {
										Element p3 = (Element)o;
										String pname = p3.getName();
										String pvalue = p3.getText();
										nn.setProperty(pname, FsEncoding.decode(pvalue));
									}
								}
							}
						}
					}
				}
			} else {
				//System.out.println("IS SUBNODE");
				for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {
					Element node = (Element)iter.next();
					for(Iterator<Node> iter2 = node.nodeIterator(); iter2.hasNext(); ) {
						Element node2 = (Element)iter2.next();
						FsNode nn = new FsNode("unknown","unknown");
						if (!node2.getName().equals("properties")) {
							nn.setName(node2.getName());
							nn.setId(node2.attribute("id").getText());
							nn.setPath(path+"/"+nn.getName()+"/"+nn.getId());							
							for(Iterator<Node> iter3 = node2.nodeIterator(); iter3.hasNext(); ) {
								Element p2 = (Element)iter3.next();
								if (p2.getName().equals("properties")) {
									for(Iterator<Node> iter4 = p2.nodeIterator(); iter4.hasNext(); ) {
										Object o  = iter4.next();
										if (o instanceof Element) {
											Element p3 = (Element)o;
											String pname = p3.getName();
											String pvalue = p3.getText();
											if (pvalue.indexOf("Solistai Laima")!=-1) {
												System.out.println("D1="+pvalue);
												System.out.println("D2="+FsEncoding.decode(pvalue));
											}
											nn.setProperty(pname, FsEncoding.decode(pvalue));
										}
									}
								}
							}
							result.add(nn);
						}
					}
				}
			}
 		} catch(Exception e) {
 			e.printStackTrace();
 		}
		return result;
	}
	
	public static void setProperty(String path,String name,String value) {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return;
		}
		
		String postpath = path+"/properties/"+name;
		// danielfix LazyHomer.sendRequest("PUT",postpath,value,"text/xml");
		String node = smithers.put(postpath,value,"text/xml");
	}
	

	public static boolean insertNode(FsNode node,String insertpath) {
		String  body = "<fsxml>";
		body += node.asXML(true);
		body += "</fsxml>";
		if (insertpath.endsWith("/")) insertpath = insertpath.substring(0,insertpath.length()-1); // remove last '/' if attached

		//System.out.println("SAVE NODE = "+node.getPath()+" "+node.getName()+" "+node.getId()+" "+body);
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return false;
		}
		if (node.getName()==null) {
			return false;
		}
		if (node.getId()!=null) {
			String result = smithers.put(insertpath+"/properties",body,"text/xml");
			if (result.indexOf("<error id")!=-1) { return false; }
		} else {
			String result = smithers.post(insertpath+"/"+node.getName(),body,"text/xml");
			if (result.indexOf("<error id")!=-1) { return false; }			
		}
		return true;
	}
	
	public static Iterator<String> changedProperties(FsNode node1,FsNode node2) {
		List<String> set = new ArrayList<String>();
		for(Iterator<String> iter = node1.getKeys(); iter.hasNext(); ) {
			String key = (String)iter.next();
			if (!node1.getProperty(key).equals(node2.getProperty(key))) {
				set.add(key);
			}
		}
		return set.iterator();
	}
	
	public static FsTimeTagNodes searchTimeTagNodes(String path,String filter) {
		FsTimeTagNodes results = new FsTimeTagNodes();
		List<FsNode> nodes = Fs.getNodes(path,1);
		for (int i=0;i<nodes.size();i++) {
			FsNode node = nodes.get(i);
			if (!Arrays.asList(ignorelist).contains(node.getName())) {
				results.addNode(node);
			}
		}
		return results;
	}
	
    public static FSList getReferParents(String path) {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return null;
		}
		String body = "<fsxml mimetype=\"application/fscommand\" id=\"showrefs\">";
		body+="<properties>";
		body+="</properties>";
		body+="</fsxml>";
		System.out.println("PATH="+path);
		System.out.println("BODY3="+body);
		//String result = LazyHomer.sendRequestBart("POST",currentpath,body,"text/xml");
		String result = smithers.post(path,body,"application/fscommand");
		System.out.println("R="+result);
		FSList list = new FSList();
		try {
			Document doc = DocumentHelper.parseText(result);
			if (doc!=null) {
				   for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {     
					   Element node = (Element)iter.next();
					   System.out.println("PAR NAME="+node.getName());
					   String parentpath = node.getText();
					   System.out.println("PAR2 NAME="+parentpath);
					   FsNode parent = Fs.getNode(parentpath);
					   System.out.println("PAR3b NAME="+parent);
					   if (parent!=null) {
						   list.addNode(parent);
					   } else {
						  System.out.println("Mojo : can't load refering node "+parentpath); 
					   }
				   }
				   System.out.println("PAR4 NAME="+list.size());
				   return list;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
        return null;
    }
    
    public static String createLink(String source,String destination) {
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (Fs.isMainNode(destination)) {
			String newbody = "datatype=attributes&referid="+source;
			String result = smithers.post(destination,newbody,"text/xml");
			return result;
		} else {
			String newbody = "<fsxml><attributes><referid>"+source+"</referid></attributes></fsxml>"; 
			String result = smithers.put(destination+"/attributes",newbody,"text/xml");
			return result;
		}
    }
}

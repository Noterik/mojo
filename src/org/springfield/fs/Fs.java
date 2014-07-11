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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import org.springfield.mojo.http.HttpHelper;
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
	
	private static String[] ignorelist = {"rawvideo","screens"};

	public static FsNode getNode(String path) {
		FsNode result = new FsNode();
		result.setPath(path);
		path += "/properties";
		String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
		
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return null;
		}
		
		String node = smithers.get(path,xml,"text/xml");
				
		if (node.indexOf("<error id=\"404\">")!=-1) {
			return null; // node not found
		}
 		try { 
			Document doc = DocumentHelper.parseText(node);
			//System.out.println("RAWNODE="+node);
			for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {
				Element p = (Element)iter.next();
				result.setName(p.getName());
				result.setId(p.attribute("id").getText());
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
	
	public static void deleteNode(String path){
		String xml = "<fsxml><properties><depth>0</depth></properties></fsxml>";
//	danielfix	LazyHomer.sendRequestBart("DELETE", path, xml, "text/xml");
		
	}
	
	public static boolean isMainNode(String path) {
		int r = path.split("/").length;
		if  ( r % 2 == 0 ) return true;
		return false;
	}
	
	public static List<FsNode> getNodes(String path,int depth) {
		List<FsNode> result = new ArrayList<FsNode>();
		String xml = "<fsxml><properties><depth>"+depth+"</depth></properties></fsxml>";
		
		String nodes = "";
		if (path.indexOf("http://")==-1) {
		// danielfix	nodes = LazyHomer.sendRequestBart("GET",path,xml,"text/xml");
		} else {
			ServiceInterface smithers = ServiceManager.getService("smithers");
			if (smithers==null) {
				System.out.println("org.springfield.fs.Fs : service not found smithers");
				return null;
			}
			smithers.get(path,xml,"text/xml");
			path = path.substring(path.indexOf("/domain/"));
		}
 		try { 
			Document doc = DocumentHelper.parseText(nodes);
			if (isMainNode(path)) {
				for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {
					Element node = (Element)iter.next();
					FsNode nn = new FsNode();
					if (!node.getName().equals("properties")) {
						nn.setName(node.getName());
						nn.setId(node.attribute("id").getText());
						nn.setPath(path+"/"+nn.getName()+"/"+nn.getId());
						result.add(nn);
					}
				}
			} else {
				//System.out.println("IS SUBNODE");
				for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {
					Element node = (Element)iter.next();
					for(Iterator<Node> iter2 = node.nodeIterator(); iter2.hasNext(); ) {
						Element node2 = (Element)iter2.next();
						FsNode nn = new FsNode();
						if (!node2.getName().equals("properties")) {
							nn.setName(node2.getName());
							nn.setId(node2.attribute("id").getText());
							nn.setPath(path+"/"+nn.getName()+"/"+nn.getId());
							result.add(nn);
							for(Iterator<Node> iter3 = node2.nodeIterator(); iter3.hasNext(); ) {
								Element p2 = (Element)iter3.next();
								if (p2.getName().equals("properties")) {
									for(Iterator<Node> iter4 = p2.nodeIterator(); iter4.hasNext(); ) {
										Object o  = iter4.next();
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
	

	public static boolean insertNode(FsNode node) {
		String  body = "<fsxml>";
		body += node.asXML();
		body += "</fsxml>";
		System.out.println("SAVE NODE = "+node.getPath()+" "+node.getName()+" "+node.getId()+" "+body);
		ServiceInterface smithers = ServiceManager.getService("smithers");
		if (smithers==null) {
			System.out.println("org.springfield.fs.Fs : service not found smithers");
			return false;
		}
		String result = smithers.put(node.getPath()+"/properties",body,"text/xml");
		System.out.println("RESULT="+result);
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
}
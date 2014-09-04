/* 
* FsNode.java
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

import java.util.*;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springfield.fs.*;


/**
 * FsNode
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class FsNode implements Comparable<FsNode>  {
	
	private String name;
	private String id;
    private String referid;
	private String path;
	private float starttime;
	private float duration;
	private String imageBaseUri;
	private String asindex;
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	public FsNode() {
		setName("unknown"); // set to default value should be set later
	}
	
	public FsNode(String name) {
		setName(name);
	}
	
	public FsNode(String name,String id) {
		setName(name);
		setId(id);
	}

	public void setPath(String p) {
		path = p;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setId(String i) {
		id = i;
	}
	
	public String getId() {
		return id;
	}
	
    public void setReferid(String i) {
        referid = i;
    }

    public String getReferid() {
        return referid;
    }
	
	public float getStarttime() {
		return starttime;
	}
	
	public float getDuration() {
		return duration;
	}
	
	public void setImageBaseUri(String imageBaseUri) {
		this.imageBaseUri = imageBaseUri;
	}
	
	public void setProperty(String name,String value) {
		properties.put(name, value);
		if (name.equals("starttime")) {
			starttime = Float.parseFloat(value);
		} else if (name.equals("duration")) {
			duration = Float.parseFloat(value);
		}
		
		// ok lets kill the search index
		asindex=null;
		asIndex();
	}
	
	public String getScreenShotUrl() {
		String times = getShotsFormat(getStarttime()/1000);
		if (imageBaseUri != null) {
			return imageBaseUri+"/"+times;
		}
		//TODO: change this to a generic 404 image?
		return times;
	}
	
	public String getProperty(String name) {
		return properties.get(name);
	}

	public String getProperty(String name,String def) {
		String value =  properties.get(name);
		if (value!=null) {
			return value;
		} else {
			return def;
		}
	}
	
	public Iterator<String> getKeys() {
		return properties.keySet().iterator();
	}
	public String getPropertiesXML() {
		return getPropertiesXML(false);
	}
	
	
	public String getPropertiesXML(boolean fsencode) {
			String xml="<properties>";
			for(Iterator<String> i = this.getKeys(); i.hasNext();){
				String key = i.next();
				String value = getProperty(key);
				if (fsencode) value = FsEncoding.encode(value);
				if (value.contains("&") || value.contains("<")) {
					xml+="<"+key+"><![CDATA["+value+"]]></"+key+">\n";
				} else {
					xml+="<"+key+">"+value+"</"+key+">\n";
				}
			}
			xml+="</properties>";
			return xml;
	}
	
	public int compareTo(FsNode n) throws ClassCastException {
		 Float f1 = getStarttime();
		 Float f2 = n.getStarttime();
		 return f1.compareTo(f2);
   }
	
	private String getShotsFormat(double seconds) {
		String result = null;
		int sec = 0;
		int hourSecs = 3600;
		int minSecs = 60;
		int hours = 0;
		int minutes = 0;
		while (seconds >= hourSecs) {
			hours++;
			seconds -= hourSecs;
		}
		while (seconds >= minSecs) {
			minutes++;
			seconds -= minSecs;
		}
		sec = new Double(seconds).intValue();
		result = "h/" + hours;
		result += "/m/" + minutes ;
		result += "/sec" + sec + ".jpg";
		return result;
	}
	
	
	public ArrayList<String> allowedActions(String asker,String type) {
		// access property looks like access_read = 'u(admin,daniel),a(euscreenpreview)'
		// where u=user,a=application etc etc. Any normal writes on 'access_ are forbidden.
		ArrayList<String> list = new ArrayList<String>();
		// we do them one by one to be sure
		if (type.equals("user")) {
			if (readAllowedForUser(asker)) list.add("read");
			if (writeAllowedForUser(asker)) list.add("write");
		} else if (type.equals("application")) {
			if (readAllowedForAppliction(asker)) list.add("read");
			if (writeAllowedForApplication(asker)) list.add("write");
		}
		return list;
	}
	
	public boolean checkActions(String asker,String type,int depth,String actions) {
		
		// check if it has the actions we need to check, travel up if needed
		ArrayList<String> allowedactions = allowedActions(asker,type);
		
		String[] wantedactions = actions.split(":");
		for (int i=0;i<wantedactions.length;i++) {
			if (!allowedactions.contains(wantedactions[i])) {
				return false;
			}
		}
		return true;
	}
	
	
	
	private boolean readAllowedForUser(String user) {
		String accessline = this.getAccessProperty("read");
		if (accessline!=null) {
			int pos = accessline.indexOf("u(");
			if (pos!=-1) {
				String userline = accessline.substring(pos+2);
				userline = userline.substring(0,userline.indexOf(")"));
				String[] users = userline.split(",");
				for (int i = 0; i<users.length;i++) {
					if (users[i].equals(user)) return true;
				}
			}
		}
		return false;
	}

	private boolean writeAllowedForUser(String user) {
		String accessline = this.getAccessProperty("write");
		if (accessline!=null) {
			int pos = accessline.indexOf("u(");
			if (pos!=-1) {
				String userline = accessline.substring(pos+2);
				userline = userline.substring(0,userline.indexOf(")"));
				String[] users = userline.split(",");
				for (int i = 0; i<users.length;i++) {
					if (users[i].equals(user)) return true;
				}
			}
		}
		return false;
	}

	private boolean readAllowedForAppliction(String app) {
		String accessline = this.getAccessProperty("read");
		if (accessline!=null) {
			int pos = accessline.indexOf("a(");
			if (pos!=-1) {
				String appline = accessline.substring(pos+2);
				appline = appline.substring(0,appline.indexOf(")"));
				String[] apps = appline.split(",");
				for (int i = 0; i<apps.length;i++) {
					if (apps[i].equals(app)) return true;
				}
			}
		}
		return false;
	}

	private boolean writeAllowedForApplication(String app) {
		String accessline = this.getAccessProperty("write");
		if (accessline!=null) {
			int pos = accessline.indexOf("a(");
			if (pos!=-1) {
				String appline = accessline.substring(pos+2);
				appline = appline.substring(0,appline.indexOf(")"));
				String[] apps = appline.split(",");
				for (int i = 0; i<apps.length;i++) {
					if (apps[i].equals(app)) return true;
				}
			}
		}
		return false;
	}

	
	public String getAccessProperty(String type) {
		String path = this.getPath();
		// /domain/
		path = path.substring(8);

		String dpath = null;
		int pos = path.indexOf("/");	
		if (pos!=-1) {
			dpath = "/domain/"+path.substring(0,pos);
			path = path.substring(pos);
		} else {
			dpath = "/domain/";
		}
		
	
		
		if (path.endsWith("/")) path = path.substring(0,path.length()-1); // remove last '/' if attached
		
		boolean finished = false;
		FsNode node = null;
		while (!finished) {
			if (Fs.isMainNode(dpath+path)) {
				node = Fs.getNode(dpath+path+"/.access");
			} else {
				node = Fs.getNode(dpath+path);	
			}
			
			if (node!=null) {
				String result = node.getProperty("access_"+type);
				if (result!=null) {
					return result;
				}
			}
			
			pos  = path.lastIndexOf("/");
			if (pos!=-1) {
				path = path.substring(0,pos);
			} else {
				finished = true;
			}
		}
		return null;
	}

	
	public String asXML(){
		return asXML(false);
	}
	
	public String asXML(boolean fsencode){
		String xml = "<" + this.getName() + " id=\"" + this.getId() + "\"";
		if (this.getId()==null) {
			xml = "<" + this.getName() + " ";
		}
		if(this.getReferid() != null){
			xml += " referid=\"" + this.getReferid() + "\"";
		}
		xml += ">";
		
		xml += "<properties";
		if(!this.getKeys().hasNext()){ // weird code daniel, so weird fix
			xml += "/></" + this.getName() + ">";
			return xml;
		}
		xml += ">";
		
		for(Iterator<String> i = this.getKeys(); i.hasNext();){
			String key = i.next();
			String value = getProperty(key);
			if (fsencode) value = FsEncoding.encode(value);
			if (value.contains("&") || value.contains("<")) {
				xml+="<"+key+"><![CDATA["+value+"]]></"+key+">\n";
			} else {
				xml+="<"+key+">"+value+"</"+key+">\n";
			}
		}
		
		xml += "</properties>";
		xml += "</" + this.getName() + ">";
		
		return xml;
	}
	
	public String asIndex() {
		if (asindex!=null) return asindex;
		
		StringBuffer buf  = new StringBuffer();
		
		for(Iterator<String> i = this.getKeys(); i.hasNext();){
			String key = i.next();
			buf.append(" "+(this.getProperty(key).toLowerCase())+" ");
		}		
		asindex = buf.toString();
		return asindex;
	}
	
	public boolean isMainNode() {
		int r = path.split("/").length;
		if  ( r % 2 == 0 ) return true;
		return false;
	}
	
	public static FsNode parseFsNode(String fsxml) {
		try {
			   FsNode newnode = new FsNode();
			   Document doc = DocumentHelper.parseText(fsxml);
			   if (doc!=null) {
				   Element rootnode = doc.getRootElement();
				   String name = rootnode.getName();
				   String id = rootnode.attributeValue("id");
				   //System.out.println("PARSE NODE NAME="+name+" ID="+id);	
				   newnode.setName(name);
				   newnode.setId(id);
				   for(Iterator<Node> iter = doc.getRootElement().nodeIterator(); iter.hasNext(); ) {     
					   Element node = (Element)iter.next();
					   //System.out.print("NAME="+node.getName());
						if (node.getName().equals("properties")) {
							   for(Iterator<Node> iter2 = node.nodeIterator(); iter2.hasNext(); ) {  
									Node node2 = iter2.next();
									if(node2 instanceof Element){
										Element child2 = (Element)node2;	
										String pname = child2.getName();
										String pvalue = child2.getText();
										newnode.setProperty(pname, pvalue);
										//System.out.println("PARSED NODE PNAME="+pname+" PVALUE="+pvalue);	
									}
							   }
						}
				   }
			   }   
			   return newnode;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}

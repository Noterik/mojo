/*
 * Created on Nov 7, 2008
 */
package org.springfield.fs;

import java.util.Iterator;
import java.util.Map;



public class FSXMLBuilder {

	/**
	 * This function wraps the supplied parameters in a Springfield compliant
	 * XML error message and returns it
	 * 
	 * @param errorId
	 * @param message
	 * @param details
	 * @param uri
	 * @return
	 */

	public static String getErrorMessage(String errorId, String message, String details, String uri) {
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<error id=\"" + errorId + "\">");
		xml.append("<properties>");
		xml.append("<message>" + message + "</message>");
		xml.append("<details>" + details + "</details>");
		xml.append("<uri>" + uri + "</uri>");
		xml.append("</properties>");
		xml.append("</error>");
		return xml.toString();
	}

	/**
	 * This function should be called when you want to put an error message in
	 * the output of a script
	 * 
	 * @param errorId
	 * @param message
	 * @param details
	 * @param uri
	 * @return
	 */

	public static String getFSXMLErrorMessage(String errorId, String message, String details, String uri) {
		StringBuffer xml = new StringBuffer("<fsxml>");
		xml.append("<properties>");
		xml.append("<message>" + message + "</message>");
		xml.append("<details>" + details + "</details>");
		xml.append("<uri>" + uri + "</uri>");
		xml.append("</properties>");
		xml.append("</fsxml>");
		return xml.toString();
	}
	
	/**
	 * This function wraps the supplied parameters in a Springfield compliant
	 * XML status message and returns it
	 * 
	 * @param statusId
	 * @param message
	 * @param details
	 * @param uri
	 * @return
	 */

	public static String getStatusMessage(String statusId, String message, String details, String uri) {
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<status id=\""+statusId+"\">");
		xml.append("<properties>");
		xml.append("<message>" + message + "</message>");
		xml.append("<details>" + details + "</details>");
		xml.append("<uri>" + uri + "</uri>");
		xml.append("</properties>");
		xml.append("</status>");
		return xml.toString();
	}

	/**
	 * This function wraps the supplied parameters in a Springfield compliant
	 * XML status message and returns it
	 * 
	 * @param message
	 * @param details
	 * @param uri
	 * @return
	 */

	public static String getStatusMessage(String message, String details, String uri) {
		StringBuffer xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<status id=\"400\">");
		xml.append("<properties>");
		xml.append("<message>" + message + "</message>");
		xml.append("<details>" + details + "</details>");
		xml.append("<uri>" + uri + "</uri>");
		xml.append("</properties>");
		xml.append("</status>");
		return xml.toString();
	}

	/**
	 * This function wraps the supplied parameters in a Springfield compliant
	 * XML status message and returns it
	 * 
	 * @param message
	 * @param details
	 * @param uri
	 * @return
	 */

	public static String getFSXMLStatusMessage(String message, String details, String uri) {
		StringBuffer xml = new StringBuffer("<fsxml>");
		xml.append("<properties>");
		xml.append("<message>" + message + "</message>");
		xml.append("<details>" + details + "</details>");
		xml.append("<uri>" + uri + "</uri>");
		xml.append("</properties>");
		xml.append("</fsxml>");
		return xml.toString();
	}

	/**
	 * This functions builds the XML for the new ingest call j.blom@noterik.nl
	 * in smithers.
	 * 
	 * @param uri
	 * @param source
	 * @param collectionId
	 * @return
	 */

	public static String getIngestXML(String uri, String source, String collectionId) {
		StringBuffer xml = new StringBuffer("<fsxml>");
		xml.append("<properties>");
		xml.append("<smart>true</smart>");
		xml.append("<destinationuri>" + uri + "</destinationuri>");
		xml.append("<source>" + source + "</source>");
		xml.append("<collection>" + collectionId + "</collection>");
		xml.append("</properties>");
		xml.append("</fsxml>");
		return xml.toString();
	}
	
	/**
	 * This functions builds the XML for the new ingest call j.blom@noterik.nl
	 * in smithers.
	 * 
	 * @param uri
	 * @param source
	 * @param collectionId
	 * @param preferredStream
	 * @return
	 */
	public static String getIngestXML(String uri, String source, String collectionId, String preferredStream) {
		StringBuffer xml = new StringBuffer("<fsxml>");
		xml.append("<properties>");
		xml.append("<smart>true</smart>");
		xml.append("<destinationuri>" + uri + "</destinationuri>");
		xml.append("<source>" + source + "</source>");
		xml.append("<collection>" + collectionId + "</collection>");
		xml.append("<preferred>"+preferredStream+"</preferred>");
		xml.append("</properties>");
		xml.append("</fsxml>");
		return xml.toString();
	}
	
	public static String getIngestXMLForDumbClient(String user, String source, String videoId, String collectionId, String profile) {
		StringBuffer xml = new StringBuffer("<fsxml>");
		xml.append("<properties>");
		xml.append("<smart>false</smart>");
		xml.append("<user>" + user + "</user>");
		xml.append("<source>" + source + "</source>");
		xml.append("<collection>" + collectionId + "</collection>");
		xml.append("<video>" + videoId + "</video>");
		xml.append("<profile>" + profile + "</profile>");
		xml.append("</properties>");
		xml.append("</fsxml>");
		return xml.toString();
	}

	public static String getAddReferIdXML(String referUri) {
		return "<fsxml><attributes><referid>" + referUri + "</referid></attributes></fsxml>";
	}
	
	/** 
	 * This function is only called by APU
	 * @param p
	 * @return
	 */

	/*
	public static String getVideoPropertiesFromProfile(EncodingProfile p) {			
		StringBuffer xml = new StringBuffer("<fsxml>");
		xml.append("<properties>");
		xml.append("<transcoder>apu</transcoder>");
		if (p.getFormat() != null) {
			xml.append("<format>" + p.getFormat() + "</format>");
		}
		if (p.isOriginal()) {
			xml.append("<original>true</original>");
		}
		if (p.getExtension() != null) {
			xml.append("<extension>" + p.getExtension() + "</extension>");
		}
		if (p.getWidth() != null) {
			xml.append("<wantedwidth>" + p.getWidth() + "</wantedwidth>");
		}
		if (p.getHeight() != null) {
			xml.append("<wantedheight>" + p.getHeight() + "</wantedheight>");
		}
		if (p.getBitRate() != null) {
			xml.append("<wantedbitrate>" + p.getBitRate() + "</wantedbitrate>");
		}
		if (p.getFrameRate() != null) {
			xml.append("<wantedframerate>" + p.getFrameRate() + "</wantedframerate>");
		}
		if (p.getKeyFrameRate() != null) {
			xml.append("<wantedkeyframerate>" + p.getKeyFrameRate() + "</wantedkeyframerate>");
		}
		if (p.getAudioBitRate() != null) {
			xml.append("<wantedaudiobitrate>" + p.getAudioBitRate() + "</wantedaudiobitrate>");
		}
		xml.append("</properties>");
		xml.append("</fsxml>");
		return xml.toString();
	}
	*/

	public static String getDefaultVideoProperties(String title, String description) {
		StringBuffer xml = new StringBuffer("<fsxml>");
		xml.append("<properties>");
		xml.append("<title>" + title + "</title>");
		xml.append("<description>" + description + "</description>");
		xml.append("</properties>");
		xml.append("</fsxml>");
		return xml.toString();
	}

	public static String getDefaultScreensProperties(short width, short height, String interval, boolean redo) {
		StringBuffer xml = new StringBuffer("<fsxml>");
		xml.append("<properties>");
		xml.append("<size>" + width + "x" + height + "</size>");
		xml.append("<interval>" + interval + "</interval>");
		xml.append("<redo>" + new Boolean(redo).toString() + "</redo>");
		xml.append("</properties>");
		xml.append("</fsxml>");
		return xml.toString();
	}

	public static String wrapInFsxml(String data, Map<String, String> properties) {
		String key;
		StringBuffer props  = new StringBuffer();
		for(Iterator<String> i =  properties.keySet().iterator(); i.hasNext(); ) {
			key = i.next();
			props.append("<"+key+">"+properties.get(key)+"</"+key+">");
		}
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
		xml.append("<fsxml>");
		xml.append("<properties>"+props.toString()+"</properties>");
		xml.append(data);
		xml.append("</fsxml>");
		
		return xml.toString();
	}
}
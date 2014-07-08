/*
 * Created on Nov 7, 2008
 */
package org.springfield.mojo.ftp;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

public class URIParser {

	private static Logger logger = Logger.getLogger(URIParser.class);
	
	/**
	 * Returns the last part of the given URI
	 * 
	 * @param uri
	 * @return
	 */
	public static String getCurrentUriPart(String uri) {
		if (uri == null || uri.equals("")) {
			return null;
		}
		if (uri.charAt(uri.length() - 1) == '/') {
			uri = uri.substring(0, uri.length() - 1);
		}
		return uri.substring(uri.lastIndexOf("/") + 1, uri.length());
	}

	/**
	 * Returns the part before the last part of the given URI
	 * 
	 * @param uri
	 * @return
	 */

	public static String getParentUriPart(String uri) {
		if (uri == null || uri.equals("")) {
			return null;
		}
		if (uri.charAt(uri.length() - 1) == '/') {
			uri = uri.substring(0, uri.length() - 1);
		}
		uri = uri.substring(0, uri.length() - getCurrentUriPart(uri).length() - 1);
		return uri.substring(uri.lastIndexOf("/") + 1, uri.length());
	}
	
	/**
	 * Returns the parent URI
	 * 
	 * @param uri
	 * @return
	 */
	public static String getPreviousUri(String uri){
		if(uri == null || uri.equals("")){
			return null;
		}
		uri = removeLastSlash(uri);
		return uri.substring(0, uri.lastIndexOf("/"));
	}
	
	/**
	 * removes the slash from the beginning of the uri
	 * 
	 * @param uri
	 * @return
	 */
	public static String removeFirstSlash(String uri){
		if(uri.indexOf("/") == 0){
			return uri.substring(1); 
		}else{
			return uri;
		}
	}
	
	/**
	 * removes the slash from the ending of the uri
	 * 
	 * @param uri
	 * @return
	 */
	public static String removeLastSlash(String uri){
		if(uri.lastIndexOf("/") == uri.length()-1){
			return uri.substring(0,uri.length()-1); 
		}else{
			return uri;
		}
	}
	
	/**
	 * Returns the ID for a specified type
	 * @param uri
	 * @param type
	 * @return
	 */
	private static String getTypeIdFromUri(String uri, String type) {
		String value = null;
		String typeUriPart = "/"+type+"/";
		int index1, index2;

		index1 = uri.indexOf(typeUriPart);
		if (index1 != -1) {
			index1 += typeUriPart.length();
			index2 = uri.indexOf("/", index1);
			if (index2 != -1) {
				value = uri.substring(index1, index2);
			} else {
				value = uri.substring(index1);
			}
		}
		return value;
	}
	
	public static String getDomainIdFromUri(String uri) {
		return getTypeIdFromUri(uri,"domain");
	}
	public static String getUserIdFromUri(String uri) {
		return getTypeIdFromUri(uri,"user");
	}
	public static String getPresentationIdFromUri(String uri) {
		return getTypeIdFromUri(uri,"presentation");
	}
	public static String getCollectionIdFromUri(String uri) {
		return getTypeIdFromUri(uri,"collection");
	}
	public static String getVideoIdFromUri(String uri) {
		return getTypeIdFromUri(uri,"video");
	}
	public static String getRawvideoIdFromUri(String uri) {
		return getTypeIdFromUri(uri,"rawvideo");
	}
	public static String getImageIdFromUri(String uri) {
		return getTypeIdFromUri(uri,"image");
	}
	public static String getAudioIdFromUri(String uri) {
		return getTypeIdFromUri(uri,"audio");
	}
	public static String getScreensIdFromUri(String uri){
		return getTypeIdFromUri(uri,"screens");
	}
	public static String getServiceIdFromUri(String uri){
		return getTypeIdFromUri(uri,"service");
	}
	public static String getExportIdFromUri(String uri){
		return getTypeIdFromUri(uri,"service");
	}

	/**
	 * @deprecated Use getDomainIdFromUri
	 * @param uri
	 * @return
	 */
	public static String getDomainFromUri(String uri) {
		return getDomainIdFromUri(uri);
	}
	
	/**
	 * @deprecated Use getUserIdFromUri
	 * @param uri
	 * @return
	 */
	public static String getUserFromUri(String uri) {
		return getUserIdFromUri(uri);
	}
	
	/**
	 * @deprecated Use getServiceIdFromUri
	 * @param uri
	 * @return
	 */
	public static String getServiceTypeFromUri(String uri){
		return getScreensIdFromUri(uri);
	}

	/**
	 * @deprecated
	 * @param uri
	 * @param type
	 * @return
	 */
	public static String getRawIdFromUri(String uri, String type) {
		String rawId = null;
		int index1, index2;

		index1 = uri.indexOf("/raw" + type + "/");
		if (index1 != -1) {
			index1 += ("/raw" + type + "/").length();
			index2 = uri.indexOf("/", index1);
			if (index2 != -1) {
				rawId = uri.substring(index1, index2);
			} else {
				rawId = uri.substring(index1);
			}
		}
		return rawId;
	}
	
	public static String getParentUri(String uri) {
		String parentUri = "";
		uri = removeFirstSlash(uri);
		String[] parts = uri.split("/");
		for(int i=0; i<parts.length-2; i++) {
			parentUri += "/"+parts[i];
		}
		return parentUri;
	}
	
	/**
	 * Check if uri end with a resource id
	 * 
	 * uri are always made up as:
	 * /type/id/type/id ... etc
	 * 
	 * @param uri
	 * @return
	 */
	public static boolean isResourceId(String uri){
		if(uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		if(uri.endsWith("/")) {
			uri = uri.substring(0,uri.length()-1);
		}
		String[] parts = uri.split("/");
		if(parts.length%2 == 0) { // even
			return true;
		}
		return false;
	}

	/**
	 * Reslove '.' and '..' from uri
	 * 
	 * @param uri
	 * @param currentWorkingUri
	 * @return
	 */
	public static String resolveLocalUri(String uri, String currentWorkingUri) {
		if(!currentWorkingUri.endsWith("/"))
			currentWorkingUri = currentWorkingUri + "/";
		
		URI curUriObj, uriObj = null;		
		try {
			curUriObj = new URI(currentWorkingUri);
			uriObj = curUriObj.resolve(uri);
		} catch (URISyntaxException e) {
			logger.error("",e);
		}
		if(uriObj!=null) {
			return uriObj.toString();
		}		
		return uri;
	}
	
	/**
	 * Get the resource type of a uri
	 * 
	 * uri are always made up as:
	 * /type/id/type/id ... etc
	 * 
	 * @param uri
	 * @return
	 */
	public static String getResourceTypeFromUri(String uri){
		String cp = getCurrentUriPart(uri);
		if(cp.equals("properties")) {
			uri = uri.substring(0,uri.indexOf("properties")-1);
			cp = getCurrentUriPart(uri);
		}		
		String pp = getParentUriPart(uri);
		if(uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		if(uri.endsWith("/")) {
			uri = uri.substring(0,uri.length()-1);
		}
		String[] parts = uri.split("/");
		if(parts.length%2 == 0) { // even, so current part is ID
			return pp;
		}
		return cp;
	}
	
}
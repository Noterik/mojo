/* 
* EntityProxy.java
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

/**
 * EntityProxy.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2014
 * @package org.springfield.mojo.linkedtv
 * 
 */
public class EntityProxy {
	private String dbpediaUri;
	
	public EntityProxy(String dbpediaUri) {
		this.dbpediaUri = dbpediaUri;
		
		getEntityProxyInformation();
	}
	
	private void getEntityProxyInformation() {
		
	}
	
	public String getLabel() {
		return "";
	}
	
	public String[] getThumbnailUri() {
		String[] thumbnailUris = {};
		return thumbnailUris;
	}
	
	public String getAbstract() {
		return "";
	}
	
	public String getBirthDate() {
		return "";
	}
	
	public String getDeathDate() {
		return "";
	}
	
	public String getBirthPlace() {
		return "";
	}
	
	public String getDeathPlace() {
		return "";
	}
	
	public String getNationality() {
		return "";
	}
	
	public String[] getProfession() {
		String[] professions = {};
		return professions;
	}
	
	public String[] getArtStyle() {
		String[] artStyles = {};
		return artStyles;
	}
	
}

/* 
* GAINObjectEntity.java
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

import org.springfield.fs.FsNode;

/**
 * GAINObjectEntity.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2014
 * @package org.springfield.mojo.linkedtv
 * 
 */
public class GAINObjectEntity {
	public String source;
	public String lod;
	public String type;
	public String label;
	public String typeLabel;
	public String entityType;
	public int confidence;
	public int relevance;
	
	public GAINObjectEntity(FsNode item) {
		this.source = item.getProperty("source") == null ? "" : item.getProperty("source");
		this.lod = item.getProperty("locator") == null ? "" : item.getProperty("locator");
		this.type = item.getProperty("dbpediatype") == null ? "" : item.getProperty("dbpediatype"); 
		this.label = item.getProperty("title") == null ? "" : item.getProperty("title"); 
		this.typeLabel = item.getProperty("type") == null ? "" : item.getProperty("type");
		this.entityType = ""; //should be either named or common entity, but optional, so left empty
		//confidence and relevance currently not yet available
		this.confidence = 1;
		this.relevance = 1;
	}
}

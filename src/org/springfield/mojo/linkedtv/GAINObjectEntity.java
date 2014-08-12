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

import org.dom4j.Node;

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
	
	public GAINObjectEntity(Node item) {
		this.source = item.selectSingleNode("//properties/source") == null ? "" : item.selectSingleNode("//properties/source").getText();
		this.lod = item.selectSingleNode("//properties/locator") == null ? "" : item.selectSingleNode("//properties/locator").getText();
		this.type = item.selectSingleNode("//properties/dbpediatype") == null ? "" : item.selectSingleNode("//properties/dbpediatype").getText();
		this.label = item.selectSingleNode("//properties/title") == null ? "" : item.selectSingleNode("//properties/title").getText();
		this.typeLabel = item.selectSingleNode("//properties/type") == null ? "" : item.selectSingleNode("//properties/type").getText();
		this.entityType = item.selectSingleNode("//properties/entitytype") == null ? "" : item.selectSingleNode("//properties/entitytype").getText();
		//confidence and relevance currently not yet available
		this.confidence = 1;
		this.relevance = 1;
	}
}

/* 
* FSSortNode.java
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

/**
 * FSSortNode
 * 
 * @author Daniel Ockeloen
 * @copyright Copyright: Noterik B.V. 2012
 * @package org.springfield.lou.fs
 *
 */
public class FSSortNode implements Comparable<FSSortNode> {
	
	public String sortfield;
	public String direction;
	public FsNode node;
	
	public FSSortNode(FsNode n,String f,String d) {
		sortfield = f;
		direction = d;
		node = n;
	}

	public int compareTo(FSSortNode n) throws ClassCastException {
		 if (direction.equals("up")) { 
			 return sortfield.compareTo(n.sortfield);
		 } else if (direction.equals("int_up")) {
			 Integer a, b;
			 
			 try {
				 a = Integer.parseInt(sortfield);
			 } catch (NumberFormatException e) {
				 a = 0;
			 }
			 
			 try {
				 b = Integer.parseInt(n.sortfield);
			 } catch (NumberFormatException e) {
				 b = 0;
			 }

			 return a.compareTo(b);
		 } else if (direction.equals("int_down")) {
			 Integer a, b;
			 
			 try {
				 a = Integer.parseInt(sortfield);
			 } catch (NumberFormatException e) {
				 a = 0;
			 }
			 
			 try {
				 b = Integer.parseInt(n.sortfield);
			 } catch (NumberFormatException e) {
				 b = 0;
			 }
			 
			 return b.compareTo(a);
		 } else {
			 return n.sortfield.compareTo(sortfield); 
		 }
	}
}

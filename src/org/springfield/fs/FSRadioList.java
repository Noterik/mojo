package org.springfield.fs;

import org.springfield.fs.FSList;
import org.springfield.fs.FsNode;

public class FSRadioList extends FSList {
	
	public void addOption(String name,String value,String checked) {
		FsNode node = new FsNode("option",name);
		node.setProperty("value", value);
		if (name.equals(checked)) {
			node.setProperty("checked","checked");
		} else {
			node.setProperty("checked","");
		}
		this.addNode(node);
	}

}

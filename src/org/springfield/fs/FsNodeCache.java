package org.springfield.fs;

import java.util.*;


public class FsNodeCache {
	private static Map<String, FsNode> nodecache = new HashMap<String, FsNode>();
	private static int total = 0;
	private static int hit = 0;
	

	public static FsNode getCachedNode(String path) {
		total++;
		FsNode n = nodecache.get(path);
		if (n!=null) {
			hit++;
			return n;
		}
		return null;
	}
	
	public static void setCachedNode(String path,FsNode node) {
		nodecache.put(path, node);
	}
	
	public static void removeCachedNode(String path) {
		nodecache.remove(path);
	}
	
	public static int getTotalRequests() {
		return total;
	}
	
	public static int getHitRequests() {
		return hit;
	}
	
	public static int getHitRate() {
		if (total==0) return 0;
		return Math.round((hit*100)/total);
	}
	
	public static int size() {
		return nodecache.size();
	}
	
}

/* 
* GAIN.java
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

import org.json.simple.JSONObject;
import org.springfield.mojo.http.HttpHelper;
import org.springfield.mojo.http.Response;

/**
 * GAIN.java
 *
 * @author Pieter van Leeuwen
 * @copyright Copyright: Noterik B.V. 2014
 * @package org.springfield.mojo.linkedtv
 * 
 */
public class GAIN {
	private static String CLIENT_TYPE = "Springfield/Lou";
	private static String CLIENT_VERSION = "0.2";
	private static String GAIN_URI = "http://dev.wa.vse.cz/gain/listener";
	
	
	private String accountId = "";
	private String applicationId = "";
	private String screenId = "";
	private String mediaresourceId = "";
	private String userId = "";
	private String type = "";
	private String category = "";
	private String action = "";
	private String objectId = "";
	private int videoTime = 0;	
	private String orientation = "";
	private String viewtime = "";
	
	public GAIN(String accountId, String applicationId) {
		this.accountId = accountId;
		this.applicationId = applicationId;
	}
	
	public void application_new() {
		this.type = "event";
		this.category = "application";
		this.action = "new";
		this.screenId = "";
		this.mediaresourceId = "";
		this.userId = "";
		this.objectId = "";
		
		sendGAINRequest();
	}
	
	public void application_remove() {
		this.type = "event";
		this.category = "application";
		this.action = "remove";
		this.screenId = "";
		this.mediaresourceId = "";
		this.userId = "";
		this.objectId = "";
		
		sendGAINRequest();
	}
	
	public void screen_new(String screenId) {
		this.type = "event";
		this.category = "screen";
		this.action = "new";
		this.screenId = screenId;
		this.mediaresourceId = "";
		this.userId = "";
		this.objectId = "";
		
		sendGAINRequest();
	}
	
	public void screen_remove(String screenId) {
		this.type = "event";
		this.category = "screen";
		this.action = "remove";
		this.screenId = screenId;
		this.mediaresourceId = "";
		this.userId = "";
		this.objectId = "";
		
		sendGAINRequest();
	}
	
	public void screen_orientation(String screenId, String orientation) {
		this.type = "event";
		this.category = "screen";
		this.action = "orientation";
		this.screenId = screenId;
		this.orientation = orientation;
		this.mediaresourceId = "";
		this.userId = "";
		this.objectId = "";
		
		sendGAINRequest();
	}
	
	public void player_play(String screenId, String mediaresourceId, int videoTime) {
		this.type = "event";
		this.category = "player";
		this.action = "play";
		this.screenId = screenId;
		this.mediaresourceId = mediaresourceId;
		this.userId = "";
		this.objectId = "";
		this.videoTime = videoTime;
		
		sendGAINRequest();
	}
	
	public void player_pause(String screenId, String mediaresourceId, int videoTime) {
		this.type = "event";
		this.category = "player";
		this.action = "pause";
		this.screenId = screenId;
		this.mediaresourceId = mediaresourceId;
		this.userId = "";
		this.objectId = "";
		this.videoTime = videoTime;
		
		sendGAINRequest();
	}
	
	public void player_stop(String screenId, String mediaresourceId, int videoTime) {
		this.type = "event";
		this.category = "player";
		this.action = "stop";
		this.screenId = screenId;
		this.mediaresourceId = mediaresourceId;
		this.userId = "";
		this.objectId = "";
		this.videoTime = videoTime;
		
		sendGAINRequest();
	}
	
	public void user_login(String userId) {
		this.type = "event";
		this.category = "user";
		this.action = "login";
		this.screenId = "";
		this.mediaresourceId = "";
		this.userId = userId;
		this.objectId = "";
		
		sendGAINRequest();
	}
	
	public void user_logout(String userId) {
		this.type = "event";
		this.category = "user";
		this.action = "logout";
		this.screenId = "";
		this.mediaresourceId = "";
		this.userId = userId;
		this.objectId = "";

		sendGAINRequest();
	}
	
	public void bookmark(String userId, String objectId) {
		this.type = "event";
		this.category = "user";
		this.action = "bookmark";
		this.screenId = "";
		this.mediaresourceId = "";
		this.userId = userId;
		this.objectId = objectId;
		
		this.userId = userId;
		sendGAINRequest();
	}
	
	public void select(String userId, String objectId) {
		this.type = "event";
		this.category = "user";
		this.action = "select";
		this.screenId = "";
		this.mediaresourceId = "";
		this.userId = userId;
		this.objectId = objectId;

		sendGAINRequest();
	}
	
	public void viewtime(String userId, String objectId) {
		this.type = "event";
		this.category = "user";
		this.action = "viewtime";
		this.screenId = "";
		this.mediaresourceId = "";
		this.userId = userId;
		this.objectId = objectId;

		sendGAINRequest();
	}
	
	//keep alive signal
	
	private void sendGAINRequest() {
		JSONObject json = new JSONObject();
		json.put("accountId", accountId);
		json.put("applicationId", applicationId);
		json.put("screenId", screenId);
		json.put("userId",userId);
		json.put("mediaresourceId", mediaresourceId);
		json.put("objectId", objectId);
		json.put("type", type);
		
		JSONObject attributes = new JSONObject();
		attributes.put("category", category);
		attributes.put("action", action);
		
		//screen orientation specific
		if (category.equals("screen") && action.equals("orientation")) {
			attributes.put("orientation", orientation);
		}
		
		//player event specific
		if (category.equals("player")) {
			attributes.put("location", videoTime);
		}
		
		//user viewtime specific
		if (category.equals("user") && action.equals("viewtime")) {
			attributes.put("viewtime", viewtime);
		}
		
		JSONObject client = new JSONObject();
		client.put("type",CLIENT_TYPE);
		client.put("version", CLIENT_VERSION);
		
		attributes.put("client", client);
		json.put("attributes", attributes);
		
		String body = json.toString();
		
		System.out.println(body);
		
		Response response = HttpHelper.sendRequest("POST", GAIN_URI, body, "application/json");
		if (response.getStatusCode() != 201) {
			System.out.println("GAIN did return unexpected response code "+response.getStatusCode());
		}
	}	
}

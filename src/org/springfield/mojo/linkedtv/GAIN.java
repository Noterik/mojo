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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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
	private static String CLIENT_VERSION = "0.3";
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
	private String videoTime = "";	
	private String orientation = "";
	private String viewtime = "";
	private List<GAINObjectEntity> entities = new ArrayList<GAINObjectEntity>();
	
	public GAIN(String accountId, String applicationId) {
		this.accountId = accountId;
		this.applicationId = applicationId;
	}
	
	public void application_new() {
		this.type = "event";
		this.category = "application";
		this.action = "new";
		
		sendEventRequest();
	}
	
	public void application_remove() {
		this.type = "event";
		this.category = "application";
		this.action = "remove";
		
		sendEventRequest();
	}
	
	public void screen_new(String screenId) {
		this.type = "event";
		this.category = "screen";
		this.action = "new";
		this.screenId = screenId;
		
		sendEventRequest();
	}
	
	public void screen_remove(String screenId) {
		this.type = "event";
		this.category = "screen";
		this.action = "remove";
		this.screenId = screenId;
		
		sendEventRequest();
	}
	
	public void screen_orientation(String screenId, String orientation) {
		this.type = "event";
		this.category = "screen";
		this.action = "orientation";
		this.screenId = screenId;
		this.orientation = orientation;
		
		sendEventRequest();
	}
	
	public void player_play(String screenId, String mediaresourceId, String videoTime) {
		this.type = "event";
		this.category = "player";
		this.action = "play";
		this.screenId = screenId;
		this.mediaresourceId = mediaresourceId;
		this.videoTime = videoTime;
		
		sendEventRequest();
	}
	
	public void player_pause(String screenId, String mediaresourceId, String videoTime) {
		this.type = "event";
		this.category = "player";
		this.action = "pause";
		this.screenId = screenId;
		this.mediaresourceId = mediaresourceId;
		this.videoTime = videoTime;
		
		sendEventRequest();
	}
	
	public void player_stop(String screenId, String mediaresourceId, String videoTime) {
		this.type = "event";
		this.category = "player";
		this.action = "stop";
		this.screenId = screenId;
		this.mediaresourceId = mediaresourceId;
		this.videoTime = videoTime;
		
		sendEventRequest();
	}
	
	public void user_login(String userId, String screenId) {
		this.type = "event";
		this.category = "user";
		this.action = "login";
		this.screenId = screenId;
		this.userId = userId;
		
		sendEventRequest();
	}
	
	public void user_logout(String userId, String screenId) {
		this.type = "event";
		this.category = "user";
		this.action = "logout";
		this.screenId = screenId;
		this.userId = userId;

		sendEventRequest();
	}
	
	public void user_bookmark(String userId, String objectId, String screenId) {
		this.type = "event";
		this.category = "user";
		this.action = "bookmark";
		this.screenId = screenId;
		this.userId = userId;
		this.objectId = objectId;		
		this.userId = userId;
		
		sendEventRequest();
	}
	
	public void user_select(String userId, String objectId, String screenId) {
		this.type = "event";
		this.category = "user";
		this.action = "select";
		this.screenId = screenId;
		this.userId = userId;
		this.objectId = objectId;

		sendEventRequest();
	}
	
	public void user_viewtime(String userId, String objectId, String screenId, String viewtime) {
		this.type = "event";
		this.category = "user";
		this.action = "viewtime";
		this.screenId = screenId;
		this.userId = userId;
		this.objectId = objectId;
		this.viewtime = viewtime;

		sendEventRequest();
	}
	
	public void updateEntities(List<GAINObjectEntity> entities) {
		this.entities = entities;
	}
	
	//External context requests from KINECT
	public void sendContextRequest(String context, String videoTime) {
		this.videoTime = videoTime;
		JSONParser parser = new JSONParser();
		
		try {
			JSONObject json = (JSONObject) parser.parse(context);
			json.put("accountId", accountId);
			json.put("applicationId", applicationId);
			json.put("userId", userId);
			json.put("mediaresourceId", mediaresourceId);
			
			//add object information
			JSONObject object = new JSONObject();
			object.put("objectId", objectId);
			
			//add entities array
			JSONArray entities = new JSONArray();			
			
			for (Iterator<GAINObjectEntity> it = this.entities.iterator(); it.hasNext(); ) {
				GAINObjectEntity ent = it.next();
				
				JSONObject entity = new JSONObject();			
				entity.put("source", ent.source);
				entity.put("lod", ent.lod);
				entity.put("type", ent.entityType);
				entity.put("label", ent.label);
				entity.put("typeLabel", ent.typeLabel);
				entity.put("entityType", ent.entityType);
				entity.put("confidence", ent.confidence);
				entity.put("relevance", ent.relevance);
				
				entities.add(entity);
			}
			
			object.put("entities", entities);
			json.put("object", object);
			
			JSONObject attributes = (JSONObject) json.get("attributes");
			if (attributes != null) {
				attributes.put("location", videoTime);
			}
			
			String body = json.toString();
			
			sendRequest(body);
		} catch (ParseException e) {
			System.out.println("Could not parse context json "+context);
		}		
	}
	
	//Keep alive request
	public void sendKeepAliveRequest() {
		JSONObject json = new JSONObject();
		json.put("accountId", accountId);
		json.put("applicationId", applicationId);
		json.put("screenId", screenId);
		json.put("userId",userId);
		json.put("mediaresourceId", mediaresourceId);
		json.put("type", type);
		
		//add object information
		JSONObject object = new JSONObject();
		object.put("objectId", objectId);
		
		//add entities array
		JSONArray entities = new JSONArray();			
		
		for (Iterator<GAINObjectEntity> it = this.entities.iterator(); it.hasNext(); ) {
			GAINObjectEntity ent = it.next();
			
			JSONObject entity = new JSONObject();			
			entity.put("source", ent.source);
			entity.put("lod", ent.lod);
			entity.put("type", ent.entityType);
			entity.put("label", ent.label);
			entity.put("typeLabel", ent.typeLabel);
			entity.put("entityType", ent.entityType);
			entity.put("confidence", ent.confidence);
			entity.put("relevance", ent.relevance);
			
			entities.add(entity);
		}
		
		object.put("entities", entities);		
		json.put("object", object);

		//attach the keep alive attribute
		JSONObject attributes = new JSONObject();
		attributes.put("action", "keepalive");
		
		json.put("attributes", attributes);
		
		String body = json.toString();
		
		sendRequest(body);
	}
	
	//Event request
	private void sendEventRequest() {
		JSONObject json = new JSONObject();
		json.put("accountId", accountId);
		json.put("applicationId", applicationId);
		json.put("screenId", screenId);
		json.put("userId",userId);
		json.put("mediaresourceId", mediaresourceId);
		json.put("type", type);
		
		//add object information
		JSONObject object = new JSONObject();
		object.put("objectId", objectId);
		
		//add entities array
		JSONArray entities = new JSONArray();			
		
		for (Iterator<GAINObjectEntity> it = this.entities.iterator(); it.hasNext(); ) {
			GAINObjectEntity ent = it.next();
			
			JSONObject entity = new JSONObject();			
			entity.put("source", ent.source);
			entity.put("lod", ent.lod);
			entity.put("type", ent.entityType);
			entity.put("label", ent.label);
			entity.put("typeLabel", ent.typeLabel);
			entity.put("entityType", ent.entityType);
			entity.put("confidence", ent.confidence);
			entity.put("relevance", ent.relevance);
			
			entities.add(entity);
		}
		
		object.put("entities", entities);		
		json.put("object", object);
		
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
		
		sendRequest(body);
	}
	
	private void sendRequest(String json) {
		System.out.println(json);
		
		/*Response response = HttpHelper.sendRequest("POST", GAIN_URI, json, "application/json");
		if (response.getStatusCode() != 201) {
			System.out.println("GAIN did return unexpected response code "+response.getStatusCode());
			//System.out.println(response.getResponse());
		} else {
			//System.out.println("successful response "+response.getStatusCode()+" content "+response.getResponse());
		}*/
	}
}

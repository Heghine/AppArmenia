package com.fluger.app.armenia.data;

import org.json.JSONObject;

public class AppCategoryItemData {
	
	public static final String TYPE_TRENDING = "trending";
	public static final String TYPE_TOP = "top";
	public static final String TYPE_NEW = "new";
	
	public int id;
	public String type; // shows if item is in 'trending', 'new' or 'top' lists
	public int category; // shows if item is 'application', 'notification', 'ringtone', etc
	public String title;
	public String description;
	public String imageUrl;
	public String imageThumbnailUrl;
	public String tagsStr;
	public String[] tags;
	public int downloadCount;
	public int rating;
	public boolean isFree;
	public String audioUrl;
	
	public AppCategoryItemData() {
		
	}
	
	public AppCategoryItemData(JSONObject data) {
		id = data.optInt("id", 0);
		title = data.optString("title", "");
		description = data.optString("description", "");
		imageUrl = data.optString("image", "");
		imageThumbnailUrl = data.optString("image_thumb", "");
		downloadCount = data.optInt("downloads", 5);
		rating = data.optInt("rating", 2);
		if (data.has("free")) {
			isFree = data.optBoolean("free", true);
		}
		if (data.has("tags")) {
			tagsStr = data.optString("tags", "");
			tags = data.optString("tags", "").split(",");
		}
		if (data.has("and_audio")) {
			audioUrl = data.optString("and_audio", "");
		}
	}
	
	@Override
	public AppCategoryItemData clone() throws CloneNotSupportedException {
		AppCategoryItemData newData = new AppCategoryItemData();
		
		newData.id = this.id;
		newData.type = this.type;
		newData.category = this.category;
		newData.title = this.title;
		newData.description = this.description;
		newData.imageUrl = this.imageUrl;
		newData.imageThumbnailUrl = this.imageThumbnailUrl;
		newData.downloadCount = this.downloadCount;
		newData.rating = this.rating;
		newData.isFree = this.isFree;
		newData.tags = this.tags;
		newData.audioUrl = this.audioUrl;
		
		return newData;
	}
}

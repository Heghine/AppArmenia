package com.fluger.app.armenia.data;

import org.json.JSONObject;

public class TagItemData {
	
	public int id;
	public String tag;
	public int category;
	public int position;
	
	public TagItemData() {
		
	}
	
	public TagItemData(JSONObject data) {
		id = data.optInt("id", 0);
		tag = data.optString("tag", "");
		category = data.optInt("category", 1);
		position = data.optInt("position", 0);
	}

}

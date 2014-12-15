package com.fluger.app.armenia.data;

import java.util.ArrayList;
import java.util.List;

public class AppCategory {
	public String name;
	public List<AppCategoryItemData> childrenToShow = new ArrayList<AppCategoryItemData>();
	public ArrayList<AppCategoryItemData> children = new ArrayList<AppCategoryItemData>();
	
	public AppCategory(String name) {
		this.name = name;
	}
}

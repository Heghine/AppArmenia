package com.fluger.app.armenia.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;

import com.fluger.app.armenia.HomeActivity;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.backend.API;
import com.fluger.app.armenia.backend.API.RequestObserver;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.data.TagItemData;
import com.fluger.app.armenia.manager.AppArmeniaManager;
import com.fluger.app.armenia.util.CategoriesAdapter;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.ItemsAdapter;
import com.fluger.app.armenia.util.Utils;

public class ApplicationsActivity extends Activity implements ActionBar.TabListener {
	
	private int position;
	private ListView categoriesList;
	private ListView itemsList;
	private CategoriesAdapter categoriesAdapter;
	private ItemsAdapter itemsAdapter;
	private ArrayList<AppCategoryItemData> items = new ArrayList<AppCategoryItemData>();
	private ArrayList<TagItemData> categories = new ArrayList<TagItemData>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_applications);
		
		position = getIntent().getIntExtra("position", 0);
		
		getItemsListByType(Constants.TYPE_TRENDING);
		
		
		API.getTagsList(Constants.APPLICATIONS_CATEGORY_POSITION, new RequestObserver() {
			
			@Override
			public void onSuccess(JSONObject response) throws JSONException {
				JSONArray tagsJson = response.getJSONArray("values");
				for (int i = 0; i < tagsJson.length(); i++) {
					categories.add(new TagItemData(tagsJson.getJSONObject(i)));
				}
				
				ApplicationsActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						categoriesAdapter.notifyDataSetChanged();
					}
				});
			}
			
			@Override
			public void onError(String response, Exception e) {
				
			}
		});
		
		categoriesAdapter = new CategoriesAdapter(this, R.layout.item_category_list, categories);
		categoriesList = (ListView) findViewById(R.id.categories_list);
		categoriesList.setAdapter(categoriesAdapter);
		categoriesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				API.getAppsSearchList(5, 0, categories.get(position).tag, new RequestObserver() {
					
					@Override
					public void onSuccess(JSONObject response) throws JSONException {
						AppArmeniaManager.getInstance().resetApplicationsData();
						JSONArray result = response.getJSONArray("values");
						for (int i = 0; i < result.length(); i++) {
							JSONObject categoryJson = result.getJSONObject(i);
							String type = categoryJson.optString("type", "");
							JSONArray categoryItemsJson = categoryJson.getJSONArray("items");
							for (int j = 0; j < categoryItemsJson.length(); j++) {
								AppCategoryItemData categoryItemData = new AppCategoryItemData(categoryItemsJson.getJSONObject(j));
								categoryItemData.type = type;
								categoryItemData.category = Constants.APPLICATIONS_CATEGORY_POSITION;
								AppArmeniaManager.getInstance().applicationsData.get(type).add(categoryItemData);
							}
						}
						
						ApplicationsActivity.this.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								getActionBar().getTabAt(2).select();
							}
						});
					}
					
					@Override
					public void onError(String response, Exception e) {
						
					}
				});
			}
		});
		
		itemsAdapter = new ItemsAdapter(this, R.layout.item_applications_list, items);
		itemsList = (ListView) findViewById(R.id.items_list);
		itemsList.setAdapter(itemsAdapter);
		itemsList.setVisibility(View.GONE);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		ActionBar actionBar = getActionBar();
		
		if (actionBar.getTabCount() == 0) {
			actionBar.addTab(actionBar.newTab().setText(R.string.tab_applications_categories).setTabListener(this));
			actionBar.addTab(actionBar.newTab().setText(R.string.tab_applications_top).setTabListener(this));
			actionBar.addTab(actionBar.newTab().setText(R.string.tab_applications_trending).setTabListener(this), true);
			actionBar.addTab(actionBar.newTab().setText(R.string.tab_applications_new).setTabListener(this));
		}
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setLogo(Utils.getIconIdByMenuPosition(position));
		setTitle(Constants.MENU_ITEMS[position]);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActionBar().removeAllTabs();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		case R.id.action_settings:
			Intent appCategoriesActivity = new Intent(ApplicationsActivity.this, SettingsActivity.class);
			appCategoriesActivity.putExtra(HomeActivity.POSITION, Constants.SETTINGS_POSITION);
			startActivity(appCategoriesActivity);
			break;
		case R.id.action_search:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals(getResources().getString(R.string.tab_applications_categories))) {
			itemsList.setVisibility(View.GONE);
			categoriesList.setVisibility(View.VISIBLE);
//			categoriesAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(getResources().getString(R.string.tab_applications_new))) {
			categoriesList.setVisibility(View.GONE);
			itemsList.setVisibility(View.VISIBLE);
			itemsAdapter.clear();
			getItemsListByType(Constants.TYPE_NEW);
			itemsAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(getResources().getString(R.string.tab_applications_top))) {
			categoriesList.setVisibility(View.GONE);
			itemsList.setVisibility(View.VISIBLE);
			itemsAdapter.clear();
			getItemsListByType(Constants.TYPE_TOP);
			itemsAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(getResources().getString(R.string.tab_applications_trending))) {
			categoriesList.setVisibility(View.GONE);
			itemsList.setVisibility(View.VISIBLE);
			itemsAdapter.clear();
			getItemsListByType(Constants.TYPE_TRENDING);
			itemsAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}
	
	private void getItemsListByType(String type) {
		ArrayList<AppCategoryItemData> allItems = AppArmeniaManager.getInstance().applicationsData.get(type);
		for (int i = 0; i < allItems.size(); i++) {
			items.add(allItems.get(i));
		}
	}
}

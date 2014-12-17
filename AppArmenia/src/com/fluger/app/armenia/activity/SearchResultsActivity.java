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
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.backend.API;
import com.fluger.app.armenia.backend.API.RequestObserver;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.ItemsAdapter;

public class SearchResultsActivity extends Activity implements ActionBar.TabListener {
	
	private ListView itemsList;
	private ItemsAdapter itemsAdapter;
	private ArrayList<Integer> tabs = new ArrayList<Integer>();
	private SparseArray<ArrayList<AppCategoryItemData>> items = new SparseArray<ArrayList<AppCategoryItemData>>();
	private ArrayList<AppCategoryItemData> itemsToShow = new ArrayList<AppCategoryItemData>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("heghine", "onCreate");
		setContentView(R.layout.activity_search_results);
		
		itemsAdapter = new ItemsAdapter(this, R.layout.item_applications_list, itemsToShow);
		itemsList = (ListView) findViewById(R.id.items_list);
		itemsList.setAdapter(itemsAdapter);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		handleIntent(getIntent());
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
		menu.findItem(R.id.action_settings).setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			getActionBar().removeAllTabs();	
			onBackPressed();
			return true;
		case R.id.action_search:
			return true;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			final int selectedCategory = intent.getIntExtra("category", 0);
			
			getActionBar().setTitle(query);
			
			items.clear();
			tabs.clear();
			
			API.search(5, 0, query, new RequestObserver() {
				
				@Override
				public void onSuccess(JSONObject response) throws JSONException {
					JSONArray resultJsonArray = response.getJSONArray("values");
					for (int i = 0; i < resultJsonArray.length(); i++) {
						int category = resultJsonArray.getJSONObject(i).optInt("category", 0);
						JSONArray itemsJsonArray = resultJsonArray.getJSONObject(i).getJSONArray("items");
						if (itemsJsonArray.length() > 0) {
							tabs.add(category);
							for (int j = 0; j < itemsJsonArray.length(); j++) {
								AppCategoryItemData itemData = new AppCategoryItemData(itemsJsonArray.getJSONObject(j));
								itemData.category = category;
								if (items.get(category) != null) {
									items.get(category).add(itemData);
								} else {
									items.put(category, new ArrayList<AppCategoryItemData>());
									items.get(category).add(itemData);
								}
							}
						}
					}
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if (!tabs.isEmpty()) {
								if (getActionBar().getTabCount() == 0) {
									for (int i = 0; i < tabs.size(); i++) {
										if (selectedCategory - 1 == i) {
											getActionBar().addTab(getActionBar().newTab().setText(Constants.CATEGORIES[tabs.get(i) - 1]).setTabListener(SearchResultsActivity.this), true);
										} else {
											getActionBar().addTab(getActionBar().newTab().setText(Constants.CATEGORIES[tabs.get(i) - 1]).setTabListener(SearchResultsActivity.this));
										}
									}
								}
								itemsAdapter.clear();
								getItemsListByType(tabs.get(0));
								itemsAdapter.notifyDataSetChanged();
							}
						}
					});
				}
				
				@Override
				public void onError(String response, Exception e) {
					
				}
			});
		}

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals(Constants.CATEGORIES[0])) {
			itemsAdapter.clear();
			getItemsListByType(1);
			itemsAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(Constants.CATEGORIES[1])) {
			itemsAdapter.clear();
			getItemsListByType(2);
			itemsAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(Constants.CATEGORIES[2])) {
			itemsAdapter.clear();
			getItemsListByType(3);
			itemsAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(Constants.CATEGORIES[3])) {
			itemsAdapter.clear();
			getItemsListByType(4);
			itemsAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		
	}
	
	private void getItemsListByType(int type) {
		for (int i = 0; i < items.get(type).size(); i++) {
			itemsToShow.add(items.get(type).get(i));
		}
	}

}

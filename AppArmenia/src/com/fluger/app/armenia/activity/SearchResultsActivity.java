package com.fluger.app.armenia.activity;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.backend.API;
import com.fluger.app.armenia.backend.API.RequestObserver;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.util.ItemsAdapter;

public class SearchResultsActivity extends Activity {
	
	private ListView itemsList;
	private ItemsAdapter itemsAdapter;
	private ArrayList<AppCategoryItemData> items = new ArrayList<AppCategoryItemData>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_results);
		
		itemsAdapter = new ItemsAdapter(this, R.layout.item_applications_list, items);
		itemsList = (ListView) findViewById(R.id.items_list);
		itemsList.setAdapter(itemsAdapter);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);

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
			itemsAdapter.clear();
			API.search(5, 0, query, new RequestObserver() {
				
				@Override
				public void onSuccess(JSONObject response) throws JSONException {
					JSONArray resultJsonArray = response.getJSONArray("values");
					for (int i = 0; i < resultJsonArray.length(); i++) {
						int category = resultJsonArray.getJSONObject(i).optInt("category", 0);
						JSONArray itemsJsonArray = resultJsonArray.getJSONObject(i).getJSONArray("items");
						for (int j = 0; j < itemsJsonArray.length(); j++) {
							AppCategoryItemData itemData = new AppCategoryItemData(itemsJsonArray.getJSONObject(j));
							itemData.category = category;
							items.add(itemData);
						}
					}
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							itemsAdapter.notifyDataSetChanged();
						}
					});
				}
				
				@Override
				public void onError(String response, Exception e) {
					
				}
			});
		}

	}

}

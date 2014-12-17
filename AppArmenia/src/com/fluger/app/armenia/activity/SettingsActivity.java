package com.fluger.app.armenia.activity;

import java.util.ArrayList;

import com.fluger.app.armenia.R;
import com.fluger.app.armenia.data.TagItemData;
import com.fluger.app.armenia.util.CategoriesAdapter;
import com.fluger.app.armenia.util.Constants;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;

public class SettingsActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		ArrayList<TagItemData> settings = new ArrayList<TagItemData>();
		for (int i = 0; i < Constants.MENU_SETTINGS.length; i++) {
			TagItemData item = new TagItemData();
			item.tag = Constants.MENU_SETTINGS[i];
			settings.add(item);
		}
		
		CategoriesAdapter categoriesAdapter = new CategoriesAdapter(this, R.layout.item_category_list, settings);
		ListView categoriesList = (ListView) findViewById(R.id.categories_list);
		categoriesList.setAdapter(categoriesAdapter);
		categoriesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				performMenuSettingsAction(position);
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		setTitle(Constants.SETTINGS);
		getActionBar().setLogo(R.drawable.ic_settings);
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
		}
		return super.onOptionsItemSelected(item);
	}

	private void performMenuSettingsAction(int position) {
		switch (position) {
		case Constants.CLEAR_CACHE_MENU_SETTINGS_POSITION:

			break;
		case Constants.SET_CACHE_SIZE_MENU_SETTINGS_POSITION:

			break;
		case Constants.NEWS_UPDATE_TIME_MENU_SETTINGS_POSITION:

			break;
		case Constants.RESTORE_DEFAULTS_MENU_SETTINGS_POSITION:

			break;
		case Constants.CLEAR_SEARCH_MENU_SETTINGS_POSITION:

			break;

		default:
			break;
		}
	}
}

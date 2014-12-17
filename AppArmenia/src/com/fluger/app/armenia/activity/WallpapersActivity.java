package com.fluger.app.armenia.activity;

import java.util.ArrayList;
import java.util.List;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import com.fluger.app.armenia.HomeActivity;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.activity.details.WallpaperDetailsActivity;
import com.fluger.app.armenia.backend.API;
import com.fluger.app.armenia.backend.API.RequestObserver;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.data.TagItemData;
import com.fluger.app.armenia.manager.AppArmeniaManager;
import com.fluger.app.armenia.util.CategoriesAdapter;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class WallpapersActivity extends Activity implements ActionBar.TabListener {
	private int position;
	private ListView categoriesList;
	private ListView itemsList;
	private CategoriesAdapter categoriesAdapter;
	private WallpapersAdapter itemsAdapter;
	private ArrayList<AppCategoryItemData> items = new ArrayList<AppCategoryItemData>();
	private ArrayList<AppCategoryItemData> itemsToShow = new ArrayList<AppCategoryItemData>();
	private ArrayList<TagItemData> categories = new ArrayList<TagItemData>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wallpapers);
		position = getIntent().getIntExtra("position", 0);

		getItemsListByType(Constants.TYPE_TRENDING);

		API.getTagsList(Constants.WALLPAPERS_CATEGORY_POSITION, new RequestObserver() {

			@Override
			public void onSuccess(JSONObject response) throws JSONException {
				JSONArray tagsJson = response.getJSONArray("values");
				for (int i = 0; i < tagsJson.length(); i++) {
					categories.add(new TagItemData(tagsJson.getJSONObject(i)));
				}

				WallpapersActivity.this.runOnUiThread(new Runnable() {

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
				API.getWallpapersSearchList(5, 0, categories.get(position).tag, new RequestObserver() {

					@Override
					public void onSuccess(JSONObject response) throws JSONException {
						AppArmeniaManager.getInstance().resetWallpapersData();
						JSONArray result = response.getJSONArray("values");
						for (int i = 0; i < result.length(); i++) {
							JSONObject categoryJson = result.getJSONObject(i);
							String type = categoryJson.optString("type", "");
							JSONArray categoryItemsJson = categoryJson.getJSONArray("items");
							for (int j = 0; j < categoryItemsJson.length(); j++) {
								AppCategoryItemData categoryItemData = new AppCategoryItemData(categoryItemsJson.getJSONObject(j));
								categoryItemData.type = type;
								categoryItemData.category = Constants.WALLPAPERS_CATEGORY_POSITION;
								AppArmeniaManager.getInstance().wallpapersData.get(type).add(categoryItemData);
							}
						}

						WallpapersActivity.this.runOnUiThread(new Runnable() {

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

//		itemsAdapter = new ItemsAdapter(this, R.layout.item_applications_list, items);
		itemsAdapter = new WallpapersAdapter(this, R.layout.item_wallpapers_list, itemsToShow);
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

		getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		setTitle(Constants.MENU_ITEMS[position]);
		getActionBar().setLogo(Utils.getIconIdByMenuPosition(position));
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
			Intent appCategoriesActivity = new Intent(WallpapersActivity.this, SettingsActivity.class);
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
			categoriesList.setVisibility(View.VISIBLE);
			itemsList.setVisibility(View.GONE);
			categoriesAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(getResources().getString(R.string.tab_applications_new))) {
			categoriesList.setVisibility(View.GONE);
			itemsList.setVisibility(View.VISIBLE);
			items.clear();
			itemsAdapter.clear();
			getItemsListByType(Constants.TYPE_NEW);
			itemsAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(getResources().getString(R.string.tab_applications_top))) {
			categoriesList.setVisibility(View.GONE);
			itemsList.setVisibility(View.VISIBLE);
			items.clear();
			itemsAdapter.clear();
			getItemsListByType(Constants.TYPE_TOP);
			itemsAdapter.notifyDataSetChanged();
		} else if (tab.getText().equals(getResources().getString(R.string.tab_applications_trending))) {
			categoriesList.setVisibility(View.GONE);
			itemsList.setVisibility(View.VISIBLE);
			items.clear();
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
		ArrayList<AppCategoryItemData> children = AppArmeniaManager.getInstance().wallpapersData.get(type);
		for (int i = 0; i < children.size(); i++) {
			items.add(children.get(i));
			if (i % 3 == 0)
				itemsToShow.add(children.get(i));
		}
	}

	public class WallpapersAdapter extends ArrayAdapter<AppCategoryItemData> {
		private Context context;
		
		public WallpapersAdapter(Context context, int resource, List<AppCategoryItemData> objects) {
			super(context, resource, objects);
			this.context = context;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final AppCategoryItemData child = (AppCategoryItemData) getItem(position);
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_wallpapers_list, parent, false);
			}
			
			ImageView imageView1 = (ImageView) convertView.findViewById(R.id.first);
			String url1 = Constants.FILES_URL + child.imageUrl;
			ImageSize targetSize1 = new ImageSize(270, 270);
			ImageLoader.getInstance().loadImage(url1, targetSize1, null);
			ImageLoader.getInstance().displayImage(url1, imageView1, AppArmeniaManager.getInstance().options);
			imageView1.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					AppArmeniaManager.getInstance().itemDataToBePassed = child;
					Intent wallpapersDetailsActivity = new Intent(WallpapersActivity.this, WallpaperDetailsActivity.class);
					wallpapersDetailsActivity.putExtra(HomeActivity.POSITION, WallpapersActivity.this.position);
					startActivity(wallpapersDetailsActivity);
				}
			});
			
			ImageView imageView2 = (ImageView) convertView.findViewById(R.id.second);
			if (3 * position + 1 < items.size()) {
				imageView2.setVisibility(View.VISIBLE);
				String url2 = Constants.FILES_URL + items.get(3 * position + 1).imageUrl;
				ImageSize targetSize2 = new ImageSize(135, 135);
				ImageLoader.getInstance().loadImage(url2, targetSize2, null);
				ImageLoader.getInstance().displayImage(url2, imageView2, AppArmeniaManager.getInstance().options);
				imageView2.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AppArmeniaManager.getInstance().itemDataToBePassed = items.get(3 * position + 1);
						Intent wallpapersDetailsActivity = new Intent(WallpapersActivity.this, WallpaperDetailsActivity.class);
						wallpapersDetailsActivity.putExtra(HomeActivity.POSITION, WallpapersActivity.this.position);
						startActivity(wallpapersDetailsActivity);
					}
				});
			} else {
				imageView2.setVisibility(View.GONE);
			}
			
			ImageView imageView3 = (ImageView) convertView.findViewById(R.id.third);
			if (3 * position + 2 < items.size()) {
				imageView3.setVisibility(View.VISIBLE);
				String url3 = Constants.FILES_URL + items.get(3 * position + 2).imageUrl;
				ImageSize targetSize3 = new ImageSize(135, 135);
				ImageLoader.getInstance().loadImage(url3, targetSize3, null);
				ImageLoader.getInstance().displayImage(url3, imageView3, AppArmeniaManager.getInstance().options);
				imageView3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						AppArmeniaManager.getInstance().itemDataToBePassed = items.get(3 * position + 2);
						Intent wallpapersDetailsActivity = new Intent(WallpapersActivity.this, WallpaperDetailsActivity.class);
						wallpapersDetailsActivity.putExtra(HomeActivity.POSITION, WallpapersActivity.this.position);
						startActivity(wallpapersDetailsActivity);
					}
				});
			} else {
				imageView3.setVisibility(View.GONE);
			}
			
			return convertView;
		}
		
	}
}

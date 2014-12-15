package com.fluger.app.armenia.activity.details;

import java.util.ArrayList;
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
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.fluger.app.armenia.HomeActivity;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.activity.SettingsActivity;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.manager.AppArmeniaManager;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.DetailsAdapter;
import com.fluger.app.armenia.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class WallpaperDetailsActivity extends Activity implements ActionBar.TabListener {

	private int position;
	private AppCategoryItemData itemData;
	private RelativeLayout detailsView;
	private RelativeLayout previewView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_details_wallpaper);

		position = getIntent().getIntExtra(HomeActivity.POSITION, 0);

		itemData = AppArmeniaManager.getInstance().itemDataToBePassed;
		
		final String url = Constants.FILES_URL + itemData.imageUrl;
		final String fileName = url.substring(url.lastIndexOf("/"));
		
		ImageLoader.getInstance().loadImage(url, null);
		ImageLoader.getInstance().displayImage(url, ((ImageView) findViewById(R.id.wallpaper_bg_img)), AppArmeniaManager.getInstance().options);

		detailsView = ((RelativeLayout) findViewById(R.id.details_container));
		previewView = ((RelativeLayout) findViewById(R.id.preview_container));

		ArrayList<String> details = new ArrayList<String>();
		details.add(itemData.title);
		details.add(itemData.tagsStr);
		details.add("Downloads: " + itemData.downloadCount);
		details.add("User: ");
		details.add("Rating");

		DetailsAdapter categoriesAdapter = new DetailsAdapter(this, R.layout.item_details_list, details);
		ListView detailsListView = ((ListView) findViewById(R.id.details_list));
		detailsListView.setAdapter(categoriesAdapter);

//		if (!Utils.fileExists(fileName, Constants.WALLPAPERS_CATEGORY_POSITION)) {
//			((TextView) findViewById(R.id.set_as_wallpaper_txt)).setText(R.string.download);
//		} else {
//			((TextView) findViewById(R.id.set_as_wallpaper_txt)).setText(R.string.set_as_wallpaper);
//		}
		((TextView) findViewById(R.id.set_as_wallpaper_txt)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (!Utils.fileExists(fileName, Constants.NOTIFICATIONS_CATEGORY_POSITION)) {
//					(new FileDownloaderTask(Constants.NOTIFICATIONS_CATEGORY_POSITION, new OnFileDownloadListener() {
//
//						@Override
//						public void onDownloadSuccess() {
//							((TextView) findViewById(R.id.set_as_notification_txt)).setText(R.string.set_as_notification);
//						}
//
//						@Override
//						public void onDownloadFailure() {
//
//						}
//					})).execute(url);
//				} else {
					setAsWallpaper(fileName);
					Toast.makeText(WallpaperDetailsActivity.this, R.string.done, Toast.LENGTH_LONG).show();
//				}
			}
		});

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();

		ActionBar actionBar = getActionBar();

		if (actionBar.getTabCount() == 0) {
			actionBar.addTab(actionBar.newTab().setText(R.string.tab_notifications_preview).setTabListener(this));
			actionBar.addTab(actionBar.newTab().setText(R.string.tab_notifications_details).setTabListener(this));
		}
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		setTitle(itemData.title);
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

		menu.findItem(R.id.action_share).setVisible(true);

		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (AppArmeniaManager.getInstance().mediaPlayer != null) {
				AppArmeniaManager.getInstance().mediaPlayer.reset();
			}
			onBackPressed();
			return true;
		case R.id.action_settings:
			Intent appCategoriesActivity = new Intent(WallpaperDetailsActivity.this, SettingsActivity.class);
			appCategoriesActivity.putExtra(HomeActivity.POSITION, Constants.SETTINGS_POSITION);
			startActivity(appCategoriesActivity);
			break;
		case R.id.action_search:
			return true;
		case R.id.action_share:
			Toast.makeText(this, "Share!", Toast.LENGTH_SHORT).show();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getText().equals(getResources().getString(R.string.tab_notifications_preview))) {
			detailsView.setVisibility(View.GONE);
			previewView.setVisibility(View.VISIBLE);
		} else if (tab.getText().equals(getResources().getString(R.string.tab_notifications_details))) {
			detailsView.setVisibility(View.VISIBLE);
			previewView.setVisibility(View.GONE);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	private void setAsWallpaper(String fileName) {
		
	}
}

package com.fluger.app.armenia.activity;

import com.fluger.app.armenia.HomeActivity;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.manager.AppArmeniaManager;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SearchView;

public class BeelineActivity extends Activity {
	private int position;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_beeline);
		
		position = getIntent().getIntExtra("position", 0);
		ImageView imgView = ((ImageView) findViewById(R.id.beeline_img_container));
		ImageSize targetSize = new ImageSize(256, 256);
		ImageLoader.getInstance().loadImage(Constants.BEELINE_IMG_URL, targetSize, null);
		ImageLoader.getInstance().displayImage(Constants.BEELINE_IMG_URL, imgView, AppArmeniaManager.getInstance().options);
		
		((WebView) findViewById(R.id.beeline_txt_container)).loadData(getString(R.string.beeline_txt), "text/html", "utf-8");
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		setTitle(Constants.MENU_ITEMS[position]);
		getActionBar().setLogo(Utils.getIconIdByMenuPosition(position));
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFD345")));
	}
	
	@Override
	public void onDestroy() {
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E23239")));
		super.onDestroy();
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
			Intent appCategoriesActivity = new Intent(BeelineActivity.this, SettingsActivity.class);
			appCategoriesActivity.putExtra(HomeActivity.POSITION, Constants.SETTINGS_POSITION);
			startActivity(appCategoriesActivity);
			break;
		case R.id.action_search:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

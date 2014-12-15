package com.fluger.app.armenia;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import com.fluger.app.armenia.activity.ApplicationsActivity;
import com.fluger.app.armenia.activity.BeelineActivity;
import com.fluger.app.armenia.activity.NewsActivity;
import com.fluger.app.armenia.activity.NotificationsActivity;
import com.fluger.app.armenia.activity.ReviewsActivity;
import com.fluger.app.armenia.activity.RingtonesActivity;
import com.fluger.app.armenia.activity.SettingsActivity;
import com.fluger.app.armenia.activity.WallpapersActivity;
import com.fluger.app.armenia.fragment.AppCategoriesFragment;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.Utils;

public class HomeActivity extends Activity {
	public static final String POSITION = "position";
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence currentPageTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		currentPageTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerList.setAdapter(new DrawerMenuArrayAdapter(this, R.layout.item_drawer_list, Constants.MENU_ITEMS));
		mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItemWithActivity(position);
			}

		});

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(currentPageTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(currentPageTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
//			selectItem(0);
			Fragment fragment = new AppCategoriesFragment();
			FragmentManager fragmentManager = getFragmentManager();
			
			Bundle args = new Bundle();
			args.putInt(AppCategoriesFragment.ARG_POSITION, 0);
			fragment.setArguments(args);
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();
			mDrawerList.setItemChecked(0, true);
//			mDrawerLayout.closeDrawer(mDrawerList);
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		mDrawerToggle.setDrawerIndicatorEnabled(true);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case R.id.action_settings:
			selectItemWithActivity(Constants.SETTINGS_POSITION);
			break;
		case R.id.action_search:
			return true;
			
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		currentPageTitle = title;
		getActionBar().setTitle(currentPageTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public ActionBarDrawerToggle getmDrawerToggle() {
		return mDrawerToggle;
	}
	
	public void selectItemWithActivity(int position) {
		Intent appCategoriesActivity = null;
		switch (position) {
		case Constants.HOME_MENU_POSITION:
			return;
		case Constants.APPLICATIONS_MENU_POSITION:
			appCategoriesActivity = new Intent(HomeActivity.this, ApplicationsActivity.class);
			break;
		case Constants.NEWS_MENU_POSITION:
			appCategoriesActivity = new Intent(HomeActivity.this, NewsActivity.class);
			break;
		case Constants.REVIEWS_MENU_POSITION:
			appCategoriesActivity = new Intent(HomeActivity.this, ReviewsActivity.class);
			break;
		case Constants.WALLPAPERS_MENU_POSITION:
			appCategoriesActivity = new Intent(HomeActivity.this, WallpapersActivity.class);
			break;
		case Constants.RINGTONES_MENU_POSITION:
			appCategoriesActivity = new Intent(HomeActivity.this, RingtonesActivity.class);
			break;
		case Constants.NOTIFICATIONS_MENU_POSITION:
			appCategoriesActivity = new Intent(HomeActivity.this, NotificationsActivity.class);
			break;
		case Constants.BEELINE_MENU_POSITION:
			appCategoriesActivity = new Intent(HomeActivity.this, BeelineActivity.class);
			break;
		case Constants.SETTINGS_POSITION:
			appCategoriesActivity = new Intent(HomeActivity.this, SettingsActivity.class);
			break;
		default:
			break;
		}
		appCategoriesActivity.putExtra(POSITION, position);
		startActivity(appCategoriesActivity);
		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	public class DrawerMenuArrayAdapter extends ArrayAdapter<String> {
		private Context context;

		public DrawerMenuArrayAdapter(Context context, int resource, String[] menuItems) {
			super(context, resource, menuItems);
			this.context = context;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_drawer_list, parent, false);
			}
			((TextView) convertView).setText(Constants.MENU_ITEMS[position]);
			((TextView) convertView).setCompoundDrawablesWithIntrinsicBounds(Utils.getDrawerIconIdByMenuPosition(position), 0, 0, 0);

			return convertView;
		}
	}
}

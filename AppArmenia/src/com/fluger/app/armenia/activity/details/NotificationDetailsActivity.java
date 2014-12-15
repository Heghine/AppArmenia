package com.fluger.app.armenia.activity.details;

import java.io.File;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
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
import com.fluger.app.armenia.util.FileDownloaderTask;
import com.fluger.app.armenia.util.OnFileDownloadListener;
import com.fluger.app.armenia.util.OnMediaPlayerCompleteListener;
import com.fluger.app.armenia.util.Utils;

public class NotificationDetailsActivity extends Activity implements ActionBar.TabListener {

	private int position;
	private AppCategoryItemData itemData;
	private RelativeLayout detailsView;
	private RelativeLayout previewView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_details_notification);

		position = getIntent().getIntExtra(HomeActivity.POSITION, 0);

		itemData = AppArmeniaManager.getInstance().itemDataToBePassed;
		final String url = Constants.FILES_URL + itemData.audioUrl;
		final String fileName = url.substring(url.lastIndexOf("/"));
		Log.d("heghine", "url = " + url);
		AppArmeniaManager.getInstance().initMediaPlayer(url, new OnMediaPlayerCompleteListener() {

			@Override
			public void onComplete() {
				((ImageButton) findViewById(R.id.play_btn)).setVisibility(View.VISIBLE);
				((ImageButton) findViewById(R.id.pause_btn)).setVisibility(View.GONE);
			}
		});

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

		if (!Utils.fileExists(fileName, Constants.NOTIFICATIONS_CATEGORY_POSITION)) {
			((TextView) findViewById(R.id.set_as_notification_txt)).setText(R.string.download);
		} else {
			((TextView) findViewById(R.id.set_as_notification_txt)).setText(R.string.set_as_notification);
		}
		((TextView) findViewById(R.id.set_as_notification_txt)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!Utils.fileExists(fileName, Constants.NOTIFICATIONS_CATEGORY_POSITION)) {
					(new FileDownloaderTask(Constants.NOTIFICATIONS_CATEGORY_POSITION, new OnFileDownloadListener() {

						@Override
						public void onDownloadSuccess() {
							((TextView) findViewById(R.id.set_as_notification_txt)).setText(R.string.set_as_notification);
						}

						@Override
						public void onDownloadFailure() {

						}
					})).execute(url);
				} else {
					setAsNotification(fileName);
					Toast.makeText(NotificationDetailsActivity.this, R.string.done, Toast.LENGTH_LONG).show();
				}
			}
		});

		((ImageButton) findViewById(R.id.play_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ImageButton) findViewById(R.id.play_btn)).setVisibility(View.GONE);
				((ImageButton) findViewById(R.id.pause_btn)).setVisibility(View.VISIBLE);
				AppArmeniaManager.getInstance().mediaPlayer.start();
			}
		});

		((ImageButton) findViewById(R.id.pause_btn)).setVisibility(View.GONE);
		((ImageButton) findViewById(R.id.pause_btn)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ImageButton) findViewById(R.id.play_btn)).setVisibility(View.VISIBLE);
				((ImageButton) findViewById(R.id.pause_btn)).setVisibility(View.GONE);
				AppArmeniaManager.getInstance().mediaPlayer.pause();
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
			Intent appCategoriesActivity = new Intent(NotificationDetailsActivity.this, SettingsActivity.class);
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

	private void setAsNotification(String fileName) {
		File root = Environment.getExternalStorageDirectory();
		Uri notificationFile = Uri.parse(root.getAbsolutePath() + Constants.NOTIFICATIONS_CACHE_URL + fileName);
		File chosenFile = new File(notificationFile.getPath());
		Log.d("heghine", "setting as notification: path = " + chosenFile.getAbsolutePath());
		
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, chosenFile.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, chosenFile.getName());
		values.put(MediaStore.MediaColumns.SIZE, chosenFile.length());
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		values.put(AudioColumns.ARTIST, this.getString(R.string.app_name));
		values.put(AudioColumns.IS_RINGTONE, false);
		values.put(AudioColumns.IS_NOTIFICATION, true);
		values.put(AudioColumns.IS_ALARM, false);
		values.put(AudioColumns.IS_MUSIC, false);

		Uri uri = MediaStore.Audio.Media.getContentUriForPath(chosenFile.getAbsolutePath());
		String where = MediaStore.MediaColumns.DATA + "=\"" + chosenFile.getAbsolutePath() + "\"";
		Log.d("heghine", "setting as ringtone: where = " + where);
		this.getContentResolver().delete(uri, where, null);


		Uri newUri = this.getContentResolver().insert(uri, values);
		Log.d("heghine", "setting as ringtone: newUri = " + newUri.getPath());
		RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION, newUri);
	}
}

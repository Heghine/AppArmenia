/*
 * This is test class and is not used
 */

package com.fluger.app.armenia.activity.details;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.fluger.app.armenia.HomeActivity;
import com.fluger.app.armenia.R;
import com.fluger.app.armenia.activity.SettingsActivity;
import com.fluger.app.armenia.backend.API;
import com.fluger.app.armenia.backend.API.RequestObserver;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.manager.AppArmeniaManager;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.FileDownloaderTask;
import com.fluger.app.armenia.util.ImageAdapter;
import com.fluger.app.armenia.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

@SuppressWarnings("deprecation")
public class ApplicationDetailsActivity extends Activity {

	private int position;
	private String[] screenshots;
	private AppCategoryItemData itemData;
	private int mDotsCount;
	private LinearLayout mDotsLayout;
	private boolean isReadMoreClicked;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details_application);
		position = getIntent().getIntExtra(HomeActivity.POSITION, 0);

		itemData = AppArmeniaManager.getInstance().itemDataToBePassed;

		SimpleDateFormat newformat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
		SimpleDateFormat oldformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
		String reformattedStr = null;
		try {
			reformattedStr = newformat.format(oldformat.parse(itemData.uploaded));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		((TextView) findViewById(R.id.update_date_txt)).setText(reformattedStr);
		if (!itemData.isFree) {
			((TextView) findViewById(R.id.free_txt)).setText("$5");
		}
		
		String url = Constants.FILES_URL + itemData.imageUrl;
		ImageSize targetSize = new ImageSize(80, 80);
		ImageLoader.getInstance().loadImage(url, targetSize, null);
		ImageLoader.getInstance().displayImage(url, (ImageView) findViewById(R.id.applications_item_img), AppArmeniaManager.getInstance().options);
		
		((TextView) findViewById(R.id.applications_item_name)).setText(itemData.title);
		((RatingBar) findViewById(R.id.applications_item_rating)).setRating(itemData.rating);
		((TextView) findViewById(R.id.applications_item_download)).setText(getString(R.string.download) + " " + Integer.toString(itemData.downloadCount));
		
		itemData.description = itemData.description.replaceAll("<br />", "\n");
		final String shortDescription = itemData.description.substring(0, itemData.description.length()/2) + " ...";
		((TextView) findViewById(R.id.applications_item_description_txt)).setText(shortDescription);
		
		((ImageButton) findViewById(R.id.read_more_btn)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isReadMoreClicked) {
					isReadMoreClicked = true;
					((ImageButton) findViewById(R.id.read_more_btn)).setBackgroundResource(R.drawable.ic_arrow_up_mini);
					((TextView) findViewById(R.id.read_more_txt)).setText(R.string.less);
					((TextView) findViewById(R.id.applications_item_description_txt)).setText(itemData.description);
				} else {
					isReadMoreClicked = false;
					((ImageButton) findViewById(R.id.read_more_btn)).setBackgroundResource(R.drawable.ic_arrow_down_mini);
					((TextView) findViewById(R.id.read_more_txt)).setText(R.string.more);
					((TextView) findViewById(R.id.applications_item_description_txt)).setText(shortDescription);
				}
			}
		});
		
		
		((TextView) findViewById(R.id.install_txt)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemData.androidUrl));
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(ApplicationDetailsActivity.this, "Play Store is missing!", Toast.LENGTH_LONG);
				}
			}
		});

		API.getAppsScreenshots(itemData.id, new RequestObserver() {

			@Override
			public void onSuccess(JSONObject response) throws JSONException {
				JSONArray screenShotsUrlJson = response.getJSONArray("values");
				screenshots = new String[screenShotsUrlJson.length()];
				for (int i = 0; i < screenShotsUrlJson.length(); i++) {
					final String url = screenShotsUrlJson.getJSONObject(i).optString("image", "");
					screenshots[i] = itemData.id + url.substring(url.lastIndexOf("/"));
					ApplicationDetailsActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							(new FileDownloaderTask(Constants.APPLICATIONS_CATEGORY_POSITION, null)).execute(Constants.FILES_URL + url);
						}
					});
				}

				ApplicationDetailsActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						initGallery();
					}
				});
			}

			@Override
			public void onError(String response, Exception e) {

			}
		});

		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		setTitle(itemData.title);
		getActionBar().setLogo(Utils.getIconIdByMenuPosition(position));
	}

	private void initGallery() {
		final Gallery gallery = (Gallery) findViewById(R.id.applications_screenshots_gallery);
		gallery.setAdapter(new ImageAdapter(this, screenshots));

		mDotsLayout = (LinearLayout) findViewById(R.id.application_screenshots_slider);
		mDotsCount = gallery.getAdapter().getCount();
		final ImageView[] dotImg = new ImageView[mDotsCount];
		for (int i = 0; i < mDotsCount; i++) {
			ImageView iv = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.setMargins(2, 2, 2, 2);
			iv.setLayoutParams(params);
			iv.setBackgroundResource(R.drawable.ic_dot);
			dotImg[i] = iv;
			mDotsLayout.addView(dotImg[i]);
			final int index = i;
			dotImg[i].setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					gallery.setSelection(index);
				}
			});
			
		}

		gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				for (int i = 0; i < mDotsCount; i++) {
	                dotImg[i].setBackgroundResource(R.drawable.ic_dot);
	            }

			   dotImg[position].setBackgroundResource(R.drawable.ic_dot_active);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
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
			Intent appCategoriesActivity = new Intent(ApplicationDetailsActivity.this, SettingsActivity.class);
			appCategoriesActivity.putExtra(HomeActivity.POSITION, Constants.SETTINGS_POSITION);
			startActivity(appCategoriesActivity);
			break;
		case R.id.action_search:
			return true;
			// case R.id.action_share:
			// Toast.makeText(this, "Share!", Toast.LENGTH_SHORT).show();
			// break;
		}
		return super.onOptionsItemSelected(item);
	}
}

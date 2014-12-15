package com.fluger.app.armenia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import com.fluger.app.armenia.backend.API;
import com.fluger.app.armenia.backend.API.RequestObserver;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.manager.AppArmeniaManager;
import com.fluger.app.armenia.util.Constants;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MainActivity extends Activity {
	
	public interface OnDataLoadListener {
		public void onDataLoadComplete();
	}
	
	private OnDataLoadListener onDataLoadListener = new OnDataLoadListener() {
		
		@Override
		public void onDataLoadComplete() {
			if (isTrendingDataLoaded && isApplicationsDataLoaded && isWallpapersDataLoaded && isRingtonesDataLoaded && isNotificationsDataLoaded) {
				AppArmeniaManager.getInstance().isDataLoaded = true;
				MainActivity.this.runOnUiThread(new Runnable() {
	
					@Override
					public void run() {
						startAppCategoriesActivity();
					}
				});
			}
		}
	};
	
	public boolean isTrendingDataLoaded;
	public boolean isApplicationsDataLoaded;
	public boolean isWallpapersDataLoaded;
	public boolean isRingtonesDataLoaded;
	public boolean isNotificationsDataLoaded;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.diskCacheFileNameGenerator(new Md5FileNameGenerator())
			.diskCacheSize(50 * 1024 * 1024) // 50 Mb
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs() // Remove for release app
			.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		
		setContentView(R.layout.activity_main);
		
		AppArmeniaManager.getInstance().initializeCategories();

		if (!AppArmeniaManager.getInstance().isDataLoaded) {
			API.getTrendingItems(0, 15, new RequestObserver() {
				
				@Override
				public void onSuccess(JSONObject response) throws JSONException {
					JSONArray result = response.getJSONArray("values");
					for (int i = 0; i < result.length(); i++) {
						JSONObject categoryJson = result.getJSONObject(i);
						int category = categoryJson.optInt("category", 0);
						JSONArray categoryItemsJson = categoryJson.getJSONArray("items");
						for (int j = 0; j < categoryItemsJson.length(); j++) {
							AppCategoryItemData categoryItemData = new AppCategoryItemData(categoryItemsJson.getJSONObject(j));
							categoryItemData.category = category;
							AppArmeniaManager.getInstance().categoriesTrendingData.get(category - 1).children.add(categoryItemData);
						}
					}
					 
					isTrendingDataLoaded = true;
					onDataLoadListener.onDataLoadComplete();
				}
				
				@Override
				public void onError(String response, Exception e) {
					
				}
			});
			API.getAppsList(5, 0, new RequestObserver() {

				@Override
				public void onSuccess(JSONObject response) throws JSONException {
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
					 
					isApplicationsDataLoaded = true;
					onDataLoadListener.onDataLoadComplete();
				}

				@Override
				public void onError(String response, Exception e) {

				}
			});
			
			API.getWallpapersList(5, 0, new RequestObserver() {

				@Override
				public void onSuccess(JSONObject response) throws JSONException {
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
					isWallpapersDataLoaded = true;
					onDataLoadListener.onDataLoadComplete();
				}

				@Override
				public void onError(String response, Exception e) {

				}
			});
			
			API.getRingtonesList(5, 0, new RequestObserver() {

				@Override
				public void onSuccess(JSONObject response) throws JSONException {
					JSONArray result = response.getJSONArray("values");
					for (int i = 0; i < result.length(); i++) {
						JSONObject categoryJson = result.getJSONObject(i);
						String type = categoryJson.optString("type", "");
						JSONArray categoryItemsJson = categoryJson.getJSONArray("items");
						for (int j = 0; j < categoryItemsJson.length(); j++) {
							AppCategoryItemData categoryItemData = new AppCategoryItemData(categoryItemsJson.getJSONObject(j));
							categoryItemData.type = type;
							categoryItemData.category = Constants.RINGTONES_CATEGORY_POSITION;
							AppArmeniaManager.getInstance().ringtonesData.get(type).add(categoryItemData);
						}
					}
					isRingtonesDataLoaded = true;
					onDataLoadListener.onDataLoadComplete();
				}

				@Override
				public void onError(String response, Exception e) {

				}
			});
			
			API.getNotificationsList(5, 0, new RequestObserver() {

				@Override
				public void onSuccess(JSONObject response) throws JSONException {
					JSONArray result = response.getJSONArray("values");
					for (int i = 0; i < result.length(); i++) {
						JSONObject categoryJson = result.getJSONObject(i);
						String type = categoryJson.optString("type", "");
						JSONArray categoryItemsJson = categoryJson.getJSONArray("items");
						for (int j = 0; j < categoryItemsJson.length(); j++) {
							AppCategoryItemData categoryItemData = new AppCategoryItemData(categoryItemsJson.getJSONObject(j));
							categoryItemData.type = type;
							categoryItemData.category = Constants.NOTIFICATIONS_CATEGORY_POSITION;
							AppArmeniaManager.getInstance().notificationsData.get(type).add(categoryItemData);
						}
					}
					isNotificationsDataLoaded = true;
					onDataLoadListener.onDataLoadComplete();
				}

				@Override
				public void onError(String response, Exception e) {

				}
			});
		} else {
//			for (int i = 0; i < AppArmeniaManager.getInstance().categories.size(); i++) {
//				if (!AppArmeniaManager.getInstance().categories.get(i).children.isEmpty()) {
//					AppArmeniaManager.getInstance().categories.get(i).childrenToShow = AppArmeniaManager.getInstance().categories.get(i).children.subList(0, 3); 
//				}
//			}
			
			startAppCategoriesActivity();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
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
	
	public void startAppCategoriesActivity() {
//		Intent appCategoriesActivity = new Intent(MainActivity.this, AppCategoriesFragment.class);
//		startActivity(appCategoriesActivity);
		Intent appCategoriesActivity = new Intent(MainActivity.this, HomeActivity.class);
		startActivity(appCategoriesActivity);
	}
}

package com.fluger.app.armenia.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.SparseArray;

import com.fluger.app.armenia.R;
import com.fluger.app.armenia.data.AppCategory;
import com.fluger.app.armenia.data.AppCategoryItemData;
import com.fluger.app.armenia.util.Constants;
import com.fluger.app.armenia.util.OnMediaPlayerCompleteListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class AppArmeniaManager {
	private static AppArmeniaManager instance;

	private AppArmeniaManager() {

	}

	public static AppArmeniaManager getInstance() {
		if (instance == null) {
			instance = new AppArmeniaManager();
		}

		return instance;
	}

	public boolean isDataLoaded; // shows if categories data is loaded
	public SparseArray<AppCategory> categoriesTrendingData = new SparseArray<AppCategory>();

	public void initializeCategories() {
		for (int i = 0; i < Constants.CATEGORIES.length; i++) {
			AppCategory category = new AppCategory(Constants.CATEGORIES[i]);
			categoriesTrendingData.put(i, category);
		}
		applicationsData.put(Constants.TYPE_TOP, new ArrayList<AppCategoryItemData>());
		applicationsData.put(Constants.TYPE_NEW, new ArrayList<AppCategoryItemData>());
		applicationsData.put(Constants.TYPE_TRENDING, new ArrayList<AppCategoryItemData>());
		wallpapersData.put(Constants.TYPE_TOP, new ArrayList<AppCategoryItemData>());
		wallpapersData.put(Constants.TYPE_NEW, new ArrayList<AppCategoryItemData>());
		wallpapersData.put(Constants.TYPE_TRENDING, new ArrayList<AppCategoryItemData>());
		ringtonesData.put(Constants.TYPE_TOP, new ArrayList<AppCategoryItemData>());
		ringtonesData.put(Constants.TYPE_NEW, new ArrayList<AppCategoryItemData>());
		ringtonesData.put(Constants.TYPE_TRENDING, new ArrayList<AppCategoryItemData>());
		notificationsData.put(Constants.TYPE_TOP, new ArrayList<AppCategoryItemData>());
		notificationsData.put(Constants.TYPE_NEW, new ArrayList<AppCategoryItemData>());
		notificationsData.put(Constants.TYPE_TRENDING, new ArrayList<AppCategoryItemData>());
	}

	public DisplayImageOptions options = new DisplayImageOptions.Builder()
												.showImageOnFail(R.drawable.ic_ringtone_default)
												.cacheInMemory(true)
												.cacheOnDisk(true)
												.considerExifParams(true)
												.bitmapConfig(Bitmap.Config.RGB_565)
												.build();

	
	public ArrayList<AppCategoryItemData> getItemsByTag(String tag, int category) {
		ArrayList<AppCategoryItemData> allItems = categoriesTrendingData.get(category).children;
		ArrayList<AppCategoryItemData> result = new ArrayList<AppCategoryItemData>();
		for (int i = 0; i < allItems.size(); i++) {
			if (allItems.get(i).tags != null && Arrays.asList(allItems.get(i).tags).contains(tag)) {
				result.add(allItems.get(i));
			}
		}
		
		return result;
	}
	
	public MediaPlayer mediaPlayer;
	
	public void initMediaPlayer(String url, final OnMediaPlayerCompleteListener onCompleteListener) {
		if (mediaPlayer == null) {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		}
		if (onCompleteListener != null) {
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					onCompleteListener.onComplete();
				}
			});
		}
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepareAsync(); 
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public HashMap<String, ArrayList<AppCategoryItemData>> applicationsData = new HashMap<String, ArrayList<AppCategoryItemData>>();
	public HashMap<String, ArrayList<AppCategoryItemData>> wallpapersData = new HashMap<String, ArrayList<AppCategoryItemData>>();
	public HashMap<String, ArrayList<AppCategoryItemData>> ringtonesData = new HashMap<String, ArrayList<AppCategoryItemData>>();
	public HashMap<String, ArrayList<AppCategoryItemData>> notificationsData = new HashMap<String, ArrayList<AppCategoryItemData>>();
	
	public AppCategoryItemData itemDataToBePassed;
	
	public void resetApplicationsData() {
		applicationsData.get(Constants.TYPE_TOP).clear();
		applicationsData.get(Constants.TYPE_NEW).clear();
		applicationsData.get(Constants.TYPE_TRENDING).clear();
	}
	
	public void resetWallpapersData() {
		wallpapersData.get(Constants.TYPE_TOP).clear();
		wallpapersData.get(Constants.TYPE_NEW).clear();
		wallpapersData.get(Constants.TYPE_TRENDING).clear();
	}
	
	public void resetRingtonesData() {
		ringtonesData.get(Constants.TYPE_TOP).clear();
		ringtonesData.get(Constants.TYPE_NEW).clear();
		ringtonesData.get(Constants.TYPE_TRENDING).clear();
	}
	
	public void resetNotificationsData() {
		notificationsData.get(Constants.TYPE_TOP).clear();
		notificationsData.get(Constants.TYPE_NEW).clear();
		notificationsData.get(Constants.TYPE_TRENDING).clear();
	}
} 

package com.fluger.app.armenia.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.fluger.app.armenia.R;

public class Utils {

	public static int getIconIdByIndex(int index) {
		int resourseId = R.drawable.ic_app_armenia;
		switch (index) {
		case Constants.APPLICATIONS_CATEGORY_POSITION:
			resourseId = R.drawable.ic_applications;
			break;
		case Constants.WALLPAPERS_CATEGORY_POSITION:
			resourseId = R.drawable.ic_wallpaper;
			break;
		case Constants.RINGTONES_CATEGORY_POSITION:
			resourseId = R.drawable.ic_ringtones;
			break;
		case Constants.NOTIFICATIONS_CATEGORY_POSITION:
			resourseId = R.drawable.ic_notifications;
			break;
		default:
			break;
		}
		return resourseId;
	}

	public static int getMenuPositionBasedOnCategoryIndex(int index) {
		int menuPosition = 0;
		switch (index) {
		case Constants.APPLICATIONS_CATEGORY_POSITION:
			menuPosition = Constants.APPLICATIONS_MENU_POSITION;
			break;
		case Constants.WALLPAPERS_CATEGORY_POSITION:
			menuPosition = Constants.WALLPAPERS_MENU_POSITION;
			break;
		case Constants.RINGTONES_CATEGORY_POSITION:
			menuPosition = Constants.RINGTONES_MENU_POSITION;
			break;
		case Constants.NOTIFICATIONS_CATEGORY_POSITION:
			menuPosition = Constants.NOTIFICATIONS_MENU_POSITION;
			break;
		default:
			break;
		}
		return menuPosition;
	}

	public static int getColorByIndex(int index) {
		int colorId = R.color.red;
		switch (index) {
		case Constants.APPLICATIONS_CATEGORY_POSITION:
			colorId = R.color.applications_color;
			break;
		case Constants.WALLPAPERS_CATEGORY_POSITION:
			colorId = R.color.wallpapers_color;
			break;
		case Constants.RINGTONES_CATEGORY_POSITION:
			colorId = R.color.ringtons_color;
			break;
		case Constants.NOTIFICATIONS_CATEGORY_POSITION:
			colorId = R.color.notifications_color;
			break;
		default:
			break;
		}
		return colorId;
	}

	public static int getIconIdByMenuPosition(int position) {
		int resourseId = R.drawable.ic_app_armenia;
		switch (position) {
		case Constants.HOME_MENU_POSITION:
			resourseId = R.drawable.ic_app_armenia;
			break;
		case Constants.APPLICATIONS_MENU_POSITION:
			resourseId = R.drawable.ic_applications;
			break;
		case Constants.NEWS_MENU_POSITION:
			resourseId = R.drawable.ic_news;
			break;
		case Constants.REVIEWS_MENU_POSITION:
			resourseId = R.drawable.ic_reviews;
			break;
		case Constants.WALLPAPERS_MENU_POSITION:
			resourseId = R.drawable.ic_wallpaper;
			break;
		case Constants.RINGTONES_MENU_POSITION:
			resourseId = R.drawable.ic_ringtones;
			break;
		case Constants.NOTIFICATIONS_MENU_POSITION:
			resourseId = R.drawable.ic_notifications;
			break;
		case Constants.BEELINE_MENU_POSITION:
			resourseId = R.drawable.ic_beeline;
			break;
		default:
			break;
		}
		return resourseId;
	}

	public static int getDrawerIconIdByMenuPosition(int position) {
		int resourseId = R.drawable.ic_app_armenia;
		switch (position) {
		case Constants.HOME_MENU_POSITION:
			resourseId = R.drawable.ic_drawer_home;
			break;
		case Constants.APPLICATIONS_MENU_POSITION:
			resourseId = R.drawable.ic_drawer_applications;
			break;
		case Constants.NEWS_MENU_POSITION:
			resourseId = R.drawable.ic_drawer_news;
			break;
		case Constants.REVIEWS_MENU_POSITION:
			resourseId = R.drawable.ic_drawer_reviews;
			break;
		case Constants.WALLPAPERS_MENU_POSITION:
			resourseId = R.drawable.ic_drawer_wallpapers;
			break;
		case Constants.RINGTONES_MENU_POSITION:
			resourseId = R.drawable.ic_drawer_ringtones;
			break;
		case Constants.NOTIFICATIONS_MENU_POSITION:
			resourseId = R.drawable.ic_drawer_notifications;
			break;
		case Constants.BEELINE_MENU_POSITION:
			resourseId = R.drawable.ic_drawer_beeline;
			break;
		default:
			break;
		}
		return resourseId;
	}

	// these functions help to load images from server by the right size
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}

		return inSampleSize;
	}

	public static Bitmap decodeSampledBitmapFromResource(InputStream is, String url, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
//		Rect rect = new Rect(0, reqHeight, 0, reqWidth);
		BitmapFactory.decodeStream(is, null, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		
		try {
	    	is.close();
			is = (InputStream) new URL(url).getContent();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeStream(is, null, options);
	}
	
	public static boolean fileExists(String fileName, int category) {
		File root = Environment.getExternalStorageDirectory();
		if (category == Constants.NOTIFICATIONS_CATEGORY_POSITION) {
			File file = new File(root.getAbsolutePath() + Constants.NOTIFICATIONS_CACHE_URL + fileName);
			return file.exists();
		} else if (category == Constants.RINGTONES_CATEGORY_POSITION) {
			File file = new File(root.getAbsolutePath() + Constants.RINGTONES_CACHE_URL + fileName);
			return file.exists();
		}
		
		return false;
	}
}

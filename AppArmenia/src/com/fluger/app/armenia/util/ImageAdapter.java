package com.fluger.app.armenia.util;

import com.fluger.app.armenia.manager.AppArmeniaManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

@SuppressWarnings("deprecation")
public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	private String[] mImageUrls;

	public ImageAdapter(Context context, String[] mImageUrls) {
		this.mContext = context;
		this.mImageUrls = mImageUrls;
	}

	public ImageAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		return mImageUrls.length;
	}

	@Override
	public Object getItem(int i) {
		return i;
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(mContext);

		imageView.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		String url = "file:///mnt/sdcard" + Constants.APPLICATIONS_CACHE_URL + "/" + mImageUrls[position];
		Log.d("heghine", "ImageAdapter: url = " + url);
		ImageSize targetSize = new ImageSize(128, 128);
		ImageLoader.getInstance().loadImage(url, targetSize, null);
		ImageLoader.getInstance().displayImage(url, imageView, AppArmeniaManager.getInstance().options);
		
		return imageView;
	}
}

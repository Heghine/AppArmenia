package com.fluger.app.armenia.util;

import com.fluger.app.armenia.backend.DowloadManager;

import android.os.AsyncTask;

public class FileDownloaderTask extends AsyncTask<String, Void, Boolean> {
	
	private int category;
	private OnFileDownloadListener onDownloadListener;
	
	public FileDownloaderTask(int category, OnFileDownloadListener onDownloadListener) {
		this.category = category;
		this.onDownloadListener = onDownloadListener;
	}
	
	@Override
	protected Boolean doInBackground(String... params) {
		String url = params[0];
		boolean result = DowloadManager.getInstance().downloadFile(url, category);
		return result;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if (onDownloadListener != null) {
			if (result) {
				onDownloadListener.onDownloadSuccess();
			} else {
				onDownloadListener.onDownloadFailure();
			}
		}
	}

}

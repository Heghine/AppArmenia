package com.fluger.app.armenia.backend;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.os.Environment;
import android.util.Log;

import com.fluger.app.armenia.util.Constants;

public class DowloadManager {
	private static final int TIMEOUT_CONNECTION = 500;
	private static final int TIMEOUT_SOCKET = 500;
	private static DowloadManager instance;

	private DowloadManager() {

	}

	public static DowloadManager getInstance() {
		if (instance == null) {
			instance = new DowloadManager();
		}

		return instance;
	}
	
	public boolean downloadFile(String fileUrl, int category) {
		if (category == Constants.NOTIFICATIONS_CATEGORY_POSITION) {
			return downloadNotificationFile(fileUrl);
		} else if (category == Constants.RINGTONES_CATEGORY_POSITION) {
			return downloadRingtoneFile(fileUrl);
		}
		
		return false;
	}

	public boolean downloadNotificationFile(String fileUrl) {
		boolean result = false;
		try {
			Log.d("DownloadManager", "download url:" + fileUrl);

			File root = Environment.getExternalStorageDirectory();
			Log.d("DownloadManager", root.getAbsolutePath() + Constants.NOTIFICATIONS_CACHE_URL);
			File dir = new File(root.getAbsolutePath() + Constants.NOTIFICATIONS_CACHE_URL);
			if (dir.exists() == false) {
				dir.mkdirs();
			}
			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/"));
			URL url = new URL(fileUrl);
			File file = new File(dir, fileName);

			URLConnection uconn = url.openConnection();
			uconn.setReadTimeout(TIMEOUT_CONNECTION);
			uconn.setConnectTimeout(TIMEOUT_SOCKET);

			InputStream is = uconn.getInputStream();
			BufferedInputStream bufferinstream = new BufferedInputStream(is);

			ByteArrayBuffer baf = new ByteArrayBuffer(5000);
			int current = 0;
			while ((current = bufferinstream.read()) != -1) {
				baf.append((byte) current);
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.flush();
			fos.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public boolean downloadRingtoneFile(String fileUrl) {
		boolean result = false;
		try {
			Log.d("DownloadManager", "download url:" + fileUrl);

			File root = Environment.getExternalStorageDirectory();
			Log.d("DownloadManager", root.getAbsolutePath() + Constants.RINGTONES_CACHE_URL);
			File dir = new File(root.getAbsolutePath() + Constants.RINGTONES_CACHE_URL);
			if (dir.exists() == false) {
				dir.mkdirs();
			}
			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/"));
			URL url = new URL(fileUrl);
			File file = new File(dir, fileName);

			URLConnection uconn = url.openConnection();
			uconn.setReadTimeout(TIMEOUT_CONNECTION);
			uconn.setConnectTimeout(TIMEOUT_SOCKET);

			InputStream is = uconn.getInputStream();
			BufferedInputStream bufferinstream = new BufferedInputStream(is);

			ByteArrayBuffer baf = new ByteArrayBuffer(5000);
			int current = 0;
			while ((current = bufferinstream.read()) != -1) {
				baf.append((byte) current);
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(baf.toByteArray());
			fos.flush();
			fos.close();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

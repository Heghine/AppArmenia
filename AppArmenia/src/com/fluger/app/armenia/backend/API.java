package com.fluger.app.armenia.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.fluger.app.armenia.util.Constants;


public class API {
	
	public static String deviceId = "some-device-device-Id/";
	
	public static final String GET_TRENDING_ITEMS = "trending/";
	public static final String GET_APPS_LIST = "applist/";
	public static final String GET_WALLPAPERS_LIST = "wplist/";
	public static final String GET_RINGTONES_LIST = "rtlist/";
	public static final String GET_NOTIFICATIONS_LIST = "ntlist/";
	public static final String SEARCH = "search/";
	public static final String GET_TAGS_LIST = "tagslist/";
	public static final String GET_APPS_SEARCH_LIST = "atsearch/";
	public static final String GET_WALLPAPERS_SEARCH_LIST = "wtsearch/";
	public static final String GET_RINGTONES_SEARCH_LIST = "rtsearch/";
	public static final String GET_NOTIFICATIONS_SEARCH_LIST = "ntsearch/";
	public static final String GET_APPS_SCREENSHOTS = "screenshots/";
	
	public static final String TAG = "API";
	
	private static RequestThread requestThread = new RequestThread();
	private static LinkedList<RequestData> requestStack = new LinkedList<RequestData>();
	
	static {
		requestThread.start();
	}
	
	public static void getTrendingItems(int limit, int offset, RequestObserver observer) {
		sendAsyncRequestGet(GET_TRENDING_ITEMS, limit + "/" + offset, observer);
	}
	
	public static void getAppsList(int limit, int offset, RequestObserver observer) {
		sendAsyncRequestGet(GET_APPS_LIST, limit + "/" + offset, observer);
	}
	
	public static void getAppsSearchList(int limit, int offset, String tag, RequestObserver observer) {
		sendAsyncRequestGet(GET_APPS_SEARCH_LIST, limit + "/" + offset + "/" + tag, observer);
	}
	
	public static void getAppsScreenshots(int appId, RequestObserver observer) {
		sendAsyncRequestGet(GET_APPS_SCREENSHOTS, "" + appId, observer);
	}
	
	public static void getWallpapersList(int limit, int offset, RequestObserver observer) {
		sendAsyncRequestGet(GET_WALLPAPERS_LIST, limit + "/" + offset, observer);
	}
	
	public static void getWallpapersSearchList(int limit, int offset, String tag, RequestObserver observer) {
		sendAsyncRequestGet(GET_WALLPAPERS_SEARCH_LIST, limit + "/" + offset + "/" + tag, observer);
	}
	
	public static void getRingtonesList(int limit, int offset, RequestObserver observer) {
		sendAsyncRequestGet(GET_RINGTONES_LIST, limit + "/" + offset, observer);
	}
	
	public static void getRingtonesSearchList(int limit, int offset, String tag, RequestObserver observer) {
		sendAsyncRequestGet(GET_RINGTONES_SEARCH_LIST, limit + "/" + offset + "/" + tag, observer);
	}
	
	public static void getNotificationsList(int limit, int offset, RequestObserver observer) {
		sendAsyncRequestGet(GET_NOTIFICATIONS_LIST, limit + "/" + offset, observer);
	}
	
	public static void getNotificationsSearchList(int limit, int offset, String tag, RequestObserver observer) {
		sendAsyncRequestGet(GET_NOTIFICATIONS_SEARCH_LIST, limit + "/" + offset + "/" + tag, observer);
	}
	
	public static void search(int limit, int offset, String query, RequestObserver observer) {
		sendAsyncRequestGet(SEARCH, limit + "/" + offset + "/" + query, observer);
	}
	
	public static void getTagsList(int category, RequestObserver observer) {
		sendAsyncRequestGet(GET_TAGS_LIST, "" + category, observer);
	}
	
	// helper functions to send get and post requests
	private static void sendAsyncRequestGet(String command, String requestStr, RequestObserver observer) {
		requestStr = Constants.SERVER_URL + command + deviceId + requestStr;

		sendAsyncRequest(command, requestStr, RequestData.GET_METHOD, observer);
	}
	
//	private static void sendAsyncRequestPost(String command, ArrayList<BasicNameValuePair> params, RequestObserver observer) {
//		params.add(new BasicNameValuePair("user_id", deviceId));
//		
//		String requestStr = Constants.SERVER_URL + command;
//
//		try {
//			sendAsyncRequest(requestStr, command, RequestData.POST_METHOD, new UrlEncodedFormEntity(params), observer);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//	}
	
	private static RequestData sendAsyncRequest(String command, String requestStr, int resuestMethod, RequestObserver observer) {
		RequestData requestData = new RequestData();
		requestData.requestObserver = observer;
		requestData.requestStr = requestStr;
		requestData.command = command;
		requestData.requestMethod = resuestMethod;
		synchronized (requestStack) {
			requestStack.add(requestData);
			requestStack.notifyAll();
		}
		return requestData;
	}
	
//	private static RequestData sendAsyncRequest(String requestStr, String command, int resuestMethod, HttpEntity params, RequestObserver observer) {
//		RequestData requestData = new RequestData();
//		requestData.requestObserver = observer;
//		requestData.requestStr = requestStr;
//		requestData.command = command;
//		requestData.params = params;
//		requestData.requestMethod = RequestData.POST_METHOD;
//		synchronized (requestStack) {
//			requestStack.add(requestData);
//			requestStack.notifyAll();
//		}
//		return requestData;
//	}
	
	public static String convertResponseToString(HttpResponse response) {
		StringBuilder sb = new StringBuilder();
		HttpEntity entity = response.getEntity();
		try {
			if (entity != null) {
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					instream.close();
				}
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String resultString = sb.toString();
		return resultString;
	}
	
	private static class RequestThread extends Thread {

		private DefaultHttpClient httpClient;

		public RequestThread() {
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (true) {
					if (requestStack.size() == 0) {
						synchronized (requestStack) {
							requestStack.wait();
						}
					}
					if (requestStack.size() != 0) {
						RequestData requestData;
						synchronized (requestStack) {
							requestData = requestStack.get(0);
						}
						if (httpClient == null) {
							httpClient = new DefaultHttpClient();
						}
						HttpRequestBase request = null;
						if (requestData.requestMethod == RequestData.GET_METHOD) {
							request = new HttpGet(requestData.requestStr);
						} else if (requestData.requestMethod == RequestData.POST_METHOD) {
							request = new HttpPost(requestData.requestStr);
							((HttpPost) request).setEntity(requestData.params);
						}
						Log.i(TAG, "request = " + requestData.requestStr);
						
						HttpResponse response = null;
						int statusCode = -1;
						String responseStr = null;
						try {
							Log.i(TAG, "request = " + requestData.requestStr);

							response = httpClient.execute(request);
							statusCode = response.getStatusLine().getStatusCode();
							
							Log.i(TAG, "statusCode = " + statusCode);
							
							if (statusCode >= 500) {
								throw new IOException("statusCode: " + statusCode);
							}
							
							responseStr = convertResponseToString(response);
							Log.i(TAG, "response = " + responseStr);
							
							JSONObject jsonObject = null;
							try {
								jsonObject = new JSONObject(responseStr);
							} catch (JSONException e) {
								JSONArray jsonArray = new JSONArray(responseStr);
								jsonObject = new JSONObject();
								jsonObject.put("values", jsonArray);
							}
							if (requestData.requestObserver != null) {
								requestData.requestObserver.onSuccess(jsonObject);
							}

							synchronized (requestStack) {
								requestStack.remove(requestData);
							}
						} catch (ClientProtocolException e) {
							e.printStackTrace();
							synchronized (requestStack) {
								requestStack.remove(requestData);
							}
						} catch (IOException e) {
							e.printStackTrace();
							synchronized (requestStack) {
								requestStack.remove(requestData);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							synchronized (requestStack) {
								requestStack.remove(requestData);
							}
						}
					}
					if (Thread.interrupted())
						break;
				}
			} catch (InterruptedException e) {
				Log.d(TAG, "api is interapted " + e);
			}
		}
	}

	public static class RequestData {
		public final static int POST_METHOD = 1;
		public final static int GET_METHOD = 2;
		
		public RequestObserver requestObserver;
		public int requestMethod = GET_METHOD;
		public String requestStr;
		public String command;
		public HttpEntity params;
		
		public void setRequestObserver(RequestObserver requestObserver){
			 this.requestObserver = requestObserver;
		}
	}

	public static interface RequestObserver {
		public void onSuccess(JSONObject response) throws JSONException;

		public void onError(String response, Exception e);
	}

}

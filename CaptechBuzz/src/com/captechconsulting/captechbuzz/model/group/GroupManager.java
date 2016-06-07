package com.captechconsulting.captechbuzz.model.group;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.captechconsulting.captechbuzz.model.GsonRequest;
import com.captechconsulting.captechbuzz.model.RequestManager;

public class GroupManager {

	private final String TAG = getClass().getSimpleName();
	private static GroupManager mInstance;

    private static String GROUP_BASE = "http://192.168.56.2:8080/SuperWeChatServer/Server";
//    private static String GROUP_BASE = "http://10.0.2.2:8080/SuperWeChatServer/Server";
	private static String GROUP_DOWNLOAD_PUBLIC_GORUP = "download_public_groups";
	private static String GROUP_QUERY = "request";
	private static String GROUP_USER_NAME = "m_user_name";
	private static String GROUP_DEFAULT_USERNAME = "a";
	private static String GROUP_RESULTS_PER_PAGE = "page_size";
	private static String GROUP_PAGE_NUM = "page_id";
	public static int GROUP_DEFAULT_PAGE_SIZE = 10;

	public static GroupManager getInstance(){
		if(mInstance == null) {
			mInstance = new GroupManager();
		}

		return mInstance;
	}

	public <T> void getDefaultHashtagGroups(Listener<Group[]> listener, ErrorListener errorListener, int pageNum){
		getTweetForHashtag(listener, errorListener, GROUP_DOWNLOAD_PUBLIC_GORUP,
                GROUP_DEFAULT_PAGE_SIZE, pageNum);
	}

	public void getTweetForHashtag(Listener<Group[]> listener, ErrorListener errorListener,
								   String hashtag, int pageSize, int pageNum){

		Uri.Builder uriBuilder = Uri.parse(GROUP_BASE).buildUpon()
				.appendQueryParameter(GROUP_QUERY, hashtag)
				.appendQueryParameter(GROUP_USER_NAME, GROUP_DEFAULT_USERNAME)
				.appendQueryParameter(GROUP_RESULTS_PER_PAGE, "" + pageSize)
				.appendQueryParameter(GROUP_PAGE_NUM, "" + pageNum);

		String uri = uriBuilder.build().toString();
		Log.i(TAG, "getGroupForHashtag: uri = " + uri);

		GsonRequest<Group[]> request = new GsonRequest<Group[]>(Method.GET
				, uri
				, Group[].class
				, listener
				, errorListener);

		Log.v(TAG, request.toString());
		RequestManager.getRequestQueue().add(request);
	}

}

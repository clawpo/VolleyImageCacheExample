package com.captechconsulting.captechbuzz;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.captechconsulting.captechbuzz.model.group.Group;
import com.captechconsulting.captechbuzz.model.group.GroupManager;
import com.captechconsulting.captechbuzz.model.images.ImageCacheManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 
 * @author Trey Robinson
 *
 */
public class GroupArrayAdapter extends BaseAdapter implements Response.Listener<Group[]>, Response.ErrorListener {

	private final String TAG = getClass().getSimpleName();

	private static final SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy h:mm a", Locale.CHINA);

	/**
	 *  The data that drives the adapter
	 */
	private final ArrayList<Group> mData;

    private int pageId = -1;

	/**
	 * The last network response containing twitter metadata
	 */
//	private TweetData mTweetData;
	private boolean isLoading;


	/**
	 * Flag telling us our last network call returned 0 results and we do not need to execute any new requests
	 */
	private boolean moreDataToLoad;

    Context mContext;

	/**
	 * @param context
	 * 			The context
	 * @param newData
	 * 			Initial dataset.
	 */
	public GroupArrayAdapter(Context context, ArrayList<Group> newData) {
        mContext = context;
        mData = new ArrayList<Group>();
		mData.addAll(newData);
		moreDataToLoad = true;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		ViewHolder viewHolder;

		//check to see if we need to load more data
		if(shouldLoadMoreData(mData, position) ) {
			loadMoreData();
		}

		if(v == null){
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.group_list_item, null);

			viewHolder = new ViewHolder();
			viewHolder.groupAvatar = (NetworkImageView)v.findViewById(R.id.groupAvatar);
			viewHolder.groupName = (TextView)v.findViewById(R.id.groupName);
			viewHolder.groupDesc = (TextView)v.findViewById(R.id.groupDesc);
			viewHolder.groupUpdateTime = (TextView)v.findViewById(R.id.groupTime);

			v.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) v.getTag();
		}

		Group group = mData.get(position);
		if(group != null){
            String url = group.getAvatarPath();
            Log.e(TAG,"url="+url);
//            ImageCacheManager.getInstance().loadImage(url,viewHolder.groupAvatar);
			viewHolder.groupAvatar.setImageUrl(url, ImageCacheManager.getInstance().getImageLoader());
			viewHolder.groupName.setText("@" + group.getMGroupName());
			viewHolder.groupDesc.setText(group.getMGroupDescription());
			viewHolder.groupUpdateTime.setText(formatDisplayDate(group.getMGroupLastModifiedTime()));
		}

		return v;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Group getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private String formatDisplayDate(String time){
		if(time != null){
            long now = Long.parseLong(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(now);
			return sdf.format(new Date(now));
		}

		return "";
	}
	/**
	 * Add the data to the current listview
	 * @param newData
	 * 			Data to be added to the listview
	 */
	public void add(ArrayList<Group> newData)
	{
		isLoading = false;
		if(!newData.isEmpty()){
			mData.addAll(newData);
			notifyDataSetChanged();
		}
	}

	/**
	 * a new request.
	 * @param data
	 * 			Current list of data
	 * @param position
	 * 			Current view position
	 * @return
	 */
	private boolean shouldLoadMoreData(List<Group> data, int position){
		// If showing the last set of data, request for the next set of data
		boolean scrollRangeReached = (position > (data.size() - GroupManager.GROUP_DEFAULT_PAGE_SIZE));
		return (scrollRangeReached && !isLoading && moreDataToLoad);
	}

	private void loadMoreData(){
		isLoading = true;
		Log.v(getClass().toString(), "Load more group");
		GroupManager.getInstance().getDefaultHashtagGroups(this, this, pageId + 1);
	}


	/**
	 * Viewholder for the listview row
	 * 
	 * @author Trey Robinson
	 *
	 */
	static class ViewHolder{
		NetworkImageView groupAvatar;
		TextView groupName;
		TextView groupDesc;
		TextView groupUpdateTime;
	}

	@Override
	public void onResponse(Group[] response) {
		if(response != null){
			mData.addAll(array2List(response));

			if(mData != null && mData.size() > 0) {
				moreDataToLoad = true;
			}
			else {
				moreDataToLoad = false;
			}

			notifyDataSetChanged();
			Log.v(TAG, "New tweets retrieved");
		}

		isLoading = false;
	}

	@Override
	public void onErrorResponse(VolleyError error) {
		Log.e(TAG, "Error retrieving additional group: " + error.getMessage());
		isLoading = false;
	}

    /**
     * 将数组转换为ArrayList集合
     * @param ary
     * @return
     */
    public static <T> ArrayList<T> array2List(T[] ary){
        List<T> list = Arrays.asList(ary);
        ArrayList<T> arrayList=new ArrayList<T>(list);
        return arrayList;
    }

}

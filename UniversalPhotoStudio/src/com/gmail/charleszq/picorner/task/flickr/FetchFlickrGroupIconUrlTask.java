/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.view.View;

import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.googlecode.flickrjandroid.groups.Group;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FetchFlickrGroupIconUrlTask extends AbstractFetchIconUrlTask {

	private Group mPhotoGroup;

	public FetchFlickrGroupIconUrlTask(Context ctx) {
		super(ctx);
	}

	/**
	 * @param ctx
	 */
	public FetchFlickrGroupIconUrlTask(Context ctx, Group g) {
		super(ctx);
		mPhotoGroup = g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Object... params) {
		beforeExecute(params);
		return mPhotoGroup.getBuddyIconUrl();
	}

	@Override
	protected void beforeExecute(Object... params) {
		if (params.length == 1)
			super.beforeExecute(params);
		else {
			mPhotoGroup = (Group) params[0];
			mIconViewRef = new WeakReference<View>((View) params[1]);
		}
	}

}

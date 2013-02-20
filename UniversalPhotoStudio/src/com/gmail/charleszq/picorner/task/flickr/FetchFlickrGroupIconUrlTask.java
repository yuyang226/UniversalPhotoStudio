/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.view.View;

import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.groups.GroupsInterface;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FetchFlickrGroupIconUrlTask extends AbstractFetchIconUrlTask {

	private static final String DEF_BUDDY_ICON_URL = "http://www.flickr.com/images/buddyicon.jpg"; //$NON-NLS-1$

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
		String buddyIconUrl = mPhotoGroup.getBuddyIconUrl();
		if (DEF_BUDDY_ICON_URL.equals(buddyIconUrl)) {
			Flickr f = FlickrHelper.getInstance().getFlickr();
			GroupsInterface gi = f.getGroupsInterface();
			try {
				Group g = gi.getInfo(mPhotoGroup.getId());
				buddyIconUrl = g.getBuddyIconUrl();
			} catch (Exception e) {
			}
		}
		return buddyIconUrl;
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

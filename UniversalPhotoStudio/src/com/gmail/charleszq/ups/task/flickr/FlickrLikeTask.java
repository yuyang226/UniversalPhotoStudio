/**
 * 
 */
package com.gmail.charleszq.ups.task.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.task.AbstractContextAwareTask;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.favorites.FavoritesInterface;

/**
 * Represents the task to add a photo as favorite.
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrLikeTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	/**
	 * @param ctx
	 *            should be an activity
	 * @param lis 
	 */
	public FlickrLikeTask(Context ctx, IGeneralTaskDoneListener<Boolean> lis) {
		super(ctx);
		addTaskDoneListener(lis);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Boolean doInBackground(String... params) {
		boolean result = false;
		if (params.length == 0) {
			return result;
		}
		String photoId = params[0];
		UPSApplication app = (UPSApplication) ((Activity) mContext)
				.getApplication();
		String token = app.getFlickrToken();
		String secret = app.getFlickrTokenSecret();
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token, secret);
		FavoritesInterface fi = f.getFavoritesInterface();
		try {
			fi.add(photoId);
			result = true;
		} catch (Exception e) {
		}
		return result;
	}

}

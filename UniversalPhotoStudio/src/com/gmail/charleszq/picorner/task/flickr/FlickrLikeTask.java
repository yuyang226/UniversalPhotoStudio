/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.favorites.FavoritesInterface;

/**
 * Represents the task to add a photo as favorite or remove a photo from my fav list.
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
		boolean like = true;
		if (params.length > 1) {
			like = Boolean.parseBoolean(params[1]);
		}
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		String token = app.getFlickrToken();
		String secret = app.getFlickrTokenSecret();
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token, secret);
		FavoritesInterface fi = f.getFavoritesInterface();
		try {
			if (like) {
				fi.add(photoId);
			} else {
				fi.remove(photoId);
			}
			result = true;
		} catch (Exception e) {
		}
		return result;
	}

}

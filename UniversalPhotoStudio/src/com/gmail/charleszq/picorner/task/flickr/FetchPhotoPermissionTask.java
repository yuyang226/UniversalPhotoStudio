/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Permissions;

/**
 * @author charleszq
 * 
 */
public class FetchPhotoPermissionTask extends
		AbstractContextAwareTask<String, Integer, Permissions> {

	public FetchPhotoPermissionTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Permissions doInBackground(String... params) {
		String photoId = params[0];
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mContext);
		try {
			return f.getPhotosInterface().getPerms(photoId);
		} catch (Exception e) {
			Log.w(TAG, "unable to get the permission of the photo: " + e.getMessage() ); //$NON-NLS-1$
		}
		return null;
	}

}

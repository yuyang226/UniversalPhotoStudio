/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Photo;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * Represents the task to see if the calling user liked the given
 * <code>MediaObject</code> or not.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class CheckUserLikePhotoTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public CheckUserLikePhotoTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {

		String photoId = params[0];
		String photoSecret = params[1];

		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mContext);
		PhotosInterface pi = f.getPhotosInterface();
		try {
			Photo p = pi.getInfo(photoId, photoSecret);
			return p.isFavorite();
		} catch (Exception e) {
			Log.w(TAG,"Unable to get the photo information: " + e.getMessage()); //$NON-NLS-1$
		}
		return false;
	}

}

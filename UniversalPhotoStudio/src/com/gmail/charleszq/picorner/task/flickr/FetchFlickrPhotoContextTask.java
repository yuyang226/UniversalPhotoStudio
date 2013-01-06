/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.List;

import android.util.Log;

import com.gmail.charleszq.picorner.task.AbstractGeneralTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoPlace;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * Represents the task to fetch the photo context, that is, which photo sets and
 * groups is this photo in.
 * 
 * @author charleszq
 * 
 */
public class FetchFlickrPhotoContextTask extends
		AbstractGeneralTask<String, Integer, List<PhotoPlace>> {

	@Override
	protected List<PhotoPlace> doInBackground(String... params) {
		
		String photoId = params[0];
		Log.d(TAG, "photo id: " + photoId ); //$NON-NLS-1$
		Flickr f = FlickrHelper.getInstance().getFlickr();
		PhotosInterface pi = f.getPhotosInterface();
		try {
			return pi.getAllContexts(photoId);
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
			return null;
		}
	}
}

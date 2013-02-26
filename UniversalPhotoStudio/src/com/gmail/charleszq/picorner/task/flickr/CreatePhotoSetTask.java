/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class CreatePhotoSetTask extends
		AbstractContextAwareTask<String, Integer, Photoset> {

	public CreatePhotoSetTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Photoset doInBackground(String... params) {
		String name = params[0];
		String photoId = params[1];

		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mContext);
		PhotosetsInterface pi = f.getPhotosetsInterface();
		try {
			return pi.create(name, name, photoId);
		} catch (Exception e) {
			return null;
		}
	}

}

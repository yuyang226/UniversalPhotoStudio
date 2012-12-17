/**
 * 
 */
package com.gmail.charleszq.ups.task.flickr;

import com.gmail.charleszq.ups.task.AbstractGeneralTask;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoFavouriteUserList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrGetPhotoFavCountTask extends
		AbstractGeneralTask<String, Integer, Integer> {

	@Override
	protected Integer doInBackground(String... params) {

		String photoid = params[0];
		Flickr f = FlickrHelper.getInstance().getFlickr();
		PhotosInterface pi = f.getPhotosInterface();

		int count = 0;
		try {
			PhotoFavouriteUserList list = pi.getFavorites(photoid, 10, 1);
			count = list.getTotal();
		} catch (Exception e) {
			logger.warn("Unable to get the flickr fav count: " + e.getMessage()); //$NON-NLS-1$
		}
		return count;
	}

}

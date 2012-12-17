/**
 * 
 */
package com.gmail.charleszq.ups.task.flickr;

import java.util.List;

import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.task.AbstractGeneralTask;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoFavouriteUserList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * Represents the task to fetch flickr fav users, only return the first 20
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class GetFlickrPhotoFavUsersTask extends
		AbstractGeneralTask<String, Integer, List<Author>> {

	@Override
	protected List<Author> doInBackground(String... params) {
		String photoId = params[0];
		Flickr f = FlickrHelper.getInstance().getFlickr();
		PhotosInterface pi = f.getPhotosInterface();
		try {
			PhotoFavouriteUserList list = pi.getFavorites(photoId, 20, 1);
			return ModelUtils.convertFlickrUsers(list);
		} catch (Exception e) {
			logger.warn("Unable to fetch flickr fav users: " + e.getMessage()); //$NON-NLS-1$
		}
		return null;
	}

}

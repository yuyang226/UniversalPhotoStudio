/**
 * 
 */
package com.gmail.charleszq.picorner.service.flickr;

import android.util.Log;

import com.gmail.charleszq.picorner.model.MediaObject;
import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.favorites.FavoritesInterface;
import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrMyFavoritesService extends FlickrAuthPhotoService {

	public FlickrMyFavoritesService(String userId, String token, String secret) {
		super(userId, token, secret);
	}

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Log.d(TAG, String.format("page size %s and page# %s", pageSize, pageNo)); //$NON-NLS-1$
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		FavoritesInterface fi = f.getFavoritesInterface();
		PhotoList list = fi.getList(mUserId, null, null, pageSize, pageNo + 1,
				mExtras);
		// this will reduce one more network call to get the status.
		MediaObjectCollection pc = ModelUtils.convertFlickrPhotoList(list);
		for (MediaObject photo : pc.getPhotos()) {
			photo.setUserLiked(true);
		}
		return pc;
	}
}

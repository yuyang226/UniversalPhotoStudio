/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import android.util.Log;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Log.d(TAG, String.format("page size %s and page# %s", pageSize, pageNo)); //$NON-NLS-1$
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		FavoritesInterface fi = f.getFavoritesInterface();
		PhotoList list = fi.getList(mUserId, null, null, pageSize, pageNo + 1,
				mExtras);

		return ModelUtils.convertFlickrPhotoList(list);
	}
}

/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import android.util.Log;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * @author Charles(charleszq@gmail.com)
 *
 */
public class FlickrContactPhotosService extends FlickrAuthPhotoService {

	/**
	 * @param userId
	 * @param token
	 * @param secret
	 */
	public FlickrContactPhotosService(String userId, String token, String secret) {
		super(userId, token, secret);
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Log.d(TAG, String.format("page size %s and page# %s", pageSize, pageNo)); //$NON-NLS-1$
		if( pageNo > 0 ) {
			//this service does not support pagination, then just return an empty list
			//so the UI knows there is no more data.
			MediaObjectCollection pc = new MediaObjectCollection();
			return pc;
		}
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		PhotosInterface pi = f.getPhotosInterface();
		PhotoList list = pi.getContactsPhotos(50, false, false, false);
		MediaObjectCollection pc = ModelUtils.convertFlickrPhotoList(list);
		return pc;
	}

}

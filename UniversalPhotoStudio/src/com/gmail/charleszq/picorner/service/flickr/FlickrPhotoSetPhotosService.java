/**
 * 
 */
package com.gmail.charleszq.picorner.service.flickr;

import android.util.Log;

import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * Represents the service to retrieve the photos of a given photo set, we need
 * to calculate to fetch last 2 pages
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrPhotoSetPhotosService extends FlickrAuthPhotoService {

	private Photoset mPhotoset;

	/**
	 * @param userId
	 * @param token
	 * @param secret
	 */
	public FlickrPhotoSetPhotosService(String userId, String token,
			String secret, Photoset ps) {
		super(userId, token, secret);
		this.mPhotoset = ps;
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
		PhotosetsInterface psi = f.getPhotosetsInterface();
		PhotoList list = psi.getPhotos(mPhotoset.getId(), mExtras,
				Flickr.PRIVACY_LEVEL_NO_FILTER, pageSize, pageNo+1);
		MediaObjectCollection col = ModelUtils.convertFlickrPhotoList(list);
		return col;
	}

}

/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrPhotoSetPhotosService extends FlickrAuthPhotoService {

	private String mPhotosetId;

	/**
	 * @param userId
	 * @param token
	 * @param secret
	 */
	public FlickrPhotoSetPhotosService(String userId, String token,
			String secret, String photosetId) {
		super(userId, token, secret);
		this.mPhotosetId = photosetId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		PhotosetsInterface psi = f.getPhotosetsInterface();
		PhotoList list = psi.getPhotos(mPhotosetId, mExtras,
				Flickr.PRIVACY_LEVEL_NO_FILTER, pageSize, pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}

}

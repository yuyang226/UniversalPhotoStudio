/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrGalleryPhotosService extends FlickrAuthPhotoService {

	private String mGalleryId;

	/**
	 * @param userId
	 * @param token
	 * @param secret
	 */
	public FlickrGalleryPhotosService(String userId, String token,
			String secret, String gid) {
		super(userId, token, secret);
		this.mGalleryId = gid;
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
		GalleriesInterface si = f.getGalleriesInterface();
		PhotoList list = si
				.getPhotos(mGalleryId, mExtras, pageSize, pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}

}

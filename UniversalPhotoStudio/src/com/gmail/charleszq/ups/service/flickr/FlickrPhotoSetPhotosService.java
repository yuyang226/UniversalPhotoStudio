/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
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

		int total = mPhotoset.getPhotoCount();
		int page = total / pageSize;
		if (total % pageSize > 0) {
			page++;
		}

		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		PhotosetsInterface psi = f.getPhotosetsInterface();
		PhotoList list = psi.getPhotos(mPhotoset.getId(), mExtras,
				Flickr.PRIVACY_LEVEL_NO_FILTER, pageSize, page);
		MediaObjectCollection col = ModelUtils.convertFlickrPhotoList(list);

		if (page - 1 >= 1) {
			PhotoList list2 = psi.getPhotos(mPhotoset.getId(), mExtras,
					Flickr.PRIVACY_LEVEL_NO_FILTER, pageSize, page - 1);
			MediaObjectCollection col2 = ModelUtils.convertFlickrPhotoList(list2);
			int pos = 0;
			for( MediaObject obj : col2.getPhotos() ) {
				col.addPhoto(obj,pos++);
			}
		}

		return col;
	}

}

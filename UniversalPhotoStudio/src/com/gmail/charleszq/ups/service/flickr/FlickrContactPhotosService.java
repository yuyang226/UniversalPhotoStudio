/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

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
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		PhotosInterface pi = f.getPhotosInterface();
		PhotoList list = pi.getContactsPhotos(50, false, false, false);
		MediaObjectCollection pc = ModelUtils.convertFlickrPhotoList(list);
		pc.setPageSize(list.size());
		pc.setTotalCount(list.size());
		return pc;
	}

}

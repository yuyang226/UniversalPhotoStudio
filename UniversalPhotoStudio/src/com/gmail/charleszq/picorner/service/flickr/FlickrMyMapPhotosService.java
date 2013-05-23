/**
 * 
 */
package com.gmail.charleszq.picorner.service.flickr;

import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.PhotoList;
import com.googlecode.flickrjandroid.photos.PhotosInterface;

/**
 * Represents the service to get my photos which have geo information.
 * 
 * @author charles(charleszq@gmail.com)
 *
 */
public class FlickrMyMapPhotosService extends FlickrAuthPhotoService {

	public FlickrMyMapPhotosService(String userId, String token, String secret) {
		super(userId, token, secret);
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.picorner.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		PhotosInterface pi = f.getPhotosInterface();
		//TODO for now only returns the first 100 photos.
		PhotoList list = pi.getWithGeoData(null, null, null, null, -1, null, mExtras, 100, 1);
		MediaObjectCollection pc = ModelUtils.convertFlickrPhotoList(list);
		return pc;
	}

}

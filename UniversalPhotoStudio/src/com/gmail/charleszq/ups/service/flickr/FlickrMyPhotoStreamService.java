/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * @author Charles(charleszq@gmail.com)
 *
 */
public class FlickrMyPhotoStreamService extends FlickrAuthPhotoService {
	
	public FlickrMyPhotoStreamService(String userId, String token, String secret) {
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
		PeopleInterface si = f.getPeopleInterface();
		PhotoList list = si.getPhotos(mUserId, mExtras, pageSize, pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}

}

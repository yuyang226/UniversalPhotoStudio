/**
 * 
 */
package com.gmail.charleszq.picorner.service.flickr;

import android.util.Log;

import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.people.PeopleInterface;
import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * @author Charles(charleszq@gmail.com)
 *
 */
public class FlickrUserPhotoStreamService extends FlickrAuthPhotoService {
	
	public FlickrUserPhotoStreamService(String userId, String token, String secret) {
		super(userId, token, secret);
	}

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Log.d(TAG, String.format("page size %s and page# %s", pageSize, pageNo)); //$NON-NLS-1$
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		PeopleInterface si = f.getPeopleInterface();
		PhotoList list = si.getPhotos(mUserId, mExtras, pageSize, pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}

}

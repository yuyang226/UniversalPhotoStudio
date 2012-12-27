/**
 * 
 */
package com.gmail.charleszq.picorner.service.flickr;

import android.util.Log;

import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.groups.pools.PoolsInterface;
import com.googlecode.flickrjandroid.photos.PhotoList;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrPhotoGroupPhotosService extends FlickrAuthPhotoService {

	private String mGroupId;

	/**
	 * @param userId
	 * @param token
	 * @param secret
	 */
	public FlickrPhotoGroupPhotosService(String userId, String token,
			String secret, String groupId) {
		super(userId, token, secret);
		this.mGroupId = groupId;
	}

	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Log.d(TAG, String.format("page size %s and page# %s", pageSize, pageNo)); //$NON-NLS-1$
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mAuthToken,
				mTokenSecret);
		PoolsInterface pi = f.getPoolsInterface();
		PhotoList list = pi.getPhotos(mGroupId, null, mExtras, pageSize,
				pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}

}

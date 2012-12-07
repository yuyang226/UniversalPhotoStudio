/**
 * 
 */
package com.gmail.charleszq.ups.service.flickr;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.FlickrHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;
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
		PoolsInterface pi = f.getPoolsInterface();
		PhotoList list = pi.getPhotos(mGroupId, null, mExtras, pageSize,
				pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}

}

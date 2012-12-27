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
import com.googlecode.flickrjandroid.stats.StatsInterface;
import com.googlecode.flickrjandroid.stats.StatsInterface.SORT;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrMyPopularPhotosService extends FlickrAuthPhotoService {


	public FlickrMyPopularPhotosService(String userId, String token, String secret) {
		super(userId, token, secret);
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
		StatsInterface si = f.getStatsInterface();
		PhotoList list = si.getPopularPhotos(null, SORT.FAVORITES, pageSize, pageNo + 1);
		return ModelUtils.convertFlickrPhotoList(list);
	}

}

/**
 * 
 */
package com.gmail.charleszq.picorner.service.ig;

import org.jinstagram.AdvancedInstagram;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

import android.location.Location;

import com.gmail.charleszq.picorner.model.MediaObjectCollection;
import com.gmail.charleszq.picorner.utils.InstagramHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramSearchNearbyPhotosService extends
		AbstractInstagramPhotoListService {

	private Location mLocation;

	/**
	 * Constructor.
	 */
	public InstagramSearchNearbyPhotosService(Location location) {
		mLocation = location;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.service.IPhotoService#getPhotos(int,
	 * int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		MediaObjectCollection pc = new MediaObjectCollection();
		AdvancedInstagram ig = InstagramHelper.getInstance().getInstagram();

		MediaFeed mf = null;
		if (pageNo <= 0) {
			// 5000: the distance, 5km
			mf = ig.searchMedia(mLocation.getLatitude(),
					mLocation.getLongitude(), 5000, pageSize);
		} else {
			if (mPagination != null) {
				mf = ig.getNextPage(mPagination, pageSize);
			}
		}

		if (mf != null) {
			mPagination = mf.getPagination();
			for (MediaFeedData feed : mf.getData()) {
				pc.addPhoto(ModelUtils.convertInstagramPhoto(feed));
			}
		}

		return pc;
	}

}

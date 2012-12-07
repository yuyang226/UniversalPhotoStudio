/**
 * 
 */
package com.gmail.charleszq.ups.service.ig;

import org.jinstagram.Instagram;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.utils.InstagramHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class InstagramPopularsService implements IPhotoService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Instagram ig = InstagramHelper.getInstance().getInstagram();

		MediaObjectCollection pc = new MediaObjectCollection();

		MediaFeed mf = ig.getPopularMedia();
		if( mf == null ) {
			return pc;
		}
		for (MediaFeedData feed : mf.getData()) {
			pc.addPhoto(ModelUtils.convertInstagramPhoto(feed));
		}

		pc.setPageSize(mf.getData().size());
		pc.setTotalCount(mf.getData().size());
		return pc;
	}

}

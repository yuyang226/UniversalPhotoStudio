/**
 * 
 */
package com.gmail.charleszq.ups.service.ig;

import org.jinstagram.Instagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.utils.InstagramHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramMyFeedsService implements IPhotoService {

	private Token mToken;

	/**
	 * 
	 */
	public InstagramMyFeedsService(Token token) {
		mToken = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		Instagram ig = InstagramHelper.getInstance().getAuthedInstagram(mToken);
		MediaFeed mf = ig.getUserFeeds();

		MediaObjectCollection pc = new MediaObjectCollection();
		if (mf == null) {
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

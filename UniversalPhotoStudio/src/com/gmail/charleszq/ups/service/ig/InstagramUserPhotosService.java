/**
 * 
 */
package com.gmail.charleszq.ups.service.ig;

import org.jinstagram.AdvancedInstagram;
import org.jinstagram.auth.model.Token;
import org.jinstagram.entity.users.feed.MediaFeed;
import org.jinstagram.entity.users.feed.MediaFeedData;

import com.gmail.charleszq.ups.model.MediaObjectCollection;
import com.gmail.charleszq.ups.utils.InstagramHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramUserPhotosService extends
		AbstractInstagramPhotoListService {

	private long mUserId;
	private Token mToken;

	public InstagramUserPhotosService(Token token, long userId) {
		this.mUserId = userId;
		this.mToken = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		MediaObjectCollection pc = new MediaObjectCollection();
		AdvancedInstagram ig = InstagramHelper.getInstance()
				.getAuthedInstagram(mToken);
		MediaFeed mf = null;
		if (pageNo == 0 || mPagination == null) {
			mf = ig.getRecentMediaFeed(mUserId, pageSize);
		} else {
			mf = ig.getNextPage(mPagination, pageSize);
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

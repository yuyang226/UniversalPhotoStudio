/**
 * 
 */
package com.gmail.charleszq.ups.service.ig;

import org.jinstagram.AdvancedInstagram;
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
public class InstagramMyPhotosService implements IPhotoService {

	private Token mToken;
	private long mUserId;

	public InstagramMyPhotosService(long userId, Token token) {
		this.mToken = token;
		this.mUserId = userId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		AdvancedInstagram ig = InstagramHelper.getInstance()
				.getAuthedInstagram(mToken);
		MediaFeed mf = ig.getRecentMediaFeed(mUserId, pageSize);

		MediaObjectCollection pc = new MediaObjectCollection();
		if (mf == null) {
			return pc;
		}
		for (MediaFeedData feed : mf.getData()) {
			pc.addPhoto(ModelUtils.convertInstagramPhoto(feed));
		}

		int returnCount = mf.getData().size();
		while (returnCount < pageSize) {
			mf = ig.getNextPage(mf.getPagination(), pageSize - returnCount);
			if (mf == null)
				break;
			returnCount += mf.getData().size();
			if (mf != null) {
				for (MediaFeedData feed : mf.getData()) {
					pc.addPhoto(ModelUtils.convertInstagramPhoto(feed));
				}
			}
		}

		pc.setPageSize(pageSize);
		pc.setTotalCount(returnCount);
		return pc;
	}

}

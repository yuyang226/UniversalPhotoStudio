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
		AdvancedInstagram ig = InstagramHelper.getInstance().getAuthedInstagram(mToken);
		MediaFeed mf = ig.getUserFeeds(pageSize);

		MediaObjectCollection pc = new MediaObjectCollection();
		if (mf == null) {
			return pc;
		}
		for (MediaFeedData feed : mf.getData()) {
			pc.addPhoto(ModelUtils.convertInstagramPhoto(feed));
		}
		
		int returnCount = mf.getData().size();
		while( returnCount < pageSize ) {
			mf = ig.getNextPage( mf.getPagination(), pageSize - returnCount );
			returnCount += mf.getData().size();
			if(mf != null ) {
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

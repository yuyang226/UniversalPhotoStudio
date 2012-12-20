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
public class InstagramUserPhotosService implements IPhotoService {
	
	private long mUserId;
	private Token mToken;

	public InstagramUserPhotosService( Token token, long userId ) {
		this.mUserId = userId;
		this.mToken = token;
	}

	/* (non-Javadoc)
	 * @see com.gmail.charleszq.ups.service.IPhotoService#getPhotos(int, int)
	 */
	@Override
	public MediaObjectCollection getPhotos(int pageSize, int pageNo)
			throws Exception {
		AdvancedInstagram ig = InstagramHelper.getInstance().getAuthedInstagram(mToken);
		MediaFeed mf = ig.getRecentMediaFeed(mUserId,pageSize);
		MediaObjectCollection pc = new MediaObjectCollection();
		for (MediaFeedData feed : mf.getData()) {
			pc.addPhoto(ModelUtils.convertInstagramPhoto(feed));
		}

		pc.setPageSize(mf.getData().size());
		pc.setTotalCount(mf.getData().size());
		return pc;
	}

}

/**
 * 
 */
package com.gmail.charleszq.ups.task.ig;

import java.util.List;

import org.jinstagram.AdvancedInstagram;
import org.jinstagram.entity.likes.LikesFeed;
import org.jinstagram.exceptions.InstagramException;

import android.content.Context;

import com.gmail.charleszq.ups.model.Author;
import com.gmail.charleszq.ups.task.AbstractContextAwareTask;
import com.gmail.charleszq.ups.utils.InstagramHelper;
import com.gmail.charleszq.ups.utils.ModelUtils;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class InstagramLoadLikesTask extends
		AbstractContextAwareTask<String, Integer, List<Author>> {

	public InstagramLoadLikesTask(Context ctx) {
		super(ctx);
	}
	
	@Override
	protected List<Author> doInBackground(String... params) {
		String id = params[0];
		int index = id.indexOf("_"); //$NON-NLS-1$
		if( index != -1 ) {
			id = id.substring(0,index);
			logger.debug("instagram media id: " + id); //$NON-NLS-1$
		}
		long photoId = Long.parseLong(id);
		AdvancedInstagram ig = InstagramHelper.getInstance().getInstagram();
		try {
			LikesFeed feed = ig.getUserLikes(photoId);
			logger.debug( "instagram likes returned: " + feed.getUserList().size()); //$NON-NLS-1$
			return ModelUtils.convertInstagramLikesFeed( feed );
		} catch (InstagramException e) {
		}
		return null;
	}

}

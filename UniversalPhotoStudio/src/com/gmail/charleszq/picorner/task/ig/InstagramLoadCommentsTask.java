/**
 * 
 */
package com.gmail.charleszq.picorner.task.ig;

import java.util.List;

import org.jinstagram.AdvancedInstagram;
import org.jinstagram.entity.comments.MediaCommentsFeed;
import org.jinstagram.exceptions.InstagramException;

import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.model.MediaObjectComment;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.InstagramHelper;
import com.gmail.charleszq.picorner.utils.ModelUtils;

/**
<<<<<<< HEAD
 * Represents the task to load comments of a given photo.
 * 
=======
>>>>>>> 4d8981d2dade917d7848ff11fd4ffe76115428b4
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramLoadCommentsTask extends
		AbstractContextAwareTask<String, Integer, List<MediaObjectComment>> {

	public InstagramLoadCommentsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected List<MediaObjectComment> doInBackground(String... params) {
		
		String id = params[0];
		int index = id.indexOf("_"); //$NON-NLS-1$
		if( index != -1 ) {
			id = id.substring(0,index);
			Log.d(TAG,"instagram media id: " + id); //$NON-NLS-1$
		}
		long photoId = Long.parseLong(id);
		AdvancedInstagram ig = InstagramHelper.getInstance().getInstagram();
		try {
			MediaCommentsFeed feed = ig.getMediaComments(photoId);
			return ModelUtils.convertInstagramComments(feed);
		} catch (InstagramException e) {
		}
		return null;
	}

}

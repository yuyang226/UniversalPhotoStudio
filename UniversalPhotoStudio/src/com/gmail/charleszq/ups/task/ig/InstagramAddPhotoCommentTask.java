/**
 * 
 */
package com.gmail.charleszq.ups.task.ig;

import org.jinstagram.Instagram;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.task.AbstractContextAwareTask;
import com.gmail.charleszq.ups.utils.InstagramHelper;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramAddPhotoCommentTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public InstagramAddPhotoCommentTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String photoId = params[0];
		String comment = params[1];

		int index = photoId.indexOf("_"); //$NON-NLS-1$
		if (index != -1) {
			photoId = photoId.substring(0, index);
			logger.debug("instagram media id: " + photoId); //$NON-NLS-1$
		}

		UPSApplication app = (UPSApplication) ((Activity) mContext)
				.getApplication();
		Instagram ig = InstagramHelper.getInstance().getAuthedInstagram(
				app.getInstagramAuthToken());
		try {
			ig.setMediaComments(Long.valueOf(photoId), comment);
			return true;
		} catch (Exception e) {
			logger.warn( "cannot add comment: " + e.getMessage()); //$NON-NLS-1$
		}
		return false;
	}

}

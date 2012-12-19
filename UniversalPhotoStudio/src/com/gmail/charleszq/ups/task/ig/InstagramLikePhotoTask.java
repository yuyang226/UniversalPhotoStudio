/**
 * 
 */
package com.gmail.charleszq.ups.task.ig;

import org.jinstagram.Instagram;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.task.AbstractContextAwareTask;
import com.gmail.charleszq.ups.task.IGeneralTaskDoneListener;
import com.gmail.charleszq.ups.utils.InstagramHelper;

/**
 * Represents the task to like a photo, or remove the photo from my like list.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramLikePhotoTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public InstagramLikePhotoTask(Context ctx,
			IGeneralTaskDoneListener<Boolean> lis) {
		super(ctx);
		addTaskDoneListener(lis);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String id = params[0];
		logger.debug("instagram media id: " + id); //$NON-NLS-1$
		int index = id.indexOf("_"); //$NON-NLS-1$
		if (index != -1) {
			id = id.substring(0, index);
			logger.debug("instagram media id: " + id); //$NON-NLS-1$
		}

		boolean like = true;
		if (params.length > 1) {
			like = Boolean.parseBoolean(params[1]);
		}
		UPSApplication app = (UPSApplication) ((Activity) mContext)
				.getApplication();
		Instagram ig = InstagramHelper.getInstance().getAuthedInstagram(
				app.getInstagramAuthToken());
		try {
			if (like) {
				ig.setUserLike(Long.parseLong(id));
			} else {
				ig.deleteUserLike(Long.parseLong(id));
			}
		} catch (Exception e) {
			logger.error("Unable to like the instagram photo: " + id); //$NON-NLS-1$
			return false;
		}
		return true;
	}

}

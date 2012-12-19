/**
 * 
 */
package com.gmail.charleszq.ups.ui.command;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.task.flickr.FlickrLikeTask;
import com.gmail.charleszq.ups.task.ig.InstagramLikePhotoTask;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class LikePhotoCommand extends AbstractCommand<Boolean> {

	public LikePhotoCommand(Context context) {
		super(context);
	}

	/**
	 * params[0] should be a <code>MediaObject</code>, so I know which task to
	 * use.
	 */
	@Override
	public boolean execute(Object... params) {
		if (params.length == 0) {
			return false;
		}
		MediaObject photo = (MediaObject) params[0];
		
		UPSApplication app = (UPSApplication) ((Activity)mContext).getApplication();
		switch (photo.getMediaSource()) {
		case FLICKR:
			if( app.getFlickrUserId() == null ) {
				return false;
			}
			FlickrLikeTask task = new FlickrLikeTask(mContext,
					this.mTaskDoneListner);
			task.execute(photo.getId());
			break;
		case INSTAGRAM:
			if( app.getInstagramUserId() == null ) {
				return false;
			}
			InstagramLikePhotoTask igLikeTask = new InstagramLikePhotoTask(
					mContext, this.mTaskDoneListner);
			igLikeTask.execute(photo.getId());
			break;
		case PX500:
			if( app.getPx500UserId() == null ) {
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	public int getIconResourceId() {
		return 0;
	}

	@Override
	public String getLabel() {
		return null;
	}

}

/**
 * 
 */
package com.gmail.charleszq.ups.ui.command;

import android.content.Context;

import com.gmail.charleszq.ups.model.MediaObject;
import com.gmail.charleszq.ups.task.flickr.FlickrLikeTask;

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
		switch (photo.getMediaSource()) {
		case FLICKR:
			FlickrLikeTask task = new FlickrLikeTask(mContext, this.mTaskDoneListner);
			task.execute(photo.getId());
			break;
		case INSTAGRAM:
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

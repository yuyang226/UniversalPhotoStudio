/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.googlecode.flickrjandroid.groups.Group;

/**
 * @author Charles(charleszq@gmail.com)
 *
 */
public class FetchFlickrGroupIconUrlTask extends AbstractFetchIconUrlTask {
	
	private Group mPhotoGroup;

	/**
	 * @param ctx
	 */
	public FetchFlickrGroupIconUrlTask(Context ctx, Group g) {
		super(ctx);
		mPhotoGroup = g;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Object... params) {
		beforeExecute(params);
		logger.debug("Group buddy icon: " + mPhotoGroup.getBuddyIconUrl()); //$NON-NLS-1$
		return mPhotoGroup.getBuddyIconUrl();
	}

}

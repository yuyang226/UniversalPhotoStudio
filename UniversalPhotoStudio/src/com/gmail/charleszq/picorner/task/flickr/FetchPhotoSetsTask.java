/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.photosets.Photosets;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * Represents the task to fetch my flickr photo set.
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FetchPhotoSetsTask extends
		AbstractContextAwareTask<Void, Integer, Photosets> {

	public FetchPhotoSetsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Photosets doInBackground(Void... params) {
		String token = SPUtil.getFlickrAuthToken(mContext);
		String secret = SPUtil.getFlickrAuthTokenSecret(mContext);
		String userId = SPUtil.getFlickrUserId(mContext);
		PhotosetsInterface psi = FlickrHelper.getInstance()
				.getFlickrAuthed(token, secret).getPhotosetsInterface();
		try {
			Photosets ps = psi.getList(userId);
			return ps;
		} catch (Exception e) {
		}
		return null;
	}

}

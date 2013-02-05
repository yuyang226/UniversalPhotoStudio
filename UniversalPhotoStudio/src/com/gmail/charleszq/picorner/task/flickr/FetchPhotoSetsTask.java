/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.SharedPreferenceUtil;
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
		String token = SharedPreferenceUtil.getFlickrAuthToken(mContext);
		String secret = SharedPreferenceUtil.getFlickrAuthTokenSecret(mContext);
		String userId = SharedPreferenceUtil.getFlickrUserId(mContext);
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

/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.Collection;

import android.content.Context;

import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.galleries.GalleriesInterface;
import com.googlecode.flickrjandroid.galleries.Gallery;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FetchMyGalleriesTask extends
		AbstractContextAwareTask<Void, Integer, Collection<Gallery>> {

	public FetchMyGalleriesTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Collection<Gallery> doInBackground(Void... params) {
		String token = SPUtil.getFlickrAuthToken(mContext);
		String secret = SPUtil.getFlickrAuthTokenSecret(mContext);
		String userId = SPUtil.getFlickrUserId(mContext);
		GalleriesInterface gi = FlickrHelper.getInstance()
				.getFlickrAuthed(token, secret).getGalleriesInterface();
		try {
			return gi.getList(userId, -1, -1);
		} catch (Exception e) {
		}
		return null;
	}

}

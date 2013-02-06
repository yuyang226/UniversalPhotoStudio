/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.photosets.Photoset;
import com.googlecode.flickrjandroid.photosets.Photosets;
import com.googlecode.flickrjandroid.photosets.PhotosetsInterface;

/**
 * Represents the task to fetch my flickr photo set.
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FetchPhotoSetsTask extends
		AbstractContextAwareTask<Integer, Integer, List<Photoset>> {
	

	public FetchPhotoSetsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected List<Photoset> doInBackground(Integer... params) {
		int page = params[0];
		
		String token = SPUtil.getFlickrAuthToken(mContext);
		String secret = SPUtil.getFlickrAuthTokenSecret(mContext);
		String userId = SPUtil.getFlickrUserId(mContext);
		PhotosetsInterface psi = FlickrHelper.getInstance()
				.getFlickrAuthed(token, secret).getPhotosetsInterface();
		
		List<Photoset> sets = new ArrayList<Photoset>();
		try {
			Photosets ps = psi.getList(userId, IConstants.DEF_PHOTO_SET_GROUP_PAGE_SIZE, page );
			sets.addAll(ps.getPhotosets());
		} catch (Exception e) {
		}
		return sets;
	}

}

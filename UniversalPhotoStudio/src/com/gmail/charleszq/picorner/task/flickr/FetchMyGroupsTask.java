/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.Collection;

import android.content.Context;

import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.gmail.charleszq.picorner.utils.IConstants;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.groups.pools.PoolsInterface;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class FetchMyGroupsTask extends
		AbstractContextAwareTask<Integer, Integer, Collection<Group>> {

	public FetchMyGroupsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Collection<Group> doInBackground(Integer... params) {
		int page = params[0];
		String token = SPUtil.getFlickrAuthToken(mContext);
		String secret = SPUtil.getFlickrAuthTokenSecret(mContext);
		PoolsInterface psi = FlickrHelper.getInstance()
				.getFlickrAuthed(token, secret).getPoolsInterface();
		try {
			return psi.getGroups(IConstants.DEF_PHOTO_SET_GROUP_PAGE_SIZE, page);
		} catch (Exception e) {
		}
		return null;
	}

}

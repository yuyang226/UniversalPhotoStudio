/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import java.util.Collection;

import android.content.Context;

import com.gmail.charleszq.picorner.SharedPreferenceUtil;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.groups.Group;
import com.googlecode.flickrjandroid.groups.pools.PoolsInterface;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class FetchMyGroupsTask extends
		AbstractContextAwareTask<Void, Integer, Collection<Group>> {

	public FetchMyGroupsTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Collection<Group> doInBackground(Void... params) {
		String token = SharedPreferenceUtil.getFlickrAuthToken(mContext);
		String secret = SharedPreferenceUtil.getFlickrAuthTokenSecret(mContext);
		PoolsInterface psi = FlickrHelper.getInstance()
				.getFlickrAuthed(token, secret).getPoolsInterface();
		try {
			return psi.getGroups();
		} catch (Exception e) {
		}
		return null;
	}

}

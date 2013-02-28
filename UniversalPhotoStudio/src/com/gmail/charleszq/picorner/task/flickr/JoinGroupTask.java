/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.groups.GroupsInterface;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class JoinGroupTask extends
		AbstractContextAwareTask<String, Integer, String> {

	public JoinGroupTask(Context ctx) {
		super(ctx);
	}

	/**
	 * Returns 'succeed' message if success, otherwise returns the error message.
	 */
	@Override
	protected String doInBackground(String... params) {
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(mContext);
		GroupsInterface gi = f.getGroupsInterface();
		String groupId = params[0];
		try {
			gi.joinPublicGroup(groupId);
			return mContext.getString(R.string.msg_join_group_succeeded);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}

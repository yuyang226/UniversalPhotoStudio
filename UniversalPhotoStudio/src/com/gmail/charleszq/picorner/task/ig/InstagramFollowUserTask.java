/**
 * 
 */
package com.gmail.charleszq.picorner.task.ig;

import org.jinstagram.Instagram;
import org.jinstagram.model.Relationship;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.InstagramHelper;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramFollowUserTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public InstagramFollowUserTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String targetUserId = params[0];
		boolean follow = Boolean.parseBoolean(params[1]);
		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		Instagram ig = InstagramHelper.getInstance().getAuthedInstagram(
				app.getInstagramAuthToken());
		try {
			ig.setUserRelationship(Long.parseLong(targetUserId),
					follow ? Relationship.FOLLOW : Relationship.UNFOLLOW);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}

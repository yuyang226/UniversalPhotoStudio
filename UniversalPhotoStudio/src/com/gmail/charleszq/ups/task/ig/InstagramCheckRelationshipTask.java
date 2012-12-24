/**
 * 
 */
package com.gmail.charleszq.ups.task.ig;

import org.jinstagram.Instagram;
import org.jinstagram.entity.relationships.RelationshipFeed;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.task.AbstractContextAwareTask;
import com.gmail.charleszq.ups.utils.InstagramHelper;

/**
 * Represents the task to get the relationship with a given user by id, returns
 * <code>true</code> if I'm following him/her.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class InstagramCheckRelationshipTask extends
		AbstractContextAwareTask<String, Void, Boolean> {

	public InstagramCheckRelationshipTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String targetUserId = params[0];
		UPSApplication app = (UPSApplication) ((Activity) mContext)
				.getApplication();
		Instagram ig = InstagramHelper.getInstance().getAuthedInstagram(
				app.getInstagramAuthToken());
		try {
			RelationshipFeed rfeed = ig.getUserRelationship(Long
					.parseLong(targetUserId));
			if (rfeed != null) {
				Log.d(getClass().getName(),
						"outgoing status: " + rfeed.getData().getOutgoingStatus()); //$NON-NLS-1$
				return "follows".equals(rfeed.getData().getOutgoingStatus()); //$NON-NLS-1$
			}
		} catch (Exception e) {
		}
		return false;
	}

}

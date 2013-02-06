/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import android.content.Context;
import android.util.Log;

import com.github.yuyang226.j500px.J500px;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.J500pxHelper;

/**
 * @author charleszq
 *
 */
public class PxVotePhotoTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public PxVotePhotoTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String photoId = params[0];
		J500px px = J500pxHelper.getJ500pxAuthedInstance(mContext);
		try {
			px.getPhotosInterface().votePhoto(Integer.parseInt(photoId), true);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}
}

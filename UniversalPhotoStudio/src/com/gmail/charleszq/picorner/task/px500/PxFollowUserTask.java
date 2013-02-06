/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import android.content.Context;

import com.github.yuyang226.j500px.J500px;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.J500pxHelper;

/**
 * @author charleszq
 *
 */
public class PxFollowUserTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public PxFollowUserTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String userId = params[0];
		boolean follow = true;
		if( params.length > 1 ) {
			follow = Boolean.parseBoolean(params[1]);
		}
		
		J500px px = J500pxHelper.getJ500pxAuthedInstance(mContext);
		try {
			px.getUsersInterface().followUser(Integer.parseInt(userId), follow);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}

/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import android.app.Activity;
import android.content.Context;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.users.User;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.msg.Message;
import com.gmail.charleszq.picorner.msg.MessageBus;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.J500pxHelper;

/**
 * @author charleszq
 * 
 */
public class PxFetchUserProfileTask extends
		AbstractContextAwareTask<String, Integer, User> {

	private boolean mIsMyProfile = true;

	public PxFetchUserProfileTask(Context ctx) {
		super(ctx);
	}

	/**
	 * if no params, means geting my own user profile; otherwise, get the user
	 * profile of the given user id.
	 */
	@Override
	protected User doInBackground(String... params) {
		J500px px = J500pxHelper.getJ500pxAuthedInstance(mContext);
		try {
			if (params.length > 0) {
				mIsMyProfile = false;
				return px.getUsersInterface().getUserProfile(
						Integer.parseInt(params[0]), null, null);
			} else {
				mIsMyProfile = true;
				return px.getUsersInterface().getUserProfile();
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected void onPostExecute(User result) {
		if (mIsMyProfile) {
			PicornerApplication app = (PicornerApplication) ((Activity) mContext)
					.getApplication();
			app.savePxUserProfile(String.valueOf(result.getId()),
					result.getUserName(), result.getUserPicUrl());
			MessageBus.broadcastMessage(Message.PUBLIC_USER_LOGIN_MSG);
		}
		super.onPostExecute(result);
	}

}

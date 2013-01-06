/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import android.app.Activity;
import android.content.Context;

import com.github.yuyang226.j500px.J500px;
import com.github.yuyang226.j500px.users.User;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.J500pxHelper;

/**
 * @author charleszq
 *
 */
public class PxFetchUserProfileTask extends
		AbstractContextAwareTask<Void, Integer, User> {

	public PxFetchUserProfileTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected User doInBackground(Void... params) {
		PicornerApplication app = (PicornerApplication) ((Activity)mContext).getApplication();
		String token = app.getPx500OauthToken();
		String secret = app.getPx500OauthTokenSecret();
		J500px px = J500pxHelper.getJ500pxAuthedInstance(token, secret);
		try {
			return px.getUsersInterface().getUserProfile();
		} catch (Exception e) {
			return null;
		}
	}

}

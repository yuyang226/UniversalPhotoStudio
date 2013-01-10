/**
 * 
 */
package com.gmail.charleszq.picorner.task.px500;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.github.yuyang226.j500px.J500px;
import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.J500pxHelper;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class PxLikePhotoTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public PxLikePhotoTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String photoId = params[0];
		boolean like = true;
		if (params.length > 1) {
			like = Boolean.parseBoolean(params[1]);
		}
		Log.d(TAG, like ? "fav a photo" : "unfav a photo");  //$NON-NLS-1$//$NON-NLS-2$

		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		J500px px = J500pxHelper.getJ500pxAuthedInstance(
				app.getPx500OauthToken(), app.getPx500OauthTokenSecret());
		try {
			px.getPhotosInterface().likePhoto(Integer.parseInt(photoId), like);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}

}

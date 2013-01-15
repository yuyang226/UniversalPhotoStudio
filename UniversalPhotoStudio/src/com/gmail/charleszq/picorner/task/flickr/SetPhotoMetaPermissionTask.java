/**
 * 
 */
package com.gmail.charleszq.picorner.task.flickr;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.task.AbstractContextAwareTask;
import com.gmail.charleszq.picorner.utils.FlickrHelper;
import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.photos.Permissions;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class SetPhotoMetaPermissionTask extends
		AbstractContextAwareTask<String, Integer, Boolean> {

	public SetPhotoMetaPermissionTask(Context ctx) {
		super(ctx);
	}

	@Override
	protected Boolean doInBackground(String... params) {
		String photoId = params[0];
		String title = params[1];
		String desc = params[2];

		PicornerApplication app = (PicornerApplication) ((Activity) mContext)
				.getApplication();
		Flickr f = FlickrHelper.getInstance().getFlickrAuthed(
				app.getFlickrToken(), app.getFlickrTokenSecret());

		boolean success = true;
		// set photo meta
		try {
			f.getPhotosInterface().setMeta(photoId, title, desc);
		} catch (Exception e) {
			success = false;
			Log.w(TAG, "failed to set photo meta info: " + e.getMessage()); //$NON-NLS-1$
		}

		// try set permission
		if (params.length == 6) {
			boolean isFriend = Boolean.parseBoolean(params[3]);
			boolean isFamily = Boolean.parseBoolean(params[4]);
			boolean isPublic = Boolean.parseBoolean(params[5]);
			f = FlickrHelper.getInstance().getFlickrAuthed(
					app.getFlickrToken(), app.getFlickrTokenSecret());
			Permissions permissions = new Permissions();
			permissions.setFamilyFlag(isFamily);
			permissions.setPublicFlag(isPublic);
			permissions.setFriendFlag(isFriend);
			try {
				f.getPhotosInterface().setPerms(photoId, permissions);
			} catch (Exception e) {
				success = false;
				Log.w(TAG, "failed to set photo visibility: " + e.getMessage()); //$NON-NLS-1$

			}
		}

		return success;
	}

}

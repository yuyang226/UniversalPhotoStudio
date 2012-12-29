/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.Author;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrUserPhotoStreamService;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class FlickrUserPhotosCommand extends MyFlickrPhotosCommand {

	private Author mUser;

	/**
	 * @param context
	 */
	public FlickrUserPhotosCommand(Context context, Author userId) {
		super(context);
		this.mUser = userId;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				PicornerApplication app = (PicornerApplication) act
						.getApplication();
				mCurrentPhotoService = new FlickrUserPhotoStreamService(
						mUser.getUserId(), app.getFlickrToken(),
						app.getFlickrTokenSecret());
			}
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		String s = mContext.getString(R.string.cd_flickr_user_photos);
		return String.format(s, mUser.getUserName() == null ? mUser.getUserId()
				: mUser.getUserName());
	}

}

/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.flickr.FlickrUserPhotoStreamService;

import android.app.Activity;
import android.content.Context;

/**
 * @author charles(charleszq@gmail.com)
 *
 */
public class FlickrUserPhotosCommand extends MyFlickrPhotosCommand {
	
	private String mUserId ;
	
	/**
	 * @param context
	 */
	public FlickrUserPhotosCommand(Context context, String userId) {
		super(context);
		this.mUserId = userId;
	}
	
	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				UPSApplication app = (UPSApplication) act.getApplication();
				mCurrentPhotoService = new FlickrUserPhotoStreamService(
						mUserId, app.getFlickrToken(),
						app.getFlickrTokenSecret());
			}
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}

}

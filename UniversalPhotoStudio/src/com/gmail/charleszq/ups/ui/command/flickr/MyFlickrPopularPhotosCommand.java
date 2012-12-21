/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.flickr.FlickrMyPopularPhotosService;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class MyFlickrPopularPhotosCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public MyFlickrPopularPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_popular;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_my_popular);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				UPSApplication app = (UPSApplication) act.getApplication();
				mCurrentPhotoService = new FlickrMyPopularPhotosService(
						app.getFlickrUserId(), app.getFlickrToken(),
						app.getFlickrTokenSecret());
			}
			return mCurrentPhotoService;
		}
		if (adapterClass == Integer.class) {
			return 100; // the maximum value of flickr service.
		}
		return super.getAdapter(adapterClass);
	}

}

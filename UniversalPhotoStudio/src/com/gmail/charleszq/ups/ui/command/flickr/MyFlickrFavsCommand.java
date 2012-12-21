/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.flickr.FlickrMyFavoritesService;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class MyFlickrFavsCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public MyFlickrFavsCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_my_favorite;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getResources().getString(R.string.f_my_fav);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				UPSApplication app = (UPSApplication) act.getApplication();
				mCurrentPhotoService = new FlickrMyFavoritesService(
						app.getFlickrUserId(), app.getFlickrToken(),
						app.getFlickrTokenSecret());
			}
			return mCurrentPhotoService;
		}
		if (adapterClass == Integer.class) {
			return 50; // the max required by flickr server.
		}
		return super.getAdapter(adapterClass);
	}

}

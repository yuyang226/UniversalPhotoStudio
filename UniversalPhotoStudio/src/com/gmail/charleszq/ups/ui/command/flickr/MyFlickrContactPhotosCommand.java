/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.R;
import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.flickr.FlickrContactPhotosService;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class MyFlickrContactPhotosCommand extends PhotoListCommand {

	/**
	 * @param context
	 */
	public MyFlickrContactPhotosCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_contacts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getResources().getString(R.string.f_my_contacts);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == Boolean.class) {
			return false;
		}
		if( adapterClass == IPhotoService.class ) {
			Activity act = (Activity) mContext;
			UPSApplication app = (UPSApplication) act.getApplication();
			IPhotoService service = new FlickrContactPhotosService(
					app.getFlickrUserId(), app.getFlickrToken(),
					app.getFlickrTokenSecret());
			return service;
		}
		return super.getAdapter(adapterClass);
	}

}

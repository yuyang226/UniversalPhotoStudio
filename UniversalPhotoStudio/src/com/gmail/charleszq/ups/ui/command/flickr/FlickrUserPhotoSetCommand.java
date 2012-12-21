/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.flickr.FlickrPhotoSetPhotosService;
import com.gmail.charleszq.ups.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.ups.task.flickr.FetchFlickrPhotosetIconUrlTask;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.googlecode.flickrjandroid.photosets.Photoset;

/**
 * Represents the command to fetch all photos from a given user photo set,
 * group, or a gallery.
 * 
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrUserPhotoSetCommand extends PhotoListCommand {

	private Photoset mPhotoSet;

	/**
	 * @param context
	 */
	public FlickrUserPhotoSetCommand(Context context, Photoset ps) {
		super(context);
		mPhotoSet = ps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.ups.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mPhotoSet == null ? "PHOTOSET NOT RIGHT" : mPhotoSet.getTitle(); //$NON-NLS-1$
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			return new FetchFlickrPhotosetIconUrlTask(mContext, mPhotoSet);
		}
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				UPSApplication app = (UPSApplication) act.getApplication();
				mCurrentPhotoService = new FlickrPhotoSetPhotosService(
						app.getFlickrUserId(), app.getFlickrToken(),
						app.getFlickrTokenSecret(), mPhotoSet);
			}
			return mCurrentPhotoService;
		}
		return super.getAdapter(adapterClass);
	}

}

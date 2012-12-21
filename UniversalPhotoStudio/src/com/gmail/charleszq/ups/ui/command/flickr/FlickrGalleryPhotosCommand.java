/**
 * 
 */
package com.gmail.charleszq.ups.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.ups.UPSApplication;
import com.gmail.charleszq.ups.service.IPhotoService;
import com.gmail.charleszq.ups.service.flickr.FlickrGalleryPhotosService;
import com.gmail.charleszq.ups.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.ups.task.flickr.FetchFlickrGalleryIconUrlTask;
import com.gmail.charleszq.ups.ui.command.PhotoListCommand;
import com.googlecode.flickrjandroid.galleries.Gallery;

/**
 * @author Charles(charleszq@gmail.com)
 * 
 */
public class FlickrGalleryPhotosCommand extends PhotoListCommand {

	private Gallery mGallery;

	/**
	 * @param context
	 */
	public FlickrGalleryPhotosCommand(Context context, Gallery g) {
		super(context);
		this.mGallery = g;
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
		return mGallery.getTitle();
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				UPSApplication app = (UPSApplication) act.getApplication();
				mCurrentPhotoService = new FlickrGalleryPhotosService(
						app.getFlickrUserId(), app.getFlickrToken(),
						app.getFlickrTokenSecret(), mGallery.getGalleryId());
			}
			return mCurrentPhotoService;
		}
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			return new FetchFlickrGalleryIconUrlTask(mContext, mGallery);
		}
		return super.getAdapter(adapterClass);
	}

}

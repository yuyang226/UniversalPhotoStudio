/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrGalleryPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrGalleryIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
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

	@Override
	public int getIconResourceId() {
		return -1;
	}

	@Override
	public String getLabel() {
		return mGallery.getTitle();
	}

	@Override
	public String getDescription() {
		String s = mContext.getString(R.string.cd_flickr_gallery_photos);
		return String.format(s, getLabel());
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				PicornerApplication app = (PicornerApplication) act
						.getApplication();
				mCurrentPhotoService = new FlickrGalleryPhotosService(
						app.getFlickrUserId(), app.getFlickrToken(),
						app.getFlickrTokenSecret(), mGallery.getGalleryId());
			}
			return mCurrentPhotoService;
		}
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			return new FetchFlickrGalleryIconUrlTask(mContext, mGallery);
		}
		if( adapterClass == FlickrUserPhotoPool.class ) {
			return mGallery.getGalleryId();
		}
		return super.getAdapter(adapterClass);
	}

}

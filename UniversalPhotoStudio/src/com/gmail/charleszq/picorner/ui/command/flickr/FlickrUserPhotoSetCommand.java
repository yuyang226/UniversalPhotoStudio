/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import android.app.Activity;
import android.content.Context;

import com.gmail.charleszq.picorner.PicornerApplication;
import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.model.FlickrUserPhotoPool;
import com.gmail.charleszq.picorner.offline.FlickrOfflineParameter;
import com.gmail.charleszq.picorner.offline.IOfflineViewParameter;
import com.gmail.charleszq.picorner.offline.OfflinePhotoCollectionType;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrPhotoSetPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrPhotosetIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.googlecode.flickrjandroid.photos.PhotoPlace;
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
	private IOfflineViewParameter mOfflineParameter;

	/**
	 * @param context
	 */
	public FlickrUserPhotoSetCommand(Context context, Photoset ps) {
		super(context);
		mPhotoSet = ps;
		mOfflineParameter = new FlickrOfflineParameter(
				OfflinePhotoCollectionType.PHOTO_SET, ps.getId());
	}

	@Override
	public int getIconResourceId() {
		return -1;
	}

	@Override
	public String getLabel() {
		return mPhotoSet.getTitle();
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			return new FetchFlickrPhotosetIconUrlTask(mContext, mPhotoSet);
		}
		if (adapterClass == IPhotoService.class) {
			if (mCurrentPhotoService == null) {
				Activity act = (Activity) mContext;
				PicornerApplication app = (PicornerApplication) act
						.getApplication();
				mCurrentPhotoService = new FlickrPhotoSetPhotosService(
						app.getFlickrUserId(), app.getFlickrToken(),
						app.getFlickrTokenSecret(), mPhotoSet);
			}
			return mCurrentPhotoService;
		}
		if (adapterClass == FlickrUserPhotoPool.class) {
			return PhotoPlace.SET + mPhotoSet.getId();
		}
		if (adapterClass == IOfflineViewParameter.class) {
			return mOfflineParameter;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		String s = mContext.getString(R.string.cd_flickr_photo_set_photos);
		return String.format(s, mPhotoSet.getTitle());
	}

}

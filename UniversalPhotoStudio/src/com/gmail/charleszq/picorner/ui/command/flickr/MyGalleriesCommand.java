/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import java.util.Comparator;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SPUtil;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrGalleryPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrGalleryIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.flickr.MyPhotoGalleriesHiddenView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.googlecode.flickrjandroid.galleries.Gallery;

/**
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyGalleriesCommand extends PhotoListCommand {

	private Gallery mGallery;
	private IHiddenView mHiddenView;

	/**
	 * @param context
	 */
	public MyGalleriesCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		return R.drawable.ic_action_flickr_gallery;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_my_galleries);
	}
	
	@Override
	public boolean execute(Object... params) {
		mGallery = (Gallery) params[0];
		return super.execute();
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new MyPhotoGalleriesHiddenView();
			}
			return mHiddenView;
		}
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			FetchFlickrGalleryIconUrlTask task = new FetchFlickrGalleryIconUrlTask(
					mContext);
			return task;
		}
		if (adapterClass == IPhotoService.class) {
			mCurrentPhotoService = new FlickrGalleryPhotosService(
					SPUtil.getFlickrUserId(mContext),
					SPUtil.getFlickrAuthToken(mContext),
					SPUtil.getFlickrAuthTokenSecret(mContext),
					mGallery.getGalleryId());
			return mCurrentPhotoService;
		}
		if (adapterClass == Comparator.class) {
			return mGallery;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public String getDescription() {
		String msg = mContext.getString(R.string.cd_flickr_gallery_photos);
		return String.format(msg, mGallery != null ? mGallery.getTitle() : ""); //$NON-NLS-1$
	}


}

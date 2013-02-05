/**
 * 
 */
package com.gmail.charleszq.picorner.ui.command.flickr;

import java.util.Comparator;

import android.content.Context;

import com.gmail.charleszq.picorner.R;
import com.gmail.charleszq.picorner.SharedPreferenceUtil;
import com.gmail.charleszq.picorner.service.IPhotoService;
import com.gmail.charleszq.picorner.service.flickr.FlickrPhotoSetPhotosService;
import com.gmail.charleszq.picorner.task.AbstractFetchIconUrlTask;
import com.gmail.charleszq.picorner.task.flickr.FetchFlickrPhotosetIconUrlTask;
import com.gmail.charleszq.picorner.ui.command.PhotoListCommand;
import com.gmail.charleszq.picorner.ui.flickr.MyPhotoSetsHiddenView;
import com.gmail.charleszq.picorner.ui.helper.IHiddenView;
import com.googlecode.flickrjandroid.photosets.Photoset;

/**
 * Represents the command to show all my photo sets in a menu item back view.
 * 
 * @author charles(charleszq@gmail.com)
 * 
 */
public class MyPhotosetsCommand extends PhotoListCommand {

	private IHiddenView mHiddenView;
	private Photoset mPhotoSet;

	/**
	 * @param context
	 */
	public MyPhotosetsCommand(Context context) {
		super(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getIconResourceId()
	 */
	@Override
	public int getIconResourceId() {
		// TODO icon
		return R.drawable.ic_action_flickr_my_favourites;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.gmail.charleszq.picorner.ui.command.ICommand#getLabel()
	 */
	@Override
	public String getLabel() {
		return mContext.getString(R.string.f_my_photo_sets);
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (adapterClass == IHiddenView.class) {
			if (mHiddenView == null) {
				mHiddenView = new MyPhotoSetsHiddenView();
			}
			return mHiddenView;
		}
		if (adapterClass == AbstractFetchIconUrlTask.class) {
			FetchFlickrPhotosetIconUrlTask task = new FetchFlickrPhotosetIconUrlTask(
					mContext);
			return task;
		}
		if (adapterClass == IPhotoService.class) {
			mCurrentPhotoService = new FlickrPhotoSetPhotosService(
					SharedPreferenceUtil.getFlickrUserId(mContext),
					SharedPreferenceUtil.getFlickrAuthToken(mContext),
					SharedPreferenceUtil.getFlickrAuthTokenSecret(mContext),
					mPhotoSet);
			return mCurrentPhotoService;
		}
		if (adapterClass == Comparator.class) {
			return mPhotoSet;
		}
		return super.getAdapter(adapterClass);
	}

	@Override
	public boolean execute(Object... params) {
		this.mPhotoSet = (Photoset) params[0];
		return super.execute();
	}

	@Override
	public String getDescription() {
		String msg = mContext.getString(R.string.cd_flickr_photo_set_photos);
		return String
				.format(msg, mPhotoSet != null ? mPhotoSet.getTitle() : ""); //$NON-NLS-1$
	}

}
